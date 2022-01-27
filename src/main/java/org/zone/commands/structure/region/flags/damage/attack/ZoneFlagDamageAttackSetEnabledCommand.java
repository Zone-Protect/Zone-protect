package org.zone.commands.structure.region.flags.damage.attack;

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
import org.zone.region.flag.entity.player.damage.attack.EntityDamagePlayerFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ZoneFlagDamageAttackSetEnabledCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE_VALUE = new ZoneArgument("zone_value",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_DAMAGE_ATTACK_ENABLE));
    public static final BooleanArgument ENABLED = new BooleanArgument("enableValue",
            "enable",
            "disable");

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE_VALUE,
                new ExactArgument("damage"),
                new ExactArgument("attack"),
                new ExactArgument("towards"),
                new ExactArgument("player"),
                new ExactArgument("set"),
                ENABLED);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Command to enable/disable the Damage Flag");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_DAMAGE_ATTACK_ENABLE);
    }

    @Override
    public @NotNull CommandResult run(
            @NotNull CommandContext commandContext, @NotNull String... args) {
        boolean enable = commandContext.getArgument(this, ENABLED);
        Zone zone = commandContext.getArgument(this, ZONE_VALUE);
        EntityDamagePlayerFlag entityDamagePlayerFlag = zone
                .getFlag(FlagTypes.ENTITY_DAMAGE_PLAYER_FLAG_TYPE)
                .orElse(new EntityDamagePlayerFlag());
        if (enable) {
            zone.addFlag(entityDamagePlayerFlag);
        } else {
            zone.removeFlag(FlagTypes.ENTITY_DAMAGE_PLAYER_FLAG_TYPE);
        }
        try {
            zone.save();
            commandContext.sendMessage(Messages.getUpdatedMessage(FlagTypes.ENTITY_DAMAGE_PLAYER_FLAG_TYPE));
        } catch (ConfigurateException ce) {
            ce.printStackTrace();
            return CommandResult.error(Messages.getZoneSavingError(ce));
        }
        return CommandResult.success();
    }
}
