package org.zone.region.flag.entity.player.move.leaving;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.zone.ZonePlugin;
import org.zone.region.Zone;
import org.zone.region.ZoneManager;
import org.zone.region.flag.FlagTypes;

import java.util.Optional;

public class LeavingFlagListener {

    @Listener(order = Order.POST)
    public void onPlayerMove(MoveEntityEvent event, @Getter("entity") Player player) {
        ZoneManager manager = ZonePlugin.getZonesPlugin().getZoneManager();
        @NotNull Optional<Zone> opOriginalZone = manager.getPriorityZone(player.world(),
                event.originalPosition());
        Optional<Zone> opNextZone = manager.getPriorityZone(player.world(),
                event.destinationPosition());
        if (opNextZone.isPresent()) {
            return;
        }
        if (opOriginalZone.isEmpty()) {
            return;
        }
        Zone zone = opOriginalZone.get();
        @NotNull Optional<LeavingFlag> opFlag = zone.getFlag(FlagTypes.LEAVING);
        if (opFlag.isEmpty()) {
            return;
        }

        Component message = opFlag.get().getLeavingMessage();
        player.sendMessage(message);

    }
}
