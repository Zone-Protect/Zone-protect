package org.zone.commands.system.arguments.sponge;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;
import org.zone.utils.component.ZoneComponentParser;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class ComponentRemainingArgument implements CommandArgument<Component> {

    private final String id;

    public ComponentRemainingArgument(String id) {
        this.id = id;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public CommandArgumentResult<Component> parse(
            CommandContext context, CommandArgumentContext<Component> argument) throws IOException {
        int first = argument.getFirstArgument();
        int length = context.getCommand().length;

        if (first >= length) {
            throw new IOException("Cannot get arguments");
        }
        String value = String.join(" ", argument.getRemainingArguments());
        try {
            Component component = ZoneComponentParser.fromString(value);
            return CommandArgumentResult.from(argument, length - first, component);
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
    }

    @Override
    public @NotNull Collection<CommandCompletion> suggest(
            CommandContext commandContext, CommandArgumentContext<Component> argument) {
        int first = argument.getFirstArgument();
        int length = commandContext.getCommand().length;

        if (first >= length) {
            return Collections.emptyList();
        }
        String value = String.join(" ", argument.getRemainingArguments());
        return ZoneComponentParser
                .getSuggestion(value)
                .stream()
                .map(CommandCompletion::of)
                .collect(Collectors.toSet());
    }
}
