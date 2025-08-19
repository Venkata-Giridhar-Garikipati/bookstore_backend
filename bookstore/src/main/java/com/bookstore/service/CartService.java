package com.bookstore.service;

import com.bookstore.dto.AddToCartRequest;
import com.bookstore.dto.CartDTO;
import com.bookstore.dto.CartItemDTO;
import com.bookstore.entity.*;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.exception.InsufficientStockException;
import com.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartDTO getCartByUser() {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserIdWithItems(currentUser.getId())
                .orElseGet(() -> createNewCart(currentUser));
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO addToCart(AddToCartRequest request) {
        User currentUser = getCurrentUser();
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

        // Check initial stock availability
        if (book.getStock() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock. Available: " + book.getStock());
        }

        Cart cart = cartRepository.findByUserIdWithItems(currentUser.getId())
                .orElseGet(() -> createNewCart(currentUser));

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId())
                .orElse(null);

        if (existingItem != null) {
            // Update quantity - check total requested quantity against stock
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (book.getStock() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock. Available: " + book.getStock() +
                        ", requested total: " + newQuantity);
            }
            existingItem.setQuantity(newQuantity);
            existingItem.calculatePrice();
            cartItemRepository.save(existingItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setBook(book);
            cartItem.setQuantity(request.getQuantity());
            cartItem.calculatePrice();
            cartItemRepository.save(cartItem);

            // Add to cart's item list if it's loaded
            if (cart.getCartItems() != null) {
                cart.getCartItems().add(cartItem);
            }
        }

        updateCartTotal(cart);
        // Refresh cart to get updated data
        cart = cartRepository.findByUserIdWithItems(currentUser.getId()).orElse(cart);
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO updateCartItem(Long cartItemId, Integer quantity) {
        User currentUser = getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Security check - ensure user owns this cart item
        if (!cartItem.getCart().getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        // Validate quantity is positive
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // Check stock availability
        if (cartItem.getBook().getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock. Available: " + cartItem.getBook().getStock());
        }

        cartItem.setQuantity(quantity);
        cartItem.calculatePrice();
        cartItemRepository.save(cartItem);

        updateCartTotal(cartItem.getCart());

        // Refresh cart to get updated data
        Cart updatedCart = cartRepository.findByUserIdWithItems(currentUser.getId()).orElse(cartItem.getCart());
        return convertToDTO(updatedCart);
    }

    @Transactional
    public CartDTO removeFromCart(Long cartItemId) {
        User currentUser = getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Security check - ensure user owns this cart item
        if (!cartItem.getCart().getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        Cart cart = cartItem.getCart();

        // Remove from cart's item list if it's loaded
        if (cart.getCartItems() != null) {
            cart.getCartItems().remove(cartItem);
        }

        cartItemRepository.delete(cartItem);
        updateCartTotal(cart);

        // Refresh cart to get updated data
        cart = cartRepository.findByUserIdWithItems(currentUser.getId()).orElse(cart);
        return convertToDTO(cart);
    }

    @Transactional
    public void clearCart() {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cartItemRepository.deleteByCartId(cart.getId());
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        BigDecimal total = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(total);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> cartItemDTOs = cart.getCartItems() != null ?
                cart.getCartItems().stream()
                        .map(this::convertCartItemToDTO)
                        .collect(Collectors.toList()) : List.of();

        Integer totalItems = cartItemDTOs.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        return new CartDTO(
                cart.getId(),
                cart.getUser().getId(),
                cart.getUser().getUsername(),
                cart.getTotalPrice(),
                cart.getCreatedAt(),
                cart.getUpdatedAt(),
                cartItemDTOs,
                totalItems
        );
    }

    private CartItemDTO convertCartItemToDTO(CartItem cartItem) {
        Book book = cartItem.getBook();
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getCart().getId(),
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getImageUrl(),
                book.getPrice(),
                book.getStock(),
                cartItem.getQuantity(),
                cartItem.getPrice()
        );
    }
}