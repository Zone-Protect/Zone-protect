package org.zone.commands.system.arguments.zone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.permission.Subject;
import org.zone.Identifiable;
import org.zone.ZonePlugin;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.GUICommandArgument;
import org.zone.commands.system.ParseCommandArgument;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.region.Zone;
import org.zone.region.group.Group;
import org.zone.region.group.key.GroupKey;
import org.zone.region.group.key.GroupKeys;
import org.zone.utils.Messages;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZoneArgument implements GUICommandArgument<Zone> {

    private final @NotNull ZoneArgumentPropertiesBuilder builder;
    private final @NotNull String id;

    public static class ZoneArgumentPropertiesBuilder {

        private @Nullable ParseCommandArgument<Zone> subZoneTo;
        private boolean onlyMainZones = true;
        private boolean onlyPartOf;
        private @Nullable ZonePermission bypassSuggestionPermission;
        private @Nullable GroupKey level = GroupKeys.OWNER;

        public boolean isLimitedToOnlyPartOf() {
            return this.onlyPartOf;
        }

        public ZoneArgumentPropertiesBuilder setLimitedToOnlyPartOf(boolean check) {
            this.onlyPartOf = check;
            return this;
        }

        public @Nullable ZonePermission getBypassSuggestionPermission() {
            return this.bypassSuggestionPermission;
        }

        public ZoneArgumentPropertiesBuilder setBypassSuggestionPermission(ZonePermission bypassSuggestionPermission) {
            this.bypassSuggestionPermission = bypassSuggestionPermission;
            return this;
        }

        public @Nullable ParseCommandArgument<Zone> getSubZoneTo() {
            return this.subZoneTo;
        }

        public ZoneArgumentPropertiesBuilder setSubZoneTo(ParseCommandArgument<Zone> subZoneTo) {
            this.subZoneTo = subZoneTo;
            return this;
        }

        public boolean isOnlyMainZones() {
            return this.onlyMainZones;
        }

        public ZoneArgumentPropertiesBuilder setOnlyMainZones(boolean onlyMainZones) {
            this.onlyMainZones = onlyMainZones;
            return this;
        }

        public @Nullable GroupKey getLevel() {
            return this.level;
        }

        public ZoneArgumentPropertiesBuilder setLevel(@Nullable GroupKey level) {
            this.level = level;
            return this;
        }
    }

    public ZoneArgument(@NotNull String id) {
        this(id, new ZoneArgumentPropertiesBuilder());
    }

    public ZoneArgument(@NotNull String id, @NotNull ZoneArgumentPropertiesBuilder builder) {
        this.id = id;
        this.builder = builder;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public boolean canApply(@NotNull CommandContext context) {
        return !this.suggest(context, "").isEmpty();
    }

    @Override
    public CommandArgumentResult<Zone> parse(
            @NotNull CommandContext context, @NotNull CommandArgumentContext<Zone> argument) throws
            IOException {
        Zone result = this
                .getZones(context)
                .filter(zone -> zone.getId().equalsIgnoreCase(argument.getFocusArgument()))
                .findAny()
                .orElseThrow(() -> new IOException("Could not find zone of " +
                        argument.getFocusArgument()));
        return CommandArgumentResult.from(argument, result);
    }

    @Override
    public @NotNull Collection<CommandCompletion> suggest(
            @NotNull CommandContext context, @NotNull CommandArgumentContext<Zone> argument) {
        return this.suggest(context, argument.getFocusArgument());
    }

    private ItemStack getItem(Zone zone) {
        return ItemStack
                .builder()
                .quantity(1)
                .itemType(ItemTypes.CAKE)
                .add(Keys.DISPLAY_NAME, Component.text(zone.getName()))
                .add(Keys.LORE, List.of(Component.text(zone.getId()).color(NamedTextColor.AQUA)))
                .build();
    }

    private Stream<Zone> getZones(CommandContext context) {
        Collection<Zone> collection = ZonePlugin.getZonesPlugin().getZoneManager().getZones();
        Stream<Zone> zones = collection.stream();
        if (this.builder.isOnlyMainZones()) {
            zones = zones.filter(zone -> zone.getParent().isEmpty());
        }
        if (this.builder.isLimitedToOnlyPartOf() && context.getSource() instanceof Player player) {
            zones = zones.filter(zone -> zone
                    .getMembers()
                    .getMembers()
                    .contains(player.uniqueId()));
        }
        if (this.builder.getLevel() != null) {
            Subject subject = context.getSource();
            if (this.builder.getBypassSuggestionPermission() == null ||
                    this.builder.getBypassSuggestionPermission() != null &&
                            !this.builder.getBypassSuggestionPermission().hasPermission(subject)) {
                if (subject instanceof Player player) {
                    zones = zones.filter(zone -> {
                        Group group = zone.getMembers().getGroup(player.uniqueId());
                        if (this.builder.getLevel() == null) {
                            return true;
                        }
                        return group.contains(this.builder.getLevel());
                    });
                }
            }
        }
        return zones;
    }

    private Collection<CommandCompletion> suggest(
            @NotNull CommandContext context, @NotNull String focus) {

        return this
                .getZones(context)
                .filter(zone -> zone.getId().toLowerCase().startsWith(focus.toLowerCase()))
                .map(zone -> CommandCompletion.of(zone.getId(), Messages.getZoneNameInfo(zone)))
                .collect(Collectors.toSet());
    }

    @Override
    public Map<ItemStack, String> createMenuOptions(CommandContext context) {
        return this.getZones(context).collect(Collectors.toMap(this::getItem, Identifiable::getId));
    }
}
