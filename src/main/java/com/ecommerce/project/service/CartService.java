package com.ecommerce.project.service;


import com.ecommerce.project.payload.CartDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart();

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);
    @Transactional
    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCart(Long cartId,Long productId);
}
