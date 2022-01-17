package org.zone.commands.system.context;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.service.permission.Subject;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.arguments.operation.OptionalArgument;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CommandContext {
    private final @NotNull String[] commands;
    private final @NotNull CommandCause cause;
    private final @NotNull Collection<ArgumentCommand> potentialCommands = new HashSet<>();

    /**
     * @param source   The command source who is running the command
     * @param commands The potential commands of the command context
     * @param command  The string arguments that the source wrote
     */
    public CommandContext(
            @NotNull CommandCause source,
            @NotNull Collection<ArgumentCommand> commands,
            @NotNull String... command) {
        Collection<ArgumentCommand> duped = commands.parallelStream().filter(cmd -> {
            List<String> argIds = cmd
                    .getArguments()
                    .stream()
                    .map(CommandArgument::getId)
                    .collect(Collectors.toList());
            return argIds.parallelStream().anyMatch(arg -> Collections.frequency(argIds, arg) > 1);
        }).collect(Collectors.toSet());
        if (!duped.isEmpty()) {
            throw new IllegalStateException("Duped argument ids found in " +
                    duped
                            .parallelStream()
                            .map(argCmd -> "\t- " + argCmd.getClass().getName())
                            .collect(Collectors.joining("\n")));
        }
        this.commands = command;
        this.potentialCommands.addAll(commands);
        this.cause = source;
    }

    /**
     * Gets the raw string arguments that the command source used
     *
     * @return A String array of the raw string arguments
     */
    public @NotNull String[] getCommand() {
        return this.commands;
    }

    /**
     * The source of the command
     *
     * @return The command sender
     */
    public @NotNull Subject getSource() {
        return this.cause.subject();
    }

    public @NotNull CommandCause getCause() {
        return this.cause;
    }

    public void sendMessage(@NotNull Component component) {
        this.cause.sendMessage(Identity.nil(), component);
    }

    /**
     * Gets the suggestions for the next argument in the command.
     * This is based upon the argument command provided as well as the raw
     * string arguments. The suggestion will be to the last of the raw string argument
     *
     * @param command The command to target
     *
     * @return A list of suggestions for the current context and provided command
     */
    public @NotNull Collection<CommandCompletion> getSuggestions(@NotNull ArgumentCommand command) {
        List<CommandArgument<?>> arguments = command.getArguments();
        int commandArgument = 0;
        Collection<OptionalArgument<?>> optionalArguments = new ArrayList<>();
        for (CommandArgument<?> arg : arguments) {
            if (this.commands.length == commandArgument) {
                if (arg instanceof OptionalArgument) {
                    optionalArguments.add((OptionalArgument<?>) arg);
                    continue;
                }
                return this.suggest(command, arg, commandArgument);
            }
            if (this.commands.length < commandArgument) {
                throw new IllegalArgumentException(
                        "Not enough provided arguments for value of that argument");
            }
            try {
                CommandArgumentResult<?> entry = this.parse(command, arg, commandArgument);
                if (commandArgument == entry.position() && arg instanceof OptionalArgument) {
                    optionalArguments.add((OptionalArgument<?>) arg);
                } else {
                    optionalArguments.clear();
                }
                commandArgument = entry.position();
            } catch (IOException e) {
                return this.suggest(command, arg, commandArgument);
            }
        }
        if (optionalArguments.isEmpty()) {
            return Collections.emptySet();
        }
        Collection<CommandCompletion> ret = new HashSet<>();
        for (OptionalArgument<?> argument : optionalArguments) {
            ret.addAll(this.suggest(command, argument, commandArgument));
        }
        return ret;
    }

    /**
     * Gets the argument value of the command argument provided
     *
     * @param command The command to target
     * @param id      The command argument that should be used
     * @param <T>     The expected type of argument (by providing the command argument, the type will be the same unless the argument is breaking the standard)
     *
     * @return The value of the argument
     *
     * @throws IllegalArgumentException If the provided id argument is not part of the command
     * @throws IllegalStateException    Argument requested is asking for string requirements then what is provided
     */
    public <T> @NotNull T getArgument(
            @NotNull ArgumentCommand command, @NotNull CommandArgument<T> id) {
        return this.getArgument(command, id.getId());
    }

    /**
     * Gets the argument value of the id provided
     *
     * @param command The command to target
     * @param id      The id of the argument to get
     * @param <T>     The expected type of argument
     *
     * @return The value of the argument
     *
     * @throws IllegalArgumentException If the provided id argument is not part of the command
     * @throws IllegalStateException    Argument requested is asking for string requirements then what is provided
     */
    public <T> @NotNull T getArgument(@NotNull ArgumentCommand command, @NotNull String id) {
        List<CommandArgument<?>> arguments = command.getArguments();
        if (arguments.stream().noneMatch(a -> a.getId().equals(id))) {
            throw new IllegalArgumentException("Argument ID (" + id + ") not found within command");
        }
        int commandArgument = 0;
        for (CommandArgument<?> arg : arguments) {
            if (this.commands.length == commandArgument && arg instanceof OptionalArgument) {
                if (arg.getId().equals(id)) {
                    try {
                        return (T) this.parse(command, arg, commandArgument).value();
                    } catch (IOException ignored) {
                    }
                }
                continue;
            }
            if (this.commands.length < commandArgument) {
                throw new IllegalStateException(
                        "Not enough provided arguments for value of that argument");
            }
            try {
                CommandArgumentResult<?> entry = this.parse(command, arg, commandArgument);
                commandArgument = entry.position();
                if (arg.getId().equals(id)) {
                    return (T) entry.value();
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        throw new IllegalArgumentException("Argument ID of '" + id + "' not found within command");
    }

    /**
     * If there is a issue with the command the user is attempting to parse, you can
     * get all the errors with this function. The error is not specific to the command argument
     *
     * @return A set of all errors
     */
    public @NotNull Set<ErrorContext> getErrors() {
        Collection<ErrorContext> map = new HashSet<>();
        for (ArgumentCommand command : this.potentialCommands) {
            List<CommandArgument<?>> arguments = command.getArguments();
            int commandArgument = 0;
            for (CommandArgument<?> arg : arguments) {
                if (this.commands.length == commandArgument && arg instanceof OptionalArgument) {
                    continue;
                }
                if (this.commands.length <= commandArgument) {
                    ErrorContext context = new ErrorContext(command,
                            commandArgument,
                            arg,
                            "Not enough arguments");
                    map.add(context);
                    break;
                }
                try {
                    CommandArgumentResult<?> entry = this.parse(command, arg, commandArgument);
                    commandArgument = entry.position();
                } catch (IOException e) {
                    ErrorContext context = new ErrorContext(command,
                            commandArgument,
                            arg,
                            e.getMessage());
                    map.add(context);
                    break;
                }
            }
        }

        //get best
        Set<ErrorContext> value = new HashSet<>();
        Integer best = null;
        for (ErrorContext value1 : map) {
            Integer current = value1.argumentFailedAt();
            if (best == null) {
                value.add(value1);
                best = current;
                continue;
            }
            if (best < current) {
                value.clear();
                value.add(value1);
                best = current;
            } else if (best.equals(current)) {
                value.add(value1);
            }
        }
        return value;
    }

    /**
     * Gets the command the user is targeting
     *
     * @return A single argument command, if none can be found then {@link Optional#empty()} will be used
     */
    public @NotNull Optional<ArgumentCommand> getCompleteCommand() {
        return this.potentialCommands.stream().filter(command -> {
            List<CommandArgument<?>> arguments = command.getArguments();
            int commandArgument = 0;
            for (CommandArgument<?> arg : arguments) {
                if (this.commands.length == commandArgument && arg instanceof OptionalArgument) {
                    continue;
                }
                if (this.commands.length <= commandArgument) {
                    return false;
                }
                try {
                    CommandArgumentResult<?> entry = this.parse(command, arg, commandArgument);
                    commandArgument = entry.position();
                } catch (IOException e) {
                    return false;
                }
            }
            return this.commands.length == commandArgument;
        }).findAny();

    }

    /**
     * Gets all potential commands from what the user has entered
     *
     * @return A set of all the potential commands
     */
    public @NotNull Set<ArgumentCommand> getPotentialCommands() {
        Map<ArgumentCommand, Integer> map = new HashMap<>();
        this.potentialCommands.forEach(c -> {
            List<CommandArgument<?>> arguments = c.getArguments();
            int commandArgument = 0;
            int completeArguments = 0;
            for (CommandArgument<?> arg : arguments) {
                if (this.commands.length == commandArgument && arg instanceof OptionalArgument) {
                    continue;
                }
                if (this.commands.length <= commandArgument) {
                    map.put(c, completeArguments);
                    return;
                }
                try {
                    CommandArgumentResult<?> entry = this.parse(c, arg, commandArgument);
                    if (commandArgument != entry.position()) {
                        commandArgument = entry.position();
                        completeArguments++;
                    }
                } catch (IOException e) {
                    map.put(c, completeArguments);
                    return;
                }
            }
            map.put(c, completeArguments);
        });

        Set<ArgumentCommand> set = new HashSet<>();
        int current = 0;
        for (Map.Entry<ArgumentCommand, Integer> entry : map.entrySet()) {
            if (entry.getValue() > current) {
                current = entry.getValue();
                set.clear();
            }
            if (entry.getValue() == current) {
                set.add(entry.getKey());
            }
        }
        return set;
    }

    private <T> @NotNull CommandArgumentResult<T> parse(
            @NotNull ArgumentCommand launcher,
            @NotNull CommandArgument<T> arg,
            int commandArgument) throws IOException {
        CommandArgumentContext<T> argContext = new CommandArgumentContext<>(launcher,
                arg,
                commandArgument,
                this.commands);
        return arg.parse(this, argContext);
    }

    private <T> @NotNull Collection<CommandCompletion> suggest(
            @NotNull ArgumentCommand launcher,
            @NotNull CommandArgument<T> arg,
            int commandArgument) {
        if (this.commands.length <= commandArgument) {
            return Collections.emptySet();
        }
        return arg.suggest(this,
                new CommandArgumentContext<>(launcher, arg, commandArgument, this.commands));
    }
}
