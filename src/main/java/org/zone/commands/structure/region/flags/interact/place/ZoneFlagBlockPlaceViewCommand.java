package org.zone.commands.structure.region.flags.interact.place;

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
import org.zone.region.flag.entity.player.interact.block.place.BlockPlaceFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Used to view the status of {@link BlockPlaceFlag}
 */
public class ZoneFlagBlockPlaceViewCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_BLOCK_INTERACTION_PLACE_VIEW));

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE,
                new ExactArgument("interact"),
                new ExactArgument("block"),
                new ExactArgument("place"),
                new OptionalArgument<>(new ExactArgument("view"), (String) null));
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("View the details on block placement");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_BLOCK_INTERACTION_PLACE_VIEW);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        Optional<BlockPlaceFlag> opFlag = zone.getFlag(FlagTypes.BLOCK_PLACE);
        commandContext.sendMessage(Messages.getEnabledInfo(opFlag.isPresent()));
        opFlag
                .flatMap(flag -> zone.getMembers().getGroup(flag.getRequiredKey()))
                .ifPresent(group -> commandContext.sendMessage(Messages.getGroupInfo(group)));
        return CommandResult.success();
    }
}
