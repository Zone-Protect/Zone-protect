package org.zone.commands.structure.region.flags.interact.door;

import net.kyori.adventure.identity.Identity;
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
import org.zone.region.flag.entity.player.interact.door.DoorInteractionFlag;
import org.zone.region.group.Group;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Used to modify the group for {@link DoorInteractionFlag}
 */
public class ZoneFlagInteractDoorGroupCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_DOOR_INTERACTION_SET_GROUP));

    public static final ZoneGroupArgument GROUP = new ZoneGroupArgument("groupId", ZONE);

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE,
                new ExactArgument("interact"),
                new ExactArgument("door"),
                new ExactArgument("set"),
                new ExactArgument("group"),
                GROUP);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Sets the minimum group that can interact with doors");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_DOOR_INTERACTION_SET_GROUP);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        @NotNull DoorInteractionFlag flag = zone
                .getFlag(FlagTypes.DOOR_INTERACTION)
                .orElseGet(FlagTypes.DOOR_INTERACTION::createCopyOfDefault);
        Group newGroup = commandContext.getArgument(this, GROUP);
        zone.getMembers().addKey(newGroup, flag.getRequiredKey());
        try {
            zone.save();
            commandContext
                    .getCause()
                    .sendMessage(Identity.nil(),
                            Messages.getUpdatedMessage(FlagTypes.DOOR_INTERACTION));
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return CommandResult.error(Messages.getZoneSavingError(e));
        }
        return CommandResult.success();
    }
}
