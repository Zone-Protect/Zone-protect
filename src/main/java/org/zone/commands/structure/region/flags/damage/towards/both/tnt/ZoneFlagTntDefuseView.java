package org.zone.commands.structure.region.flags.damage.towards.both.tnt;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.operation.OptionalArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ZoneFlagTntDefuseView implements ArgumentCommand {
    public static final ZoneArgument ZONE_VALUE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.FLAG_TNT_DEFUSE_VIEW));
    public static final OptionalArgument<String> VIEW = new OptionalArgument<>(new ExactArgument(
            "view"), (String) null);

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                             new ExactArgument("flag"),
                             ZONE_VALUE,
                             new ExactArgument("tnt"),
                             new ExactArgument("defuse"),
                             VIEW);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("View info about the tnt defuse flag");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.OVERRIDE_FLAG_TNT_DEFUSE_VIEW);
    }

    @Override
    public @NotNull CommandResult run(
            @NotNull CommandContext commandContext, @NotNull String... args) {
        Zone zone = commandContext.getArgument(this, ZONE_VALUE);
        commandContext
                .getCause()
                .sendMessage(Identity.nil(),
                        Messages.getEnabledInfo(zone.containsFlag(FlagTypes.TNT_DEFUSE)));
        return CommandResult.success();
    }
}