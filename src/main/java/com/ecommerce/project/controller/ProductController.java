package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 產品控制器
 */
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 新增產品
     * @param productDTO 傳入DTO,避免曝露Entity,增加安全性
     * @param categoryId 傳入類別的Primary Key,目的為查詢該類別是否已有該產品,沒有的話則新增在該類別的Product List中
     * @return ResponseEntity
     */
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
        ProductDTO saveProductDTO = productService.addProduct(categoryId,productDTO);
        return new ResponseEntity<>(saveProductDTO, HttpStatus.CREATED);
    }

    /**
     * 獲取所有類別
     * @param pageNumber 目前第幾頁
     * @param pageSize 一頁顯示幾筆資料
     * @param sortBy 根據什麼變數進行排序
     * @param sortOrder 升序or降序
     * @return ResponseEntity
     */
    @GetMapping("/public/products")
    public  ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_PRODUCTS_BY,required = false) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder)
    {
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    /**
     * 查詢該類別所有產品
     * @param categoryId 根據類別id進行查詢
     * @param pageNumber 目前第幾頁
     * @param pageSize 一頁顯示幾筆資料
     * @param sortBy 根據什麼變數進行排序
     * @param sortOrder 升序or降序
     * @return ResponseEntity
     */
    @GetMapping("/public/categories/{categoryId}/products")
    public  ResponseEntity<ProductResponse> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_PRODUCTS_BY,required = false) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder)
    {
        ProductResponse productResponse = productService.getProductsByCategoryId(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    /**
     * 根據關鍵字查詢
     * @param keyword 搜尋關鍵字
     * @param pageNumber 目前第幾頁
     * @param pageSize 一頁顯示幾筆資料
     * @param sortBy 根據什麼變數進行排序
     * @param sortOrder 升序or降序
     * @return ResponseEntity
     */
    @GetMapping("/public/products/keyword/{keyword}")
    public  ResponseEntity<ProductResponse> getProductByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_PRODUCTS_BY,required = false) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder)
    {
        ProductResponse productResponse = productService.getProductsByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    /**
     * 更新產品
     * @param productDTO 傳入DTO,避免曝露Entity,增加安全性
     * @param productId 傳入產品id
     * @return ResponseEntity
     */
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId) {
        ProductDTO updateProductDTO = productService.updateProduct(productId,productDTO);
        return new ResponseEntity<>(updateProductDTO, HttpStatus.OK);
    }

    /**
     * 刪除產品
     * @param productId 傳入產品I
     * @return ResponseEntity
     */
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO deleteProductDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(deleteProductDTO, HttpStatus.OK);
    }

    /**
     * 更新產品照片
     * @param productId 傳入產品id
     * @param image 上傳照片
     * @return ResponseEntity
     * @throws IOException 如果檔案有問題則拋出exception
     */
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updateProductDTO = productService.updateProductImage(productId,image);
        return new ResponseEntity<>(updateProductDTO,HttpStatus.OK);
    }
}
