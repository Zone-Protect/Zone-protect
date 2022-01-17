package org.zone.commands.structure.region.flags.interact.destroy;

import net.kyori.adventure.identity.Identity;
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
import org.zone.region.flag.entity.player.interact.block.destroy.BlockBreakFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Used for changing the enabled status of the {@link BlockBreakFlag}
 */
public class ZoneFlagBlockBreakSetEnabledCommand implements ArgumentCommand {

    public static final BooleanArgument VALUE = new BooleanArgument("enabledValue",
            "enable",
            "disable");
    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_BLOCK_INTERACTION_BREAK_ENABLE));

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE,
                new ExactArgument("interact"),
                new ExactArgument("block"),
                new ExactArgument("break"),
                new ExactArgument("set"),
                VALUE);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Sets if the prevention to break blocks is enabled");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_BLOCK_INTERACTION_BREAK_ENABLE);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        boolean value = commandContext.getArgument(this, VALUE);
        if (value) {
            zone.addFlag(FlagTypes.BLOCK_BREAK.createCopyOfDefault());
        } else {
            zone.removeFlag(FlagTypes.BLOCK_BREAK);
        }
        try {
            zone.save();
            commandContext
                    .getCause()
                    .sendMessage(Identity.nil(), Messages.getUpdatedMessage(FlagTypes.BLOCK_BREAK));
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return CommandResult.error(Messages.getZoneSavingError(e));
        }
        return CommandResult.success();
    }
}
