package com.ecommerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Category DTO 防止實體曝露
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//Request
//單筆資料 Data Transfer Object
public class CategoryDTO {
    private Long CategoryId;

    @NotBlank
    @Size(min = 5,message = "長度至少大於5")
    private String CategoryName;
}
