package org.zone.region.flag.entity.player.interact.door;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.tag.BlockTypeTags;
import org.zone.ZonePlugin;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;
import org.zone.region.group.key.GroupKeys;

import java.util.Optional;

/**
 * The listener for checking the DoorInteractionFlag
 */
public class DoorInteractListener {

    @Listener
    public void onPlayerInteractSecondary(
            InteractBlockEvent.Secondary event, @First Player player) {
        BlockSnapshot snapshot = event.block();
        @NotNull Optional<Zone> opZone = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getPriorityZone(player.world(), event.interactionPoint());
        if (opZone.isEmpty()) {
            return;
        }
        Zone zone = opZone.get();
        if (BlockTypeTags.DOORS.get().contains(snapshot.state().type())) {
            Optional<DoorInteractionFlag> opFlag = zone.getFlag(FlagTypes.DOOR_INTERACTION);
            if (opFlag.isEmpty()) {
                return;
            }
            if (player instanceof ServerPlayer sPlayer &&
                    ZonePermissions.BYPASS_DOOR_INTERACTION.hasPermission(sPlayer)) {
                return;
            }
            if (zone.getParentId().isPresent() &&
                    zone
                            .getParent()
                            .map(parent -> parent
                                    .getMembers()
                                    .getGroup(player.uniqueId())
                                    .getAllKeys()
                                    .contains(GroupKeys.INTERACT_DOOR))
                            .orElse(false)) {
                return;
            }
            if (!opFlag.get().hasPermission(zone, player.uniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
