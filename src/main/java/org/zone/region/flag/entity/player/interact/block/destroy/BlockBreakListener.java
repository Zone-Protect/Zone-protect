package org.zone.region.flag.entity.player.interact.block.destroy;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.server.ServerLocation;
import org.zone.ZonePlugin;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;
import org.zone.region.group.Group;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The listener for checking the BlockBreakFlag
 */
public class BlockBreakListener {

    @Listener
    public void onPlayerBlockChangeEvent(ChangeBlockEvent.All event, @First Player player) {
        if (player instanceof ServerPlayer sPlayer &&
                ZonePermissions.BYPASS_BLOCK_INTERACTION_BREAK.hasPermission(sPlayer)) {
            return;
        }
        Map<BlockTransaction, Zone> inZone = event
                .transactions()
                .stream()
                .filter(t -> t.original().location().isPresent())
                .map(t -> {
                    ServerLocation loc = t
                            .original()
                            .location()
                            .orElseThrow(() -> new RuntimeException("Broke logic"));
                    @NotNull Optional<Zone> zone = ZonePlugin
                            .getZonesPlugin()
                            .getZoneManager()
                            .getPriorityZone(loc);
                    return new AbstractMap.SimpleEntry<>(t, zone.orElse(null));
                })
                .filter(map -> map.getValue() != null)
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue));

        inZone
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().operation() == Operations.BREAK.get())
                .filter(entry -> entry.getValue().getFlag(FlagTypes.BLOCK_BREAK).isPresent())
                .forEach(entry -> {
                    Group group = entry.getValue().getMembers().getGroup(player.uniqueId());
                    BlockBreakFlag blockBreakFlag = entry
                            .getValue()
                            .getFlag(FlagTypes.BLOCK_BREAK)
                            .orElseThrow(() -> new RuntimeException("Broke logic"));
                    Group flagGroup = entry
                            .getValue()
                            .getMembers()
                            .getGroup(blockBreakFlag.getRequiredKey())
                            .orElse(null);
                    if (flagGroup == null) {
                        return;
                    }
                    if (group.inherits(flagGroup)) {
                        return;
                    }
                    entry.getKey().invalidate();
                });
    }

}
