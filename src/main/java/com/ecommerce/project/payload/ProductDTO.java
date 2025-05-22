package com.ecommerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Product DTO 防止實體曝露
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;

    @NotBlank(message = "不可為空白")
    @Size(min = 5,message = "長度必需為5或以上")
    private String productName;

    private String image;

    @NotBlank(message = "不可為空白")
    @Size(min = 5,message = "長度必需為5或以上")
    private String description;

    private int productQuantity;

    private double productPrice;

    private double discount;

    private double productSpecialPrice;
}
