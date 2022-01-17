package org.zone.commands.structure.region.flags.interact.itemframe;

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
import org.zone.region.flag.entity.player.interact.itemframe.ItemFrameInteractFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * `* Used to enable/disable the flag {@link ItemFrameInteractFlag}
 */

public class ZoneFlagInteractItemframesEnableDisableCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE_VALUE = new ZoneArgument("zone_value",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_ITEM_FRAME_INTERACTION_ENABLE));
    public static final BooleanArgument ENABLED = new BooleanArgument("enableValue",
            "enable",
            "disable");

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE_VALUE,
                new ExactArgument("interact"),
                new ExactArgument("itemframes"),
                new ExactArgument("set"),
                ENABLED);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Command to enable/disable Itemframe interaction");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_DOOR_INTERACTION_ENABLE);
    }

    @Override
    public @NotNull CommandResult run(
            @NotNull CommandContext commandContext, @NotNull String... args) {
        boolean enable = commandContext.getArgument(this, ENABLED);
        Zone zone = commandContext.getArgument(this, ZONE_VALUE);
        ItemFrameInteractFlag interactItemframesFlag = zone
                .getFlag(FlagTypes.ITEM_FRAME_INTERACT)
                .orElse(new ItemFrameInteractFlag());
        if (enable) {
            zone.addFlag(interactItemframesFlag);
        } else {
            zone.removeFlag(FlagTypes.ITEM_FRAME_INTERACT);
        }
        try {
            zone.save();
            commandContext.sendMessage(Messages.getUpdatedMessage(FlagTypes.ITEM_FRAME_INTERACT));
        } catch (ConfigurateException ce) {
            ce.printStackTrace();
            return CommandResult.error(Messages.getZoneSavingError(ce));
        }
        return CommandResult.success();
    }

}