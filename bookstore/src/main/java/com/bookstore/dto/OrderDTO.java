package com.bookstore.dto;

import com.bookstore.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private String shippingAddress;
    private String orderNumber;
    private List<OrderItemDTO> orderItems;
    private Integer totalItems;
}
