package org.zone.region.flag.entity.player.damage.attack;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.zone.ZonePlugin;
import org.zone.region.Zone;
import org.zone.region.group.Group;
import org.zone.region.group.key.GroupKeys;

import java.util.Optional;

public class EntityDamagePlayerListener {
    @Listener
    public void onEntityDamageEvent(DamageEntityEvent event, @First EntityDamageSource source) {

        if (!(event.entity() instanceof Player player)) {
            return;
        }

        if ((source.source() instanceof Player attacker && attacker.equals(player))) {
            return;
        }

        Optional<Zone> opZone = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getPriorityZone(event.entity().world(), event.entity().position());
        if (opZone.isEmpty()) {
            return;
        }

        Zone zone = opZone.get();
        Group group = zone.getMembers().getGroup(player.uniqueId());
        if (group.contains(GroupKeys.ENTITY_DAMAGE_PLAYER)) {
            event.setCancelled(true);
        }
    }
}