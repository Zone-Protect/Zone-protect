package org.zone.commands.system.arguments.operation;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RemainingArgument<T> implements CommandArgument<List<T>> {

    private final String id;
    private final List<CommandArgument<T>> argument;

    @Deprecated
    public RemainingArgument(String id) {
        throw new RuntimeException("Remaining Argument requires at least 1 argument");
    }

    public RemainingArgument(CommandArgument<T> argument) {
        this(argument.getId(), argument);
    }

    @SafeVarargs
    public RemainingArgument(String id, CommandArgument<T>... argument) {
        this(id, Arrays.asList(argument));
    }

    public RemainingArgument(String id, Collection<CommandArgument<T>> argument) {
        if (argument.isEmpty()) {
            throw new IllegalArgumentException("Remaining Argument cannot have a argument of empty");
        }
        this.id = id;
        this.argument = new ArrayList<>(argument);
    }

    private CommandArgumentResult<T> parseAny(
            ArgumentCommand command, CommandContext context, int B) throws IOException {
        IOException e1 = null;
        for (int A = 0; A < this.argument.size(); A++) {
            try {
                CommandArgumentContext<T> argumentContext = new CommandArgumentContext<>(command,
                        this.argument.get(A),
                        B,
                        context.getCommand());
                return this.argument.get(A).parse(context, argumentContext);
            } catch (IOException e) {
                if (A == 0) {
                    e1 = e;
                }
            }
        }
        if (e1 == null) {
            //shouldn't be possible
            throw new IOException("Unknown error occurred");
        }
        throw e1;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public CommandArgumentResult<List<T>> parse(
            CommandContext context, CommandArgumentContext<List<T>> argument) throws IOException {
        int A = argument.getFirstArgument();
        List<T> list = new ArrayList<>();
        while (A < context.getCommand().length) {
            CommandArgumentResult<T> entry = this.parseAny(argument.getArgumentCommand(),
                    context,
                    A);
            A = entry.position();
            list.add(entry.value());
        }
        return new CommandArgumentResult<>(A, list);
    }

    @Override
    public @NotNull Set<CommandCompletion> suggest(
            @NotNull CommandContext context, @NotNull CommandArgumentContext<List<T>> argument) {
        int A = argument.getFirstArgument();
        while (A < context.getCommand().length) {
            final int B = A;
            CommandArgumentResult<T> entry;
            try {
                entry = this.parseAny(argument.getArgumentCommand(), context, A);
            } catch (IOException e) {
                return this.argument
                        .stream()
                        .flatMap(a -> a
                                .suggest(context,
                                        new CommandArgumentContext<>(argument.getArgumentCommand(),
                                                a,
                                                B,
                                                context.getCommand()))
                                .stream())
                        .collect(Collectors.toSet());
            }
            A = entry.position();
        }
        return Collections.emptySet();
    }
}