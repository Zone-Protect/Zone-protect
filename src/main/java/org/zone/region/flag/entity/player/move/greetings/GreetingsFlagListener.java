package org.zone.region.flag.entity.player.move.greetings;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.zone.ZonePlugin;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;

import java.util.Optional;

public class GreetingsFlagListener {

    @Listener(order = Order.POST)
    public void onPlayerMove(MoveEntityEvent event, @Getter("entity") Player player) {
        if (event.originalPosition().toInt().equals(event.destinationPosition().toInt())) {
            //ignores this event if the player didn't move, but instead rotated
            return;
        }

        Optional<Zone> opPreviousZone = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getPriorityZone(event.entity().world(), event.originalPosition());

        if (opPreviousZone.isPresent()) {
            //player is already in a zone. No need to greet them unless they are coming from one zone to another, that is out of scope of this tutorial
            return;
        }

        Optional<Zone> opNextZone = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getPriorityZone(event.entity().world(), event.destinationPosition());

        if (opNextZone.isEmpty()) {
            //player is not moving into a zone, ignore this flag
            return;
        }

        Zone zone = opNextZone.get();
        Optional<GreetingsFlag> opFlag = zone.getFlag(FlagTypes.GREETINGS);
        if (opFlag.isEmpty()) {
            //not got the greetings flag, no message required
            return;
        }

        // gets the flag -> then gets the message -> then if the message is present -> send that message to the player
        opFlag.get().getMessage().ifPresent(player::sendMessage);
    }

}