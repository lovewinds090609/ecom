package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 實作產品服務
 */
@Service
public class ProductServiceImplementation implements ProductService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileService fileService;
    @Value("${project.image}")
    private String path;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private EntityManager entityManager;
    /**
     * 新增產品
     * @param categoryId 傳入類別Primary Key
     * @param productDTO 傳入產品DTO
     * @return ProductDTO
     */
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        List<Product> products = category.getProducts();
        boolean ifProductNotPresent = true;
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                ifProductNotPresent = false;
                break;
            }
        }
        if(ifProductNotPresent){
            Product product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
            product.setImage("image.png");
            double specialPrice = product.getProductPrice() - (product.getDiscount() * 0.01 * product.getProductPrice());
            product.setProductSpecialPrice(specialPrice);
            Product saveProduct = productRepository.save(product);
            return modelMapper.map(saveProduct,ProductDTO.class);
        } else throw new APIException("Product already exists");
    }

    /**
     * 獲取所有產品
     * @param pageNumber 目前第幾頁
     * @param pageSize 一頁顯示幾筆資料
     * @param sortBy 根據什麼變數進行排序
     * @param sortOrder 升序or降序
     * @return ProductResponse
     */
    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);
        List<Product> products = pageProducts.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products found");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        return new ProductResponse(productDTOS,pageProducts.getNumber(),pageProducts.getSize(), pageProducts.getTotalElements(), pageProducts.getTotalPages(), pageProducts.isLast());
    }

    /**
     * 透過類別ID獲取產品
     * @param categoryId 傳入類別DTO
     * @param pageNumber 目前第幾頁
     * @param pageSize 一頁顯示幾筆資料
     * @param sortBy 根據什麼變數進行排序
     * @param sortOrder 升序or降序
     * @return ProductResponse
     */
    @Override
    public ProductResponse getProductsByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //categoryId對應的是Category類別資料庫的Primary Key
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByProductPriceAsc(category,pageDetails);
        List<Product> products = pageProducts.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products found");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        return new ProductResponse(productDTOS,pageProducts.getNumber(),pageProducts.getSize(), pageProducts.getTotalElements(), pageProducts.getTotalPages(), pageProducts.isLast());
    }

    /**
     * 透過關鍵字獲取產品
     * @param keyword 關鍵字
     * @param pageNumber 目前第幾頁
     * @param pageSize 一頁顯示幾筆資料
     * @param sortBy 根據什麼變數進行排序
     * @param sortOrder 升序or降序
     * @return ProductResponse
     */
    @Override
    public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%",pageDetails);
        List<Product> products = pageProducts.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products found with keyword :" + keyword);
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        return new ProductResponse(productDTOS,pageProducts.getNumber(),pageProducts.getSize(), pageProducts.getTotalElements(), pageProducts.getTotalPages(), pageProducts.isLast());
    }

    /**
     * 更新產品
     * @param productId 傳入產品Primary Key
     * @param productDTO 傳入產品DTO
     * @return ProductDTO
     */
    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productFromDB = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        Product product = modelMapper.map(productDTO, Product.class);
        productFromDB.setProductName(product.getProductName());
        productFromDB.setProductDescription(product.getProductDescription());
        productFromDB.setProductQuantity(product.getProductQuantity());
        productFromDB.setProductPrice(product.getProductPrice());
        productFromDB.setDiscount(product.getDiscount());
        productFromDB.setProductSpecialPrice(product.getProductSpecialPrice());
        Product saveProduct = productRepository.save(productFromDB);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream().map(p -> modelMapper.map(p.getProduct(),ProductDTO.class)).toList();
            cartDTO.setProducts(products);
            return cartDTO;
        }).toList();

        cartDTOs.forEach(cart -> cartService.updateProductInCart(cart.getCartId(),productId));
        return modelMapper.map(saveProduct,ProductDTO.class);
    }

    /**
     * 刪除產品
     * @param productId 傳入產品Primary Key
     * @return ProductDTO
     */
    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDB = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCardId(),productId));
        productRepository.delete(productFromDB);
        return modelMapper.map(productFromDB,ProductDTO.class);
    }

    /**
     * 更新產品圖片
     * @param productId 傳入產品Primary Key
     * @param image 傳入更新的圖片
     * @return ProductDTO
     * @throws IOException 當檔案錯誤時拋出exception
     */
    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //先從DB抓取product
        Product productFromDb = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        //上傳image to server
        //從上傳的image獲取file name
        String fileName = fileService.uploadImage(path,image);
        //更新filename to image
        productFromDb.setImage(fileName);
        //save product
        Product saveProduct = productRepository.save(productFromDb);
        //return
        return modelMapper.map(saveProduct,ProductDTO.class);
    }

}
