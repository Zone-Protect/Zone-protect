package org.zone.commands.structure.region.info.bounds;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.tag.BlockTypeTags;
import org.spongepowered.api.world.Locatable;
import org.zone.ZonePlugin;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.event.listener.PlayerListener;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.utils.Messages;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Used to see the bounds of a zone
 */
public class ZoneInfoBoundsShowCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_REGION_BASIC_INFO));

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("info"),
                ZONE,
                new ExactArgument("bounds"),
                new ExactArgument("show"));
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Show the bounds of a specified region");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.REGION_BASIC_INFO);
    }

    @Override
    public @NotNull CommandResult run(CommandContext context, String... args) {
        Subject subject = context.getSource();
        if (!(subject instanceof Viewer viewer && subject instanceof Locatable locatable)) {
            return CommandResult.error(Messages.getPlayerOnlyMessage());
        }

        Zone specifiedZone = context.getArgument(this, ZONE);

        Collection<Zone> zones = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getZones()
                .parallelStream()
                .filter(zone1 -> zone1.getParent().isPresent())
                .filter(zone -> zone.getParent().get().equals(specifiedZone))
                .collect(Collectors.toSet());
        zones.add(specifiedZone);

        Set<BlockType> usedColours = new HashSet<>();
        Collection<BlockType> woolColours = BlockTypeTags.WOOL.get().values();

        zones.forEach(zone -> {
            Iterator<BlockType> iterator = woolColours.iterator();
            BlockType colour = iterator.next();
            if (usedColours.size() == woolColours.size()) {
                usedColours.clear();
            }
            while (usedColours.contains(colour)) {
                colour = iterator.next();
            }

            usedColours.add(colour);

            BlockType finalColour = colour;
            zone.getRegion().getTrueChildren().forEach(region -> {
                int y = locatable.blockPosition().y() - 1;
                PlayerListener.runOnOutside(region,
                        y,
                        vector3i -> viewer.sendBlockChange(vector3i, finalColour.defaultState()),
                        zone.getParentId().isPresent());
                Sponge
                        .server()
                        .scheduler()
                        .submit(Task
                                .builder()
                                .plugin(ZonePlugin.getZonesPlugin().getPluginContainer())
                                .delay(10, TimeUnit.SECONDS)
                                .execute(() -> PlayerListener.runOnOutside(region,
                                        y,
                                        viewer::resetBlockChange,
                                        zone.getParentId().isPresent()))
                                .build());
            });
        });
        return CommandResult.success();
    }
}
