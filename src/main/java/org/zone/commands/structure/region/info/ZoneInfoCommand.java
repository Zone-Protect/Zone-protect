package org.zone.commands.structure.region.info;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.user.UserManager;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.meta.eco.EcoFlag;
import org.zone.region.flag.meta.member.MembersFlag;
import org.zone.region.group.key.GroupKeys;
import org.zone.utils.Messages;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ZoneInfoCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_REGION_BASIC_INFO));

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return List.of(new ExactArgument("region"), new ExactArgument("info"), ZONE);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Show info about the zone");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.REGION_BASIC_INFO);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        MembersFlag membersFlag = zone.getMembers();
        EcoFlag ecoFlag = zone.getEconomy();
        @NotNull Collection<UUID> members = membersFlag.getMembers();

        commandContext.sendMessage(Messages.getZoneNameInfo(zone));
        commandContext.sendMessage(Messages.getMembersInfo(members));
        if (Sponge.serviceProvider().provide(EconomyService.class).isPresent()) {
            commandContext.sendMessage(Messages.getBalanceTag());
            ecoFlag
                    .getMoney()
                    .forEach((currency, bigDecimal) -> commandContext.sendMessage(Messages.getBalanceEntry(
                            currency,
                            bigDecimal)));
        }


        Collection<UUID> ownerIds = membersFlag
                .getGroupMapping()
                .entrySet()
                .parallelStream()
                .filter(entry -> entry.getKey().contains(GroupKeys.OWNER))
                .flatMap(e -> e.getValue().parallelStream())
                .collect(Collectors.toSet());
        UserManager userManager = Sponge.server().userManager();
        commandContext.sendMessage(Messages.getOwnerTag());
        ownerIds.forEach(uuid -> userManager
                .streamAll()
                .filter(profile -> profile.uuid().equals(uuid))
                .forEach(profile -> commandContext.sendMessage(Messages.getEntry(profile
                        .name()
                        .orElse("Unknown")))));
        return CommandResult.success();
    }
}
