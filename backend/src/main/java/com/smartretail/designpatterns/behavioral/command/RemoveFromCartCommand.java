package com.smartretail.designpatterns.behavioral.command;

import com.smartretail.entity.Cart;
import com.smartretail.entity.CartItem;
import com.smartretail.entity.Product;
import com.smartretail.repository.CartItemRepository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Concrete Command 3: RemoveFromCartCommand
 */
public class RemoveFromCartCommand implements RetailCommand {

    private final Long cartItemId;
    private final CartItemRepository cartItemRepository;
    private Cart savedCart;
    private Product savedProduct;
    private int savedQuantity;
    private BigDecimal savedPrice;

    public RemoveFromCartCommand(Long cartItemId, CartItemRepository cartItemRepository) {
        this.cartItemId = cartItemId;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public void execute() {
        Optional<CartItem> itemOpt = cartItemRepository.findById(cartItemId);
        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            this.savedCart = item.getCart();
            this.savedProduct = item.getProduct();
            this.savedQuantity = item.getQuantity();
            this.savedPrice = item.getPrice();
            cartItemRepository.delete(item);
        }
    }

    @Override
    public void undo() {
        if (savedCart != null && savedProduct != null) {
            CartItem restored = CartItem.builder()
                    .cart(savedCart)
                    .product(savedProduct)
                    .quantity(savedQuantity)
                    .price(savedPrice)
                    .build();
            cartItemRepository.save(restored);
        }
    }

    @Override
    public String getCommandName() {
        return "REMOVE_FROM_CART";
    }
}
