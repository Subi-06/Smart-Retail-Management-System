package com.smartretail.service.impl;

import com.smartretail.designpatterns.behavioral.command.*;
import com.smartretail.designpatterns.behavioral.memento.CartCaretaker;
import com.smartretail.dto.CartDTO;
import com.smartretail.entity.Cart;
import com.smartretail.entity.CartItem;
import com.smartretail.entity.User;
import com.smartretail.exception.ResourceNotFoundException;
import com.smartretail.repository.*;
import com.smartretail.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartCaretaker cartCaretaker;

    @Autowired
    private CommandInvoker commandInvoker;

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            Cart newCart = Cart.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
            return cartRepository.save(newCart);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        List<CartDTO.CartItemDTO> itemDTOs = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : items) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            itemDTOs.add(CartDTO.CartItemDTO.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productImage(item.getProduct().getImage())
                    .productBrand(item.getProduct().getBrand())
                    .productUnit(item.getProduct().getUnit())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(lineTotal)
                    .availableStock(item.getProduct().getQuantity())
                    .build());
        }

        // Apply membership discount estimation
        User user = cart.getUser();
        double memberDiscountRate = (user != null && user.getMembershipType() != null) ?
                user.getMembershipType().getDiscountRate() : 0.0;

        BigDecimal discount = subtotal.multiply(BigDecimal.valueOf(memberDiscountRate)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxable = subtotal.subtract(discount).max(BigDecimal.ZERO);
        BigDecimal tax = taxable.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP); // 5% GST estimate
        BigDecimal total = taxable.add(tax);

        return CartDTO.builder()
                .id(cart.getId())
                .userId(userId)
                .items(itemDTOs)
                .subtotal(subtotal)
                .discount(discount)
                .tax(tax)
                .total(total)
                .undoAvailable(cartCaretaker.hasMemento(userId))
                .build();
    }

    @Override
    @Transactional
    public CartDTO addToCart(Long userId, Long productId, int quantity) {
        getOrCreateCart(userId);
        AddToCartCommand command = new AddToCartCommand(userId, productId, quantity,
                cartRepository, cartItemRepository, productRepository);
        commandInvoker.executeCommand(command);
        return getCartByUserId(userId);
    }

    @Override
    @Transactional
    public CartDTO updateCartItemQuantity(Long cartItemId, int quantity, Long userId) {
        UpdateQuantityCommand command = new UpdateQuantityCommand(cartItemId, quantity, cartItemRepository);
        commandInvoker.executeCommand(command);
        return getCartByUserId(userId);
    }

    @Override
    @Transactional
    public CartDTO removeCartItem(Long cartItemId, Long userId) {
        RemoveFromCartCommand command = new RemoveFromCartCommand(cartItemId, cartItemRepository);
        commandInvoker.executeCommand(command);
        return getCartByUserId(userId);
    }

    @Override
    @Transactional
    public CartDTO clearCart(Long userId) {
        ClearCartCommand command = new ClearCartCommand(userId, cartRepository, cartItemRepository, productRepository, cartCaretaker);
        commandInvoker.executeCommand(command);
        return getCartByUserId(userId);
    }

    @Override
    @Transactional
    public CartDTO undoClearCart(Long userId) {
        if (cartCaretaker.hasMemento(userId)) {
            // Restore from Memento via command undo
            ClearCartCommand dummyCommand = new ClearCartCommand(userId, cartRepository, cartItemRepository, productRepository, cartCaretaker);
            dummyCommand.undo();
        }
        return getCartByUserId(userId);
    }
}
