package org.zone.commands.system.arguments.zone;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;
import org.zone.region.Zone;
import org.zone.region.group.Group;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ZoneGroupArgument implements CommandArgument<Group> {

    private final @NotNull String id;
    private final @NotNull String zoneKey;

    public ZoneGroupArgument(@NotNull String id, @NotNull CommandArgument<Zone> zoneArgument) {
        this(id, zoneArgument.getId());
    }

    public ZoneGroupArgument(@NotNull String id, @NotNull String zoneKey) {
        this.id = id;
        this.zoneKey = zoneKey;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public @NotNull CommandArgumentResult<Group> parse(
            @NotNull CommandContext context, @NotNull CommandArgumentContext<Group> argument) throws
            IOException {
        Zone zone = context.getArgument(argument.getArgumentCommand(), this.zoneKey);
        Set<Group> groups = zone.getMembers().getGroups();
        String target = argument.getFocusArgument();
        Group ret = groups
                .parallelStream()
                .filter(group -> group.getId().equalsIgnoreCase(target))
                .findAny()
                .orElseThrow(() -> new IOException("Could not find group of " + target));
        return CommandArgumentResult.from(argument, ret);
    }

    @Override
    public @NotNull Collection<CommandCompletion> suggest(
            @NotNull CommandContext context, @NotNull CommandArgumentContext<Group> argument) {
        Zone zone = context.getArgument(argument.getArgumentCommand(), this.zoneKey);
        Set<Group> groups = zone.getMembers().getGroups();
        String target = argument.getFocusArgument().toLowerCase();
        return groups
                .parallelStream()
                .filter(group -> group.getId().toLowerCase().startsWith(target))
                .map(group -> CommandCompletion.of(group.getId(), Component.text(group.getName())))
                .collect(Collectors.toSet());
    }
}
