package com.smartretail.controller;

import com.smartretail.dto.CartDTO;
import com.smartretail.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(@RequestBody CartDTO.AddItemRequest request) {
        // COMMAND PATTERN: AddToCartCommand
        return ResponseEntity.ok(cartService.addToCart(
                request.getUserId(),
                request.getProductId(),
                request.getQuantity() != null ? request.getQuantity() : 1
        ));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartDTO> updateItemQuantity(@PathVariable Long id,
                                                      @RequestParam int quantity,
                                                      @RequestParam Long userId) {
        // COMMAND PATTERN: UpdateQuantityCommand
        return ResponseEntity.ok(cartService.updateCartItemQuantity(id, quantity, userId));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<CartDTO> removeItem(@PathVariable Long id,
                                              @RequestParam Long userId) {
        // COMMAND PATTERN: RemoveFromCartCommand
        return ResponseEntity.ok(cartService.removeCartItem(id, userId));
    }

    @PostMapping("/clear/{userId}")
    public ResponseEntity<CartDTO> clearCart(@PathVariable Long userId) {
        // COMMAND & MEMENTO PATTERN: Saves Memento of cart state and clears items
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    @PostMapping("/undo/{userId}")
    public ResponseEntity<CartDTO> undoClearCart(@PathVariable Long userId) {
        // COMMAND & MEMENTO PATTERN: Restores previous cart from saved Memento
        return ResponseEntity.ok(cartService.undoClearCart(userId));
    }
}
