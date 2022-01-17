package org.zone.commands.system.arguments.simple.number;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.ParseCommandArgument;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class RangeArgument<N extends Number> implements CommandArgument<N> {

    private final CommandArgument<N> parser;
    private final ParseCommandArgument<N> lower;
    private final ParseCommandArgument<N> higher;

    public RangeArgument(
            CommandArgument<N> parser,
            ParseCommandArgument<N> lower,
            ParseCommandArgument<N> higher) {
        this.parser = parser;
        this.lower = lower;
        this.higher = higher;
    }

    @Override
    public @NotNull String getId() {
        return this.parser.getId();
    }

    @Override
    public CommandArgumentResult<N> parse(
            @NotNull CommandContext context, @NotNull CommandArgumentContext<N> argument) throws
            IOException {
        CommandArgumentResult<N> numberResult = this.parser.parse(context, argument);
        N number = numberResult.value();
        N lower = this.lower.parse(context, argument).value();
        N higher = this.higher.parse(context, argument).value();
        if (number.doubleValue() < lower.doubleValue() ||
                number.doubleValue() > higher.doubleValue()) {
            throw new IOException("Number of " +
                    number +
                    " wasn't between " +
                    lower +
                    "-" +
                    higher);
        }
        return numberResult;
    }

    @Override
    public @NotNull Collection<CommandCompletion> suggest(
            @NotNull CommandContext commandContext, @NotNull CommandArgumentContext<N> argument) {
        try {
            N lowest = this.lower.parse(commandContext, argument).value();
            N highest = this.higher.parse(commandContext, argument).value();

            Collection<CommandCompletion> ret = new HashSet<>();
            ret.add(CommandCompletion.of(lowest.toString()));
            ret.add(CommandCompletion.of(highest.toString()));
            for (int A = lowest.intValue() + 1; A < highest.intValue(); A++) {
                ret.add(CommandCompletion.of(A + ""));
            }
            return ret;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
