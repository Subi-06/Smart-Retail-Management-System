package com.smartretail.designpatterns.behavioral.command;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * COMMAND PATTERN - Invoker
 * Dispatches commands and maintains execution history for rollback and undo operations.
 */
@Component
public class CommandInvoker {

    private final Deque<RetailCommand> commandHistory = new ArrayDeque<>();
    private final Deque<RetailCommand> undoHistory = new ArrayDeque<>();

    public synchronized void executeCommand(RetailCommand command) {
        command.execute();
        commandHistory.push(command);
        undoHistory.clear();
    }

    public synchronized boolean undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            RetailCommand command = commandHistory.pop();
            command.undo();
            undoHistory.push(command);
            return true;
        }
        return false;
    }

    public synchronized boolean canUndo() {
        return !commandHistory.isEmpty();
    }
}
