package org.zone.commands.system.arguments.simple.number;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class IntegerArgument implements CommandArgument<Integer> {

    private final String id;

    public IntegerArgument(String id) {
        this.id = id;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public CommandArgumentResult<Integer> parse(
            @NotNull CommandContext context,
            @NotNull CommandArgumentContext<Integer> argument) throws IOException {
        try {
            return CommandArgumentResult.from(argument,
                    Integer.parseInt(context.getCommand()[argument.getFirstArgument()]));
        } catch (NumberFormatException e) {
            throw new IOException("'" +
                    context.getCommand()[argument.getFirstArgument()] +
                    "' is not a number");
        }
    }

    @Override
    public @NotNull Set<CommandCompletion> suggest(
            @NotNull CommandContext commandContext,
            @NotNull CommandArgumentContext<Integer> argument) {
        return Collections.emptySet();
    }
}