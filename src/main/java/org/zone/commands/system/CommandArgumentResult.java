package org.zone.commands.system;

import org.jetbrains.annotations.NotNull;
import org.zone.commands.system.context.CommandArgumentContext;

public record CommandArgumentResult<T>(int position, @NotNull T value) {

    public static <T> CommandArgumentResult<T> from(
            @NotNull CommandArgumentContext<T> argumentContext, @NotNull T value) {
        return from(argumentContext, 1, value);
    }

    public static <T> CommandArgumentResult<T> from(
            @NotNull CommandArgumentContext<T> argumentContext, int length, @NotNull T value) {
        return new CommandArgumentResult<>(argumentContext.getFirstArgument() + length, value);
    }
}
