// OrderService.java - Fixed version
package com.bookstore.service;

import com.bookstore.dto.OrderDTO;
import com.bookstore.dto.OrderItemDTO;
import com.bookstore.dto.PlaceOrderRequest;
import com.bookstore.entity.*;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.exception.InsufficientStockException;
import com.bookstore.exception.EmptyCartException;
import com.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderDTO placeOrder(PlaceOrderRequest request) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserIdWithItems(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Cannot place order with empty cart");
        }

        // Validate stock availability for all items first
        for (CartItem cartItem : cart.getCartItems()) {
            Book book = bookRepository.findById(cartItem.getBook().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

            if (book.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for book: " + book.getTitle() +
                                ". Available: " + book.getStock() + ", Requested: " + cartItem.getQuantity()
                );
            }
        }

        // Create order
        Order order = new Order();
        order.setUser(currentUser);
        order.setTotalPrice(cart.getTotalPrice());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        // Create order items and update stock
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getBook().getPrice());
            orderItem.calculatePrice();
            orderItem = orderItemRepository.save(orderItem);
            orderItems.add(orderItem);

            // Update book stock
            Book book = cartItem.getBook();
            book.setStock(book.getStock() - cartItem.getQuantity());
            bookRepository.save(book);
        }

        // Set order items to the order
        order.setOrderItems(orderItems);

        // Clear cart
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // Return fresh order with all items loaded
        Order savedOrder = orderRepository.findByIdWithItems(order.getId())
                .orElse(order);
        return convertToDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrders() {
        User currentUser = getCurrentUser();
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(currentUser.getId());
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Check if user owns the order or is admin
        if (!order.getUser().getId().equals(currentUser.getId()) &&
                !hasAdminRole(currentUser)) {
            throw new ResourceNotFoundException("Order not found");
        }

        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        User currentUser = getCurrentUser();
        if (!hasAdminRole(currentUser)) {
            throw new ResourceNotFoundException("Access denied");
        }

        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        User currentUser = getCurrentUser();
        if (!hasAdminRole(currentUser)) {
            throw new ResourceNotFoundException("Access denied");
        }

        List<Order> orders = orderRepository.findByStatusOrderByOrderDateDesc(status);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        User currentUser = getCurrentUser();
        if (!hasAdminRole(currentUser)) {
            throw new ResourceNotFoundException("Access denied");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        order = orderRepository.save(order);

        // Handle stock restoration if order is cancelled
        if (status == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));

        User currentUser = getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId()) &&
                !hasAdminRole(currentUser)) {
            throw new ResourceNotFoundException("Order not found");
        }

        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public boolean hasUserPurchasedBook(Long bookId) {
        User currentUser = getCurrentUser();
        return orderRepository.existsByUserIdAndOrderItemsBookId(currentUser.getId(), bookId);
    }

    private void restoreStock(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithBooks(order.getId());
        for (OrderItem orderItem : orderItems) {
            Book book = orderItem.getBook();
            book.setStock(book.getStock() + orderItem.getQuantity());
            bookRepository.save(book);
        }
    }

    private boolean hasAdminRole(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> orderItemDTOs = order.getOrderItems() != null ?
                order.getOrderItems().stream()
                        .map(this::convertOrderItemToDTO)
                        .collect(Collectors.toList()) : List.of();

        Integer totalItems = orderItemDTOs.stream()
                .mapToInt(OrderItemDTO::getQuantity)
                .sum();

        return new OrderDTO(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                order.getUser().getEmail(),
                order.getTotalPrice(),
                order.getOrderDate(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getOrderNumber(),
                orderItemDTOs,
                totalItems
        );
    }

    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        Book book = orderItem.getBook();
        return new OrderItemDTO(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getImageUrl(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getPrice()
        );
    }
}
