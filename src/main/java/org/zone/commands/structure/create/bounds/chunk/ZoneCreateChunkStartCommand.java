package org.zone.commands.structure.create.bounds.chunk;

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

public class ZoneCreateChunkStartCommand extends AbstractCreateZoneStartCommand {

    public static final ExactArgument CREATE = new ExactArgument("create");
    public static final ExactArgument BOUNDS = new ExactArgument("bounds");
    public static final ExactArgument CHUNK = new ExactArgument("chunk");
    public static final ExactArgument START = new ExactArgument("start");
    public static final RemainingArgument<String> NAME = new RemainingArgument<>(new StringArgument(
            "name"));

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(CREATE, BOUNDS, CHUNK, START, NAME);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Creates a chunk zone");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.REGION_CREATE_BOUNDS_CHUNK);
    }

    @Override
    protected String getNameArgument(CommandContext context) {
        return String.join(" ", context.getArgument(this, NAME));
    }

    @Override
    protected BoundMode getBoundMode() {
        return BoundModes.CHUNK;
    }

    @Override
    protected ZoneBuilder updateBuilder(
            CommandContext context, String name, BoundedRegion bounded, ZoneBuilder builder) {
        return builder;
    }
}