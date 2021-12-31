package org.zone.commands.structure.create;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.configurate.ConfigurateException;
import org.zone.Permissions;
import org.zone.ZonePlugin;
import org.zone.commands.structure.misc.Messages;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.event.listener.PlayerListener;
import org.zone.region.Zone;
import org.zone.region.ZoneBuilder;
import org.zone.region.bounds.BoundedRegion;
import org.zone.region.bounds.ChildRegion;
import org.zone.region.bounds.Region;
import org.zone.region.flag.meta.member.MembersFlag;
import org.zone.region.group.DefaultGroups;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The command for zone bound creation ending. This command can only be activated when the "start" command is active.
 * <p>Command: "/zone create bounds end"</p>
 */
public class ZoneCreateEndCommand implements ArgumentCommand {
    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("create"),
                             new ExactArgument("bounds"),
                             new ExactArgument("end"));
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Ends the creation by bounds. Will use your location as ending");
    }

    @Override
    public @NotNull Optional<String> getPermissionNode() {
        return Optional.of(Permissions.REGION_CREATE_BOUNDS.getPermission());
    }

    @Override
    public @NotNull CommandResult run(CommandContext context, String... args) {
        Subject subject = context.getSource();
        if (!(subject instanceof Player player)) {
            return CommandResult.error(Messages.setUniversalPlayerOnlyCommandErrorMessage());
        }
        Optional<ZoneBuilder> opZone = ZonePlugin
                .getZonesPlugin()
                .getMemoryHolder()
                .getZoneBuilder(player.uniqueId());
        if (opZone.isEmpty()) {
            return CommandResult.error(Messages.getZonesCreateEndCommandrunopZoneEmptyError());
        }

        Zone zone = opZone.get().build();
        MembersFlag membersFlag = zone.getMembers();
        membersFlag.addMember(DefaultGroups.OWNER, player.uniqueId());
        zone.addFlag(membersFlag);

        if (zone.getParentId().isPresent()) {
            Optional<Zone> opParent = zone.getParent();
            if (opParent.isEmpty()) {
                return CommandResult.error(Messages.getZonesCreateEndCommandrunopParentEmptyError(zone));
            }

            Region region = zone.getRegion();
            Collection<BoundedRegion> children = region.getTrueChildren();
            if (children
                    .stream()
                    .anyMatch(boundedRegion -> !opParent
                            .get()
                            .inRegion(null, boundedRegion.getMin().toDouble()))) {
                return CommandResult.error(Messages.getZonesCreateEndCommandrunError2(opParent.get()));
            }

            if (children
                    .stream()
                    .anyMatch(boundedRegion -> !opParent
                            .get()
                            .inRegion(null, boundedRegion.getMax().toDouble()))) {
                return CommandResult.error(Messages.getZonesCreateEndCommandrunError3(opParent.get()));
            }
        }

        ZonePlugin.getZonesPlugin().getZoneManager().register(zone);
        player.sendMessage(Messages.getZonesCreateEndCommandrunZoneCreated(zone));
        ZonePlugin.getZonesPlugin().getMemoryHolder().unregisterZoneBuilder(player.uniqueId());
        ChildRegion region = zone.getRegion();
        Collection<BoundedRegion> children = region.getTrueChildren();
        children.forEach(boundedRegion -> PlayerListener.runOnOutside(boundedRegion,
                                                                      player.location().blockY() +
                                                                              3,
                                                                      player::resetBlockChange,
                                                                      zone
                                                                              .getParent()
                                                                              .isPresent()));

        try {
            zone.save();
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return CommandResult.error(Messages.setUniversalZoneSavingErrorMessage(e));
        }

        return CommandResult.success();
    }

    @Override
    public boolean hasPermission(CommandCause source) {
        if (!(source.subject() instanceof Player)) {
            return false;
        }
        return ArgumentCommand.super.hasPermission(source);
    }

    @Override
    public boolean canApply(CommandContext context) {
        Subject subject = context.getSource();
        if (!(subject instanceof Player player)) {
            return false;
        }
        Optional<ZoneBuilder> opBuilder = ZonePlugin
                .getZonesPlugin()
                .getMemoryHolder()
                .getZoneBuilder(player.uniqueId());
        if (opBuilder.isEmpty()) {
            return false;
        }
        return ArgumentCommand.super.canApply(context);
    }
}
