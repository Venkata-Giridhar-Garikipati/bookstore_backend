package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsDTO {
    private Long totalBooks;
    private Long totalUsers;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long pendingOrders;
    private Long lowStockBooks;
//    private List<TopBookDTO> topSellingBooks;
    private Map<String, Long> ordersByStatus;
    private Map<String, BigDecimal> revenueByMonth;
//    private List<CategorySalesDTO> categorySales;
}
