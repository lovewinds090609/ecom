package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 類別控制器
 */
@RestController
//設定此Controller下api的統一前綴為/api開頭
@RequestMapping("/api")
public class CategoryController {

    //@Autowired 不推薦使用field注入
    private CategoryService categoryService;

    //會自動autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    /**
     * 獲取所有類別
     * @param pageNumber 目前第幾頁
     * @param pageSize 一頁顯示幾筆資料
     * @param sortBy 根據什麼變數進行排序
     * @param sortOrder 升序or降序
     * @return ResponseEntity
     */
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getCategories(
            @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_CATEGORIES_BY) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_DIR) String sortOrder)
    {
        CategoryResponse categories = categoryService.getCategories(pageNumber, pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * 創建新的類別
     * @param categoryDTO 傳入DTO,避免曝露Entity,增加安全性
     * @return ResponseEntity
     */
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    //ResponseEntity 主要功能
    //返回 HTTP 狀態碼（如 200 OK, 404 NOT FOUND, 500 INTERNAL SERVER ERROR）。
    //自定義 HTTP 標頭（Headers）（如 Content-Type, Cache-Control）。
    //返回響應主體（Body），可以是 JSON、XML、字符串、數據流等格式。
    //ResponseEntity<T> 的 T 取決於你想返回的數據類型

    /**
     * 刪除存在的類別
     * @param categoryId 傳入欲刪除類別的Primary Key
     * @return ResponseEntity
     */
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        //嘗試刪除資料
//        try {
//            String status = categoryService.deleteCategory(categoryId);
//            //以下為使用不同方法回傳
//            return new ResponseEntity<>(status, HttpStatus.OK); //最常見
//            //return ResponseEntity.ok(status);
//            //return ResponseEntity.status(HttpStatus.OK).body(status);
//        } catch (ResponseStatusException e) {
//            //如果資料不存在則處理拋出的錯誤
//            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
//        }
        CategoryDTO deletedCategoryDTO = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK); //最常見
    }

    /**
     * 更新現有的類別
     * @param categoryDTO 傳入修改後的DTO,避免曝露Entity,增加安全性
     * @param categoryId 傳入欲更改類別的Primary Key
     * @return ResponseEntity
     */
    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {
//        try {
//            Category savedCategory = categoryService.updateCategory(category,categoryId);
//            return new ResponseEntity<>("Category updated wtih id: " + categoryId, HttpStatus.OK);
//        } catch (ResponseStatusException e) {
//            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
//        }
        CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO,categoryId);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
    }
}

