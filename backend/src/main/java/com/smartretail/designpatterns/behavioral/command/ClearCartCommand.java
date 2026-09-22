package com.smartretail.designpatterns.behavioral.command;

import com.smartretail.designpatterns.behavioral.memento.CartCaretaker;
import com.smartretail.designpatterns.behavioral.memento.CartMemento;
import com.smartretail.entity.*;
import com.smartretail.entity.enums.OrderStatus;
import com.smartretail.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Concrete Command 1: ClearCartCommand
 * Integrates directly with MEMENTO PATTERN for instantaneous UNDO capability.
 */
public class ClearCartCommand implements RetailCommand {

    private final Long userId;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartCaretaker cartCaretaker;
    private CartMemento savedMemento;

    public ClearCartCommand(Long userId,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            ProductRepository productRepository,
                            CartCaretaker cartCaretaker) {
        this.userId = userId;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartCaretaker = cartCaretaker;
    }

    @Override
    public void execute() {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

            // 1. Create Memento of existing items
            List<CartMemento.CartItemSnapshot> snapshots = new ArrayList<>();
            for (CartItem item : items) {
                snapshots.add(new CartMemento.CartItemSnapshot(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()
                ));
            }

            this.savedMemento = new CartMemento(
                    cart.getId(),
                    userId,
                    snapshots,
                    null,
                    LocalDateTime.now()
            );

            // 2. Save in Caretaker for session retrieval
            cartCaretaker.saveMemento(userId, this.savedMemento);

            // 3. Clear the actual cart
            cartItemRepository.deleteAll(items);
        }
    }

    @Override
    public void undo() {
        if (this.savedMemento == null) {
            this.savedMemento = cartCaretaker.getMemento(userId);
        }
        if (this.savedMemento != null) {
            Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
            if (cartOpt.isPresent()) {
                Cart cart = cartOpt.get();
                for (CartMemento.CartItemSnapshot snapshot : this.savedMemento.getItemsSnapshot()) {
                    Optional<Product> prodOpt = productRepository.findById(snapshot.getProductId());
                    if (prodOpt.isPresent()) {
                        CartItem restoredItem = CartItem.builder()
                                .cart(cart)
                                .product(prodOpt.get())
                                .quantity(snapshot.getQuantity())
                                .price(snapshot.getPrice())
                                .build();
                        cartItemRepository.save(restoredItem);
                    }
                }
                cartCaretaker.clear(userId);
            }
        }
    }

    @Override
    public String getCommandName() {
        return "CLEAR_CART";
    }
}
