package org.zone.commands.system;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.util.Collection;

public interface SuggestCommandArgument<T> {
    @NotNull Collection<CommandCompletion> suggest(
            @NotNull CommandContext commandContext, @NotNull CommandArgumentContext<T> argument);

}
