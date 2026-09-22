package com.smartretail.designpatterns.behavioral.command;

import com.smartretail.entity.CartItem;
import com.smartretail.repository.CartItemRepository;

import java.util.Optional;

/**
 * Concrete Command 4: UpdateQuantityCommand
 */
public class UpdateQuantityCommand implements RetailCommand {

    private final Long cartItemId;
    private final int newQuantity;
    private final CartItemRepository cartItemRepository;
    private int oldQuantity;

    public UpdateQuantityCommand(Long cartItemId, int newQuantity, CartItemRepository cartItemRepository) {
        this.cartItemId = cartItemId;
        this.newQuantity = newQuantity;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public void execute() {
        Optional<CartItem> itemOpt = cartItemRepository.findById(cartItemId);
        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            this.oldQuantity = item.getQuantity();
            if (newQuantity <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(newQuantity);
                cartItemRepository.save(item);
            }
        }
    }

    @Override
    public void undo() {
        Optional<CartItem> itemOpt = cartItemRepository.findById(cartItemId);
        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            item.setQuantity(oldQuantity);
            cartItemRepository.save(item);
        }
    }

    @Override
    public String getCommandName() {
        return "UPDATE_QUANTITY";
    }
}
