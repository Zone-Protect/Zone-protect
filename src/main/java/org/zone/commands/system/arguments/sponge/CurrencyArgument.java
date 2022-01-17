package org.zone.commands.system.arguments.sponge;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.ParseCommandArgument;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyArgument implements CommandArgument<Currency> {

    private final @NotNull String id;
    private final @NotNull ParseCommandArgument<? extends Collection<Currency>> currencies;

    public CurrencyArgument(
            @NotNull String id,
            @NotNull ParseCommandArgument<? extends Collection<Currency>> account) {
        this.id = id;
        this.currencies = account;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public CommandArgumentResult<Currency> parse(
            @NotNull CommandContext context,
            @NotNull CommandArgumentContext<Currency> argument) throws IOException {
        Collection<Currency> currencies = this.currencies
                .parse(context,
                        new CommandArgumentContext<>(argument.getArgumentCommand(),
                                null,
                                argument.getFirstArgument(),
                                context.getCommand()))
                .value();

        Optional<EconomyService> opService = Sponge.serviceProvider().provide(EconomyService.class);
        if (opService.isPresent()) {
            Currency defaultCurrency = opService.get().defaultCurrency();
            if (defaultCurrency != null) {
                currencies.add(defaultCurrency);
            }
        }
        String focus = argument.getFocusArgument();

        Optional<Currency> opCurrency = currencies
                .parallelStream()
                .filter(currency -> PlainTextComponentSerializer
                        .plainText()
                        .serialize(currency.symbol())
                        .equalsIgnoreCase(focus))
                .findAny();

        return CommandArgumentResult.from(argument,
                opCurrency.orElseThrow(() -> new IOException("Unknown currency")));
    }

    @Override
    public @NotNull Collection<CommandCompletion> suggest(
            @NotNull CommandContext commandContext, CommandArgumentContext<Currency> argument) {
        Collection<Currency> currencies;
        try {
            currencies = this.currencies
                    .parse(commandContext,
                            new CommandArgumentContext<>(argument.getArgumentCommand(),
                                    null,
                                    argument.getFirstArgument(),
                                    commandContext.getCommand()))
                    .value();
        } catch (IOException e) {
            currencies = new HashSet<>();
        }

        Optional<EconomyService> opService = Sponge.serviceProvider().provide(EconomyService.class);
        if (opService.isPresent()) {
            Currency defaultCurrency = opService.get().defaultCurrency();
            if (defaultCurrency != null) {
                currencies.add(defaultCurrency);
            }
        }
        String focus = argument.getFocusArgument();

        return currencies
                .parallelStream()
                .map(currency -> CommandCompletion.of(PlainTextComponentSerializer
                        .plainText()
                        .serialize(currency.symbol()), currency.displayName()))
                .filter(currency -> currency.completion().startsWith(focus.toLowerCase()))
                .collect(Collectors.toSet());
    }
}
