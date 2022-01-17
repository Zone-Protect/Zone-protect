package org.zone.commands.system.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;

public class CommandArgumentContext<T> {

    private final @Nullable CommandArgument<T> argument;
    private final @NotNull ArgumentCommand commandRunner;
    private int firstArgument;
    private @NotNull String[] command;

    public CommandArgumentContext(
            @NotNull ArgumentCommand runner,
            @Nullable CommandArgument<T> argument,
            int firstArgument,
            @NotNull String... command) {
        this.argument = argument;
        this.firstArgument = firstArgument;
        this.command = command;
        this.commandRunner = runner;
    }

    public @NotNull ArgumentCommand getArgumentCommand() {
        return this.commandRunner;
    }

    public @Nullable CommandArgument<T> getArgument() {
        return this.argument;
    }

    public @NotNull String[] getRemainingArguments() {
        int last = this.command.length;
        if (this.firstArgument >= last) {
            throw new IndexOutOfBoundsException("min (" +
                    this.firstArgument +
                    ") is greater then max (" +
                    last +
                    ")");
        }
        String[] arr = new String[last - this.firstArgument];
        System.arraycopy(this.command, this.firstArgument, arr, 0, arr.length);
        return arr;
    }

    public @NotNull String getFocusArgument() {
        return this.command[this.firstArgument];
    }

    public int getFirstArgument() {
        return this.firstArgument;
    }

    public void setCommand(@NotNull String... args) {
        this.command = args;
    }

    public void setStartArgument(int start) {
        this.firstArgument = start;
    }

}
