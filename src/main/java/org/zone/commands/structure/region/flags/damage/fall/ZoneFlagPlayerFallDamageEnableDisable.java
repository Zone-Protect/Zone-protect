package org.zone.commands.structure.region.flags.damage.fall;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.configurate.ConfigurateException;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.simple.BooleanArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;
import org.zone.region.flag.entity.player.damage.fall.PlayerFallDamageFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ZoneFlagPlayerFallDamageEnableDisable implements ArgumentCommand {
    public static final ZoneArgument ZONE_VALUE = new ZoneArgument("zone_value",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_DAMAGE_FALL_ENABLE));
    public static final BooleanArgument ENABLED = new BooleanArgument("enableValue",
            "enable",
            "disable");

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE_VALUE,
                new ExactArgument("damage"),
                new ExactArgument("fall"),
                new ExactArgument("set"),
                ENABLED);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Command to enable/disable the fall damage flag");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_DAMAGE_FALL_ENABLE);
    }

    @Override
    public @NotNull CommandResult run(
            @NotNull CommandContext commandContext, @NotNull String... args) {
        boolean enable = commandContext.getArgument(this, ENABLED);
        Zone zone = commandContext.getArgument(this, ZONE_VALUE);
        PlayerFallDamageFlag playerFallDamageFlag = zone
                .getFlag(FlagTypes.PLAYER_FALL_DAMAGE_FLAG_TYPE)
                .orElse(new PlayerFallDamageFlag());
        if (enable) {
            zone.addFlag(playerFallDamageFlag);
        } else {
            zone.removeFlag(FlagTypes.PLAYER_FALL_DAMAGE_FLAG_TYPE);
        }
        try {
            zone.save();
            commandContext.sendMessage(Messages.getUpdatedMessage(FlagTypes.PLAYER_FALL_DAMAGE_FLAG_TYPE));
        } catch (ConfigurateException ce) {
            ce.printStackTrace();
            return CommandResult.error(Messages.getZoneSavingError(ce));
        }
        return CommandResult.success();
    }
}