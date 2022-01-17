package org.zone.commands.structure.create.bounds.exact;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.zone.commands.structure.create.bounds.AbstractCreateZoneStartCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.operation.RemainingArgument;
import org.zone.commands.system.arguments.simple.StringArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.ZoneBuilder;
import org.zone.region.bounds.BoundedRegion;
import org.zone.region.bounds.mode.BoundMode;
import org.zone.region.bounds.mode.BoundModes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The command for zone bound creation start.
 * <p>Command: "/zone create bounds start 'name'"</p>
 */
public class ZoneCreateStartCommand extends AbstractCreateZoneStartCommand {

    private static final RemainingArgument<String> NAME = new RemainingArgument<>(new StringArgument(
            "name"));

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("create"),
                new ExactArgument("bounds"),
                new ExactArgument("block"),
                new ExactArgument("start"),
                NAME);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Create a zone via walking edge to edge");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.REGION_CREATE_BOUNDS_EXACT);
    }

    @Override
    protected String getNameArgument(CommandContext context) {
        return String.join(" ", context.getArgument(this, NAME));
    }

    @Override
    protected BoundMode getBoundMode() {
        return BoundModes.BLOCK;
    }

    @Override
    protected ZoneBuilder updateBuilder(
            CommandContext context, String name, BoundedRegion bounded, ZoneBuilder builder) {
        return builder;
    }
}
