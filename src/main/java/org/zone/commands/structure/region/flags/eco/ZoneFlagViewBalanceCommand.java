package org.zone.commands.structure.region.flags.eco;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.sponge.CurrencyArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.utils.Messages;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Used to view the balance found for the zone. This command only activates when a valid economy plugin is activate on the server
 */
public class ZoneFlagViewBalanceCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE = new ZoneArgument("zone_value",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_BALANCE_VIEW));

    public static final CurrencyArgument CURRENCY = new CurrencyArgument("currency_value",
            (context, argument) -> {
                CommandArgumentContext<Collection<Currency>> zoneArgument = new CommandArgumentContext<>(
                        argument.getArgumentCommand(),
                        null,
                        argument.getFirstArgument(),
                        context.getCommand());
                Zone zone = context.getArgument(argument.getArgumentCommand(), ZONE);

                return CommandArgumentResult.from(zoneArgument,
                        zone.getEconomy().getMoney().keySet());
            });

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE,
                new ExactArgument("balance"),
                new ExactArgument("view"),
                CURRENCY);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("View the balance for the zone");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_BALANCE_VIEW);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        Currency currency = commandContext.getArgument(this, CURRENCY);
        BigDecimal decimal = zone.getEconomy().getMoney(currency);
        commandContext.sendMessage(Messages.getBalance(decimal));
        return CommandResult.success();
    }

    @Override
    public boolean hasPermission(@NotNull CommandCause source) {
        if (Sponge.serviceProvider().provide(EconomyService.class).isEmpty()) {
            return false;
        }
        return ArgumentCommand.super.hasPermission(source);
    }
}
