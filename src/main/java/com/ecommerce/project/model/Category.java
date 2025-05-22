package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 類別model
 */
@Entity( name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    //IDENTITY → 適合 MySQL / SQL Server
    //SEQUENCE → 適合 PostgreSQL / Oracle（效能高）
    //TABLE → 兼容所有資料庫，但效能較低
    //AUTO → JPA 自動選擇最佳策略
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long categoryId;

    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}
