package com.smartretail.designpatterns.behavioral.command;

/**
 * COMMAND PATTERN - Command Interface
 * Encapsulates a retail request as an object, with undo support.
 */
public interface RetailCommand {
    void execute();
    void undo();
    String getCommandName();
}
