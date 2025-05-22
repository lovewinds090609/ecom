package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定義API錯誤回傳的內容
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse {
    String message;
    boolean status;
}
