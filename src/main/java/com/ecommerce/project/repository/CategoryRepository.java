package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Category儲藏庫
 */
//不使用CrudRepository原因為JpaRepository提供更多功能,包含了CrudRepository所有功能
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryName(@NotBlank @Size(min = 5,message = "長度至少大於5") String categoryName);
}
