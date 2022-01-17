package org.zone.region.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.plugin.PluginContainer;
import org.zone.region.group.key.GroupKey;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * Basic implementation of a group
 */
public class SimpleGroup implements Group {

    private final @NotNull String name;
    private final @NotNull String key;
    private final @NotNull PluginContainer plugin;
    private final boolean canBeRemoved;
    private final Collection<GroupKey> keys = new HashSet<>();
    private @Nullable Group parent;

    public SimpleGroup(
            @NotNull PluginContainer plugin, @NotNull String key, @NotNull Group parent) {
        this(plugin, key, key, parent);
    }

    public SimpleGroup(
            @NotNull PluginContainer plugin,
            @NotNull String key,
            @NotNull String name,
            @NotNull Group parent) {
        this(plugin, key, name, parent, true);
    }

    SimpleGroup(
            @NotNull PluginContainer plugin,
            @NotNull String key,
            @NotNull String name,
            @Nullable Group parent,
            boolean canRemove) {
        this.key = key;
        this.plugin = plugin;
        this.name = name;
        this.parent = parent;
        this.canBeRemoved = canRemove;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull PluginContainer getPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull String getKey() {
        return this.key;
    }

    @Override
    public @NotNull Optional<Group> getParent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public void setParent(@NotNull Group group) {
        this.parent = group;
    }

    @Override
    public boolean canBeRemoved() {
        return this.canBeRemoved;
    }

    @Override
    public @NotNull Collection<GroupKey> getKeys() {
        return this.keys;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Group group)) {
            return false;
        }
        return group.getId().equals(this.getId());
    }
}
