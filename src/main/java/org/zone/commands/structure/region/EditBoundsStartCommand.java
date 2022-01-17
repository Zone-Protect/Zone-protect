package org.zone.commands.structure.region;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.simple.EnumArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.bounds.BoundedRegion;
import org.zone.region.bounds.PositionType;
import org.zone.region.bounds.mode.BoundModes;
import org.zone.region.flag.Flag;
import org.zone.region.flag.FlagTypes;
import org.zone.region.flag.meta.edit.EditingFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EditBoundsStartCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_REGION_EDIT_BOUNDS_EXACT));
    public static final EnumArgument<PositionType> SIDE = new EnumArgument<>("side",
            PositionType.class);

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("edit"),
                ZONE,
                new ExactArgument("Bounds"),
                new ExactArgument("start"));
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Edit the size of the region");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.REGION_EDIT_BOUNDS_EXACT);
    }

    @Override
    public @NotNull CommandResult run(
            @NotNull CommandContext commandContext, @NotNull String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        PositionType positionType = commandContext.getArgument(this, SIDE);
        if (zone.getFlag(FlagTypes.EDITING).isPresent()) {
            return CommandResult.error(Messages.getAlreadyBeingEditedError());
        }
        @NotNull Subject source = commandContext.getSource();
        if (!(source instanceof Player player)) {
            return CommandResult.error(Messages.getPlayerOnlyMessage());
        }

        Optional<BoundedRegion> opRegion = zone
                .getRegion()
                .getTrueChildren()
                .stream()
                .filter(r -> r.contains(player.blockPosition(), false))
                .findAny();

        if (opRegion.isEmpty()) {
            return CommandResult.error(Messages.getMustBeWithinZoneToEditError());
        }

        Flag flag = new EditingFlag(opRegion.get(),
                positionType,
                player.blockPosition(),
                BoundModes.BLOCK,
                player.uniqueId());
        zone.addFlag(flag);
        return CommandResult.success();
    }
}
