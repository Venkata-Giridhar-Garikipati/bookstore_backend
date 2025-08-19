package com.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long cartId;

    @NotNull(message = "Book ID is required")
    private Long bookId;

    private String bookTitle;
    private String bookAuthor;
    private String bookImageUrl;
    private BigDecimal bookPrice;
    private Integer bookStock;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal price;
}
