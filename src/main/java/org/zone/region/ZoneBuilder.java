package org.zone.region;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.volume.game.Region;
import org.spongepowered.plugin.PluginContainer;
import org.zone.region.bounds.ChildRegion;
import org.zone.region.bounds.mode.BoundMode;
import org.zone.region.bounds.mode.BoundModes;
import org.zone.region.bounds.payment.BoundsPayment;
import org.zone.region.flag.Flag;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * A Builder for the {@link Zone} class
 */
public class ZoneBuilder {

    private PluginContainer container;
    private ChildRegion region;
    private String key;
    private String name;
    private Collection<Flag> flags = new HashSet<>();
    private String parentId;
    private BoundMode boundMode;
    private ResourceKey world;
    private BoundsPayment payment;

    public BoundsPayment getPayment() {
        return this.payment;
    }

    public void setPayment(BoundsPayment payment) {
        this.payment = payment;
    }

    public ResourceKey getWorldKey() {
        return this.world;
    }

    public ZoneBuilder setWorld(ResourceKey world) {
        this.world = world;
        return this;
    }

    public ZoneBuilder setWorld(ServerWorld world) {
        this.world = world.key();
        return this;
    }

    public ZoneBuilder setWorld(Region<? extends World<?, ?>> world) {
        if (world instanceof ServerWorld sWorld) {
            return this.setWorld(sWorld);
        }
        this.world = world.worldType().key(RegistryTypes.WORLD_TYPE);
        return this;
    }

    public @NotNull BoundMode getBoundMode() {
        if (this.boundMode == null) {
            return BoundModes.BLOCK;
        }
        return this.boundMode;
    }

    public ZoneBuilder setBoundMode(@Nullable BoundMode mode) {
        this.boundMode = mode;
        return this;
    }

    public PluginContainer getContainer() {
        return this.container;
    }

    public ZoneBuilder setContainer(PluginContainer container) {
        this.container = container;
        return this;
    }

    public ChildRegion getRegion() {
        return this.region;
    }

    public ZoneBuilder setRegion(ChildRegion region) {
        this.region = region;
        return this;
    }

    public String getKey() {
        return this.key;
    }

    public ZoneBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ZoneBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public Collection<Flag> getFlags() {
        return this.flags;
    }

    public ZoneBuilder setFlags(Collection<Flag> flags) {
        this.flags = flags;
        return this;
    }

    public ZoneBuilder addFlags(Flag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public String getParentId() {
        return this.parentId;
    }

    public ZoneBuilder setParentId(String id) {
        this.parentId = id;
        return this;
    }

    public ZoneBuilder setParent(Zone parent) {
        this.parentId = parent.getId();
        parent.getWorldKey().ifPresent(this::setWorld);
        return this;
    }

    public Zone build() {
        return new Zone(this);
    }

}
