package com.bookstore.service;

import com.bookstore.dto.DashboardAnalyticsDTO;
import com.bookstore.entity.OrderStatus;
import com.bookstore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public DashboardAnalyticsDTO getDashboardAnalytics() {
        DashboardAnalyticsDTO analytics = new DashboardAnalyticsDTO();

        // Basic counts
        analytics.setTotalBooks(bookRepository.count());
        analytics.setTotalUsers(userRepository.count());
        analytics.setTotalOrders(orderRepository.count());

        // Revenue calculation
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(order -> !order.getStatus().equals(OrderStatus.CANCELLED))
                .map(order -> order.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        analytics.setTotalRevenue(totalRevenue);

        // Pending orders
        analytics.setPendingOrders(orderRepository.countByStatus(OrderStatus.PENDING));

        // Low stock books (less than 10)
        analytics.setLowStockBooks(bookRepository.countByStockLessThan(10));

        // Orders by status
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            ordersByStatus.put(status.name(), orderRepository.countByStatus(status));
        }
        analytics.setOrdersByStatus(ordersByStatus);

        // Revenue by month (last 6 months)
        Map<String, BigDecimal> revenueByMonth = calculateRevenueByMonth();
        analytics.setRevenueByMonth(revenueByMonth);

        return analytics;
    }

    private Map<String, BigDecimal> calculateRevenueByMonth() {
        Map<String, BigDecimal> revenueByMonth = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);

            BigDecimal revenue = orderRepository.findOrdersBetweenDates(start, end)
                    .stream()
                    .filter(order -> !order.getStatus().equals(OrderStatus.CANCELLED))
                    .map(order -> order.getTotalPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            revenueByMonth.put(month.toString(), revenue);
        }

        return revenueByMonth;
    }
}
