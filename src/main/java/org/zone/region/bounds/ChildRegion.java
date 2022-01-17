package org.zone.region.bounds;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A region that holds only other regions, the other regions can be any type of region such as another child or a real region
 */
public class ChildRegion implements Region {

    private final @NotNull Collection<Region> bounds = new HashSet<>();

    public ChildRegion() {
        this(Collections.emptyList());
    }

    public ChildRegion(@NotNull Collection<? extends Region> bounds) {
        this.bounds.addAll(bounds);
    }

    /**
     * Adds a region to this region
     *
     * @param region The region to add
     */
    public void add(@NotNull Region region) {
        this.bounds.add(region);
    }

    /**
     * Removes a region from this region
     *
     * @param region The region to remove
     */
    public void remove(@NotNull Region region) {
        this.bounds.remove(region);
    }

    @Override
    public boolean contains(@NotNull Vector3d location, boolean ignoreY) {
        return this.bounds.stream().anyMatch(bound -> bound.contains(location, ignoreY));
    }

    @Override
    public Collection<? extends Entity> getEntities(World<?, ?> world) {
        return this.bounds
                .stream()
                .flatMap(region -> region.getEntities(world).stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull Optional<Vector3i> getNearestPosition(@NotNull Vector3i vector3i) {
        Set<Vector3i> nearestPositions = this
                .getTrueChildren()
                .stream()
                .map(region -> region.getNearestPosition(vector3i))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        Vector3i closes = null;
        double closesDistance = Double.MAX_VALUE;

        for (Vector3i position : nearestPositions) {
            double distance = vector3i.distance(position);
            if (closesDistance < distance) {
                closesDistance = distance;
                closes = position;
            }
        }
        return Optional.ofNullable(closes);
    }

    @Override
    public void save(@NotNull ConfigurationNode node) throws SerializationException {
        ChildRegion.save(node, this);
    }

    @Override
    public @NotNull Collection<Region> getChildren() {
        return Collections.unmodifiableCollection(this.bounds);
    }

    public static @NotNull ChildRegion load(@NotNull ConfigurationNode node) {
        Collection<? extends ConfigurationNode> children = node.childrenMap().values();
        Set<Region> regions = new HashSet<>();

        for (ConfigurationNode childNode : children) {
            if (childNode.node("pos1").virtual()) {
                regions.add(ChildRegion.load(childNode));
                continue;
            }
            regions.add(BoundedRegion.load(childNode));
        }
        return new ChildRegion(regions);
    }

    public static void save(
            @NotNull ConfigurationNode node,
            @SuppressWarnings("TypeMayBeWeakened") @NotNull ChildRegion region) throws
            SerializationException {
        for (Region child : region.getChildren()) {
            child.save(node.node("" + child.hashCode()));
        }
    }
}
