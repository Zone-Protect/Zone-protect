package org.zone.commands.system.arguments.sponge;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.ParseCommandArgument;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AccountCurrencyArgument implements CommandArgument<Currency> {

    private final String id;
    private final ParseCommandArgument<UUID> account;

    public AccountCurrencyArgument(@NotNull String id, String argumentId) {
        this(id,
                ((context, argument) -> CommandArgumentResult.from(argument,
                        0,
                        context.getArgument(argument.getArgumentCommand(), argumentId))));
    }

    public AccountCurrencyArgument(@NotNull String id, UUID uuid) {
        this(id, (context, argument) -> CommandArgumentResult.from(argument, 0, uuid));
    }

    public AccountCurrencyArgument(@NotNull String id, ParseCommandArgument<UUID> account) {
        this.id = id;
        this.account = account;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public CommandArgumentResult<Currency> parse(
            @NotNull CommandContext context,
            @NotNull CommandArgumentContext<Currency> argument) throws IOException {
        Optional<EconomyService> opService = Sponge.serviceProvider().provide(EconomyService.class);
        if (opService.isEmpty()) {
            throw new IOException("Eco service could not be found");
        }
        Optional<UniqueAccount> opAccount = opService
                .get()
                .findOrCreateAccount(this.account
                        .parse(context,
                                new CommandArgumentContext<>(argument.getArgumentCommand(),
                                        null,
                                        0,
                                        argument.getRemainingArguments()))
                        .value());
        if (opAccount.isEmpty()) {
            throw new IOException("Eco service could not be found");
        }

        String focus = argument.getFocusArgument();

        Collection<Currency> currencies = new HashSet<>(opAccount.get().balances().keySet());
        currencies.add(opService.get().defaultCurrency());

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
            @NotNull CommandContext commandContext,
            @NotNull CommandArgumentContext<Currency> argument) {
        Optional<EconomyService> opService = Sponge.serviceProvider().provide(EconomyService.class);
        if (opService.isEmpty()) {
            return Collections.emptyList();
        }
        UUID parse;
        try {
            parse = this.account
                    .parse(commandContext,
                            new CommandArgumentContext<>(argument.getArgumentCommand(),
                                    null,
                                    0,
                                    argument.getRemainingArguments()))
                    .value();
        } catch (IOException e) {
            return Collections.emptyList();
        }

        Optional<UniqueAccount> opAccount = opService.get().findOrCreateAccount(parse);
        if (opAccount.isEmpty()) {
            return Collections.singleton(CommandCompletion.of(PlainTextComponentSerializer
                            .plainText()
                            .serialize(opService.get().defaultCurrency().symbol()),
                    opService.get().defaultCurrency().displayName()));
        }
        Collection<Currency> currencies = new HashSet<>(opAccount.get().balances().keySet());
        currencies.add(opService.get().defaultCurrency());

        return currencies
                .parallelStream()
                .map(currency -> CommandCompletion.of(PlainTextComponentSerializer
                        .plainText()
                        .serialize(currency.symbol()), currency.displayName()))
                .collect(Collectors.toList());
    }
}
