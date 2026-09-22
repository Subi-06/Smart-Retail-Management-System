package com.smartretail.designpatterns.behavioral.command;

import com.smartretail.entity.Cart;
import com.smartretail.entity.CartItem;
import com.smartretail.entity.Product;
import com.smartretail.repository.CartItemRepository;
import com.smartretail.repository.CartRepository;
import com.smartretail.repository.ProductRepository;

import java.util.Optional;

/**
 * Concrete Command 2: AddToCartCommand
 */
public class AddToCartCommand implements RetailCommand {

    private final Long userId;
    private final Long productId;
    private final int quantity;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private Long addedItemId;
    private boolean wasNewItem;
    private int previousQuantity;

    public AddToCartCommand(Long userId,
                             Long productId,
                             int quantity,
                             CartRepository cartRepository,
                             CartItemRepository cartItemRepository,
                             ProductRepository productRepository) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void execute() {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Cart not found for user: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found: " + productId));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            this.previousQuantity = existing.getQuantity();
            existing.setQuantity(existing.getQuantity() + quantity);
            this.wasNewItem = false;
            this.addedItemId = existing.getId();
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            CartItem saved = cartItemRepository.save(newItem);
            this.wasNewItem = true;
            this.addedItemId = saved.getId();
        }
    }

    @Override
    public void undo() {
        if (wasNewItem && addedItemId != null) {
            cartItemRepository.deleteById(addedItemId);
        } else if (!wasNewItem && addedItemId != null) {
            cartItemRepository.findById(addedItemId).ifPresent(item -> {
                item.setQuantity(previousQuantity);
                cartItemRepository.save(item);
            });
        }
    }

    @Override
    public String getCommandName() {
        return "ADD_TO_CART";
    }
}
