package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Product儲藏庫
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByProductName(String productName);

    List<Product> findByCategory(Category category);

    Page<Product> findByProductNameLikeIgnoreCase(String productName, Pageable pageDetails);

    Page<Product> findByCategoryOrderByProductPriceAsc(Category category, Pageable pageDetails);
}
