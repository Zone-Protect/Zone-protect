package org.zone.commands.structure.region.flags.interact.destroy;

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
import org.zone.region.flag.entity.player.interact.block.destroy.BlockBreakFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Used for viewing the status of the {@link BlockBreakFlag}
 */
public class ZoneFlagBlockBreakViewCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_BLOCK_INTERACTION_BREAK_VIEW));

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE,
                new ExactArgument("interact"),
                new ExactArgument("block"),
                new ExactArgument("break"),
                new OptionalArgument<>(new ExactArgument("view"), (String) null));
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("View the details on door interaction");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_BLOCK_INTERACTION_BREAK_VIEW);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        Optional<BlockBreakFlag> opFlag = zone.getFlag(FlagTypes.BLOCK_BREAK);
        commandContext.sendMessage(Messages.getEnabledInfo(opFlag.isPresent()));
        opFlag
                .flatMap(flag -> zone.getMembers().getGroup(flag.getRequiredKey()))
                .ifPresent(Messages::getGroupInfo);
        return CommandResult.success();
    }
}
