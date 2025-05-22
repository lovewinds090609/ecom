package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 實作Category服務
 */
@Service
public class CategoryServiceImplementation implements CategoryService {
    //For test
    //private List<Category> categories = new ArrayList<Category>();

    //@Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    //由於使用了@GeneratedValue(strategy = GenerationType.IDENTITY) 所以不需要設定ID
    //private Long categoryId = 1L;

    public CategoryServiceImplementation(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 獲取所有類別並分頁和排序
     * @param pageNumber 目前第幾頁
     * @param pageSize 一頁顯示幾筆資料
     * @param sortBy 根據什麼變數進行排序
     * @param sortOrder 升序or降序
     * @return CategoryResponse
     */
    @Override
    public CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        if(categoryRepository.findAll().isEmpty()) {
            throw new APIException("Categories are empty");
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        //定義分頁規則
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        //從DB抓取符合分頁的結果
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        //取得當前頁面的資料
        List<Category> categories = categoryPage.getContent();
        List<CategoryDTO> categoryDTOS = categories.stream().map
                (category -> modelMapper.map(category, CategoryDTO.class)).toList();
        //local variable 會在方法結束時釋放memory
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    /**
     * 創建類別
     * @param categoryDTO 傳入類別DTO
     * @return CategoryDTO
     */
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        //category.setCategoryId(categoryId++);
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName());
        if (categoryFromDB != null) {
            throw new APIException(String.format("Category %s already exist", categoryFromDB.getCategoryName()));
        }
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    /**
     * 刪除類別
     * @param categoryId 傳入類別Primary Key
     * @return CategoryDTO
     */
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        //categories.removeIf(category -> category.getCategoryId().equals(categoryId)); 無論存不存在都會delete
        // Stream 主要用於需要對集合進行篩選、轉換、聚合、排序等操作的情境
//        Category category = categoryRepository.findAll().stream()
//                .filter(obj -> obj.getCategoryId().equals(categoryId)).findFirst()
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));
        //優化查詢
        //Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","CategoryId",categoryId));
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    /**
     * 更新已有類別
     * @param categoryDTO 傳入欲更新的DTO
     * @param categoryId 傳入類別Primary Key
     * @return CategoryDTO
     */
    //可以使用跟delete相同的做法,但此處改用if else來處理
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO , Long categoryId) {
        //使用Optional<> 來處理null值
//        Optional<Category> optionalCategory = categoryRepository.findAll().stream()
//                .filter(obj -> obj.getCategoryId().equals(categoryId)).findFirst();
        //優化查詢
        Category category = modelMapper.map(categoryDTO, Category.class);
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        //如果Category存在
        if (optionalCategory.isPresent()) {
            Category existCategory = optionalCategory.get();
            existCategory.setCategoryName(category.getCategoryName());
            Category savedCategory = categoryRepository.save(existCategory);
            return modelMapper.map(savedCategory, CategoryDTO.class);
        }
        else {
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found");
            throw new ResourceNotFoundException("Category","CategoryId",categoryId);
        }
    }

}
