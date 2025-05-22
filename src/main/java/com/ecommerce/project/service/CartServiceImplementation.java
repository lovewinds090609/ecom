package com.ecommerce.project.service;


import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImplementation implements CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private EntityManager entityManager;
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCardId(),productId);

        if(cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists");
        }

        if (product.getProductQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getProductQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getProductQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getProductSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setProductQuantity(product.getProductQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getProductSpecialPrice() * quantity));

        cart.getCartItems().add(newCartItem);

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setProductQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;

    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new APIException("No carts exist");
        }
        List<CartDTO> cartDTOS = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            cart.getCartItems().forEach(c -> c.getProduct().setProductQuantity(c.getQuantity()));
            List<ProductDTO> products = cart.getCartItems().stream().map(
                    p -> modelMapper.map(p.getProduct(), ProductDTO.class)
            ).toList();
            cartDTO.setProducts(products);
            return cartDTO;
        }).toList();
        return cartDTOS;
    }

    @Override
    public CartDTO getCart() {
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null) {
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }
        Long cartId = cart.getCardId();
        Cart doubleCheckCart = cartRepository.findCartByEmailAndCardId(emailId,cartId);
        if(doubleCheckCart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(doubleCheckCart, CartDTO.class);
        doubleCheckCart.getCartItems().forEach(c -> c.getProduct().setProductQuantity(c.getQuantity()));
        List<ProductDTO> products = doubleCheckCart.getCartItems().stream().map(p -> modelMapper.map(p.getProduct(),ProductDTO.class)).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        if(userCart == null) {
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }
        Long cardId = userCart.getCardId();
        Cart cart = cartRepository.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cardId", cardId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        if(product.getProductQuantity() == 0) {
            throw new APIException("Product " + product.getProductName() + " is not available");
        }
        if (product.getProductQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to the quantity " + product.getProductQuantity() + ".");
        }
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cardId,productId);
        if(cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " does not exist");
        }
        int newQuantity = cartItem.getQuantity() + quantity;
        if(newQuantity < 0) {
            throw new APIException("The result quantity cannot be negative");
        }
        if(newQuantity == 0){
            deleteProductFromCart(cardId,productId);
        }else{
            cartItem.setQuantity(quantity + cartItem.getQuantity());;
            cartItemRepository.save(cartItem);
            cart.setTotalPrice(cart.getTotalPrice() + (product.getProductSpecialPrice() * quantity));
            cartRepository.save(cart);
        }
//        if(cartItem.getQuantity() == 0){
//            cartItemRepository.delete(cartItem);
//        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productStream = cartItems.stream().map(item ->{
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setProductQuantity(item.getQuantity());
            return productDTO;
        });
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem == null) {
            throw new ResourceNotFoundException("CartItem", "product", productId);
        }
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        if (cart.getTotalPrice() < 0) {
            throw new APIException("Total price cannot be negative.");
        }
        cart.getCartItems().remove(cartItem);
        cartItem.getProduct().getProducts().remove(cartItem);
        //cartItem.setProduct(null);
        //cartItem.setCart(null);
        //cartItemRepository.save(cartItem);
        //cartItemRepository.delete(cartItem);
        //cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);
        cartRepository.save(cart);
        return "Product removed from cart";
    }

    @Override
    public void updateProductInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " is not available");
        }
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getProductSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.save(cartItem);
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }

}
