package org.zone.commands.system;

import org.jetbrains.annotations.NotNull;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.io.IOException;

public interface ParseCommandArgument<T> {

    CommandArgumentResult<T> parse(
            @NotNull CommandContext context, @NotNull CommandArgumentContext<T> argument) throws
            IOException;

}
