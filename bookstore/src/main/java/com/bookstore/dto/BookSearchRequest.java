package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchRequest {
    private String keyword;
    private Long categoryId;
    private String author;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minRating;
    private Boolean inStock;
    private Boolean featured;
    private int page = 0;
    private int size = 12;
    private String sortBy = "title";
    private String sortDirection = "asc";
}