package org.zone.commands.structure.region.flags.damage.attack;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.configurate.ConfigurateException;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.arguments.zone.ZoneGroupArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;
import org.zone.region.flag.entity.player.damage.attack.EntityDamagePlayerFlag;
import org.zone.region.group.Group;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ZoneFlagDamageAttackSetGroupCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE_VALUE = new ZoneArgument("zone_value",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_DAMAGE_ATTACK_SET_GROUP));
    public static final ZoneGroupArgument GROUP = new ZoneGroupArgument("groupId", ZONE_VALUE);

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE_VALUE,
                new ExactArgument("damage"),
                new ExactArgument("attack"),
                new ExactArgument("set"),
                new ExactArgument("group"),
                GROUP);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Sets the group for Entity Damage Player flag");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_DAMAGE_ATTACK_SET_GROUP);
    }

    @Override
    public @NotNull CommandResult run(
            @NotNull CommandContext commandContext, @NotNull String... args) {
        Zone zone = commandContext.getArgument(this, ZONE_VALUE);
        EntityDamagePlayerFlag entityDamagePlayerFlag = zone
                .getFlag(FlagTypes.ENTITY_DAMAGE_PLAYER_FLAG_TYPE)
                .orElse(FlagTypes.ENTITY_DAMAGE_PLAYER_FLAG_TYPE.createCopyOfDefault());
        Group group = commandContext.getArgument(this, GROUP);
        zone.getMembers().addKey(group, entityDamagePlayerFlag.getRequiredKey());
        try {
            zone.save();
            commandContext.sendMessage(Messages.getUpdatedMessage(FlagTypes.ENTITY_DAMAGE_PLAYER_FLAG_TYPE));
        } catch (ConfigurateException ce) {
            return CommandResult.error(Messages.getZoneSavingError(ce));
        }
        return CommandResult.success();
    }
}