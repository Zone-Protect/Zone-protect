package org.zone.commands.structure.create.bounds;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.math.vector.Vector3i;
import org.zone.ZonePlugin;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.context.CommandContext;
import org.zone.region.ZoneBuilder;
import org.zone.region.bounds.BoundedRegion;
import org.zone.region.bounds.ChildRegion;
import org.zone.region.bounds.mode.BoundMode;
import org.zone.utils.Messages;

import java.util.Collections;

public abstract class AbstractCreateZoneStartCommand implements ArgumentCommand {

    protected abstract String getNameArgument(CommandContext context);

    protected abstract BoundMode getBoundMode();

    protected abstract ZoneBuilder updateBuilder(
            CommandContext context, String name, BoundedRegion bounded, ZoneBuilder builder);

    @Override
    public @NotNull CommandResult run(CommandContext context, String... args) {
        Subject subject = context.getSource();
        if (!(subject instanceof Player player)) {
            return CommandResult.error(Messages.getPlayerOnlyMessage());
        }

        String name = this.getNameArgument(context);
        Vector3i vector3i = player.location().blockPosition();
        BoundMode boundMode = this.getBoundMode();
        Vector3i startVector = new Vector3i(vector3i.x(), 0, vector3i.z());
        Vector3i endVector = new Vector3i(vector3i.x(), 256, vector3i.z());
        startVector = boundMode
                .shiftOther(player.world().location(startVector), endVector)
                .blockPosition();
        endVector = boundMode
                .shift(player.world().location(endVector), startVector)
                .blockPosition();
        BoundedRegion region = new BoundedRegion(startVector, endVector);

        ChildRegion childRegion = new ChildRegion(Collections.singleton(region));

        ZoneBuilder builder = new ZoneBuilder()
                .setName(name)
                .setContainer(ZonePlugin.getZonesPlugin().getPluginContainer())
                .setKey(name.toLowerCase().replaceAll(" ", "_"))
                .setWorld(player.world())
                .setBoundMode(boundMode)
                .setRegion(childRegion);

        builder = this.updateBuilder(context, name, region, builder);
        if (ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getZone(builder.getContainer(), builder.getKey())
                .isPresent()) {
            return CommandResult.error(Messages.getDuplicateNameError());
        }
        ZonePlugin
                .getZonesPlugin()
                .getMemoryHolder()
                .registerZoneBuilder(player.uniqueId(), builder);
        player.sendMessage(Messages.getZoneRegionBuilderEnabled());
        return CommandResult.success();
    }

    @Override
    public boolean hasPermission(CommandCause source) {
        if (!(source.subject() instanceof Player)) {
            return false;
        }
        return ArgumentCommand.super.hasPermission(source);
    }
}


