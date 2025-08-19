// PlaceOrderRequest.java - Enhanced DTO
package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {

    @NotBlank(message = "Shipping address is required")
    @Size(max = 1000, message = "Shipping address cannot exceed 1000 characters")
    private String shippingAddress;
}
