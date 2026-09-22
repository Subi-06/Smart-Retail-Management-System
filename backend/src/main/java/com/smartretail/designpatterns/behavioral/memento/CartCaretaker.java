package com.smartretail.designpatterns.behavioral.memento;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * MEMENTO PATTERN - Caretaker
 * Manages saved mementos per user session for undo restoration.
 */
@Component
public class CartCaretaker {

    private final ConcurrentHashMap<Long, CartMemento> userMementos = new ConcurrentHashMap<>();

    public void saveMemento(Long userId, CartMemento memento) {
        userMementos.put(userId, memento);
    }

    public CartMemento getMemento(Long userId) {
        return userMementos.get(userId);
    }

    public CartMemento popMemento(Long userId) {
        return userMementos.remove(userId);
    }

    public boolean hasMemento(Long userId) {
        return userMementos.containsKey(userId);
    }

    public void clear(Long userId) {
        userMementos.remove(userId);
    }
}
