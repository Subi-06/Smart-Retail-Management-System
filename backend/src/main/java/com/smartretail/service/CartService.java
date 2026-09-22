package com.smartretail.service;

import com.smartretail.dto.CartDTO;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    CartDTO addToCart(Long userId, Long productId, int quantity);
    CartDTO updateCartItemQuantity(Long cartItemId, int quantity, Long userId);
    CartDTO removeCartItem(Long cartItemId, Long userId);
    CartDTO clearCart(Long userId);
    CartDTO undoClearCart(Long userId);
}
