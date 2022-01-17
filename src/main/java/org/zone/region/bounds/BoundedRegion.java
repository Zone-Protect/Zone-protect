package org.zone.region.bounds;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.World;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * A actual region that is between two points
 */
public class BoundedRegion implements Region {

    private @NotNull Vector3i position1;
    private @NotNull Vector3i position2;

    public BoundedRegion(@NotNull Vector3i position1, @NotNull Vector3i position2) {
        this.position1 = position1;
        this.position2 = position2;
    }

    public @NotNull Vector3i getPosition(@NotNull PositionType type) {
        return switch (type) {
            case ONE -> this.position1;
            case TWO -> this.position2;
        };
    }

    public void setPosition(@NotNull PositionType type, @NotNull Vector3i vector3i) {
        switch (type) {
            case ONE -> this.position1 = vector3i;
            case TWO -> this.position2 = vector3i;
        }
    }

    public @NotNull Vector3i getMin() {
        int minX = Math.min(this.position1.x(), this.position2.x());
        int minY = Math.min(this.position1.y(), this.position2.y());
        int minZ = Math.min(this.position1.z(), this.position2.z());
        return new Vector3i(minX, minY, minZ);
    }

    public @NotNull Vector3i getMax() {
        int maxX = Math.max(this.position1.x(), this.position2.x());
        int maxY = Math.max(this.position1.y(), this.position2.y());
        int maxZ = Math.max(this.position1.z(), this.position2.z());
        return new Vector3i(maxX, maxY, maxZ);
    }

    @Override
    public boolean contains(@NotNull Vector3d vector3d, boolean ignoreY) {
        Vector3i max = this.getMax();
        Vector3i min = this.getMin();
        if (!(min.x() <= vector3d.x() && max.x() >= vector3d.x())) {
            return false;
        }
        if (!ignoreY && !(min.y() <= vector3d.y() && max.y() >= vector3d.y())) {
            return false;
        }
        return min.z() <= vector3d.z() && max.z() >= vector3d.z();
    }

    @Override
    public Collection<? extends Entity> getEntities(World world) {
        return world.entities(AABB.of(this.position1, this.position2));
    }

    @Override
    public Optional<Vector3i> getNearestPosition(@NotNull Vector3i vector3i) {
        Vector3i min = this.getMin();
        Vector3i max = this.getMax();
        int x = vector3i.x();
        int y = vector3i.y();
        int z = vector3i.z();
        if (vector3i.x() < min.x()) {
            x = min.x();
        }
        if (vector3i.x() > max.x()) {
            x = max.x();
        }
        if (vector3i.y() < min.y()) {
            y = min.y();
        }
        if (vector3i.y() > max.y()) {
            y = max.y();
        }
        if (vector3i.z() < min.z()) {
            z = min.z();
        }
        if (vector3i.z() > max.z()) {
            z = max.z();
        }
        return Optional.of(new Vector3i(x, y, z));
    }

    @Override
    public void save(@NotNull ConfigurationNode node) throws SerializationException {
        BoundedRegion.save(node, this);
    }

    @Override
    @Deprecated
    public Collection<Region> getChildren() {
        return Collections.emptyList();
    }

    @Override
    @Deprecated
    public Collection<BoundedRegion> getTrueChildren() {
        return Collections.singleton(this);
    }

    public static BoundedRegion load(@NotNull ConfigurationNode node) {
        int p1x = node.node("pos1", "x").getInt();
        int p2x = node.node("pos2", "x").getInt();
        int p1y = node.node("pos1", "y").getInt();
        int p2y = node.node("pos2", "y").getInt();
        int p1z = node.node("pos1", "z").getInt();
        int p2z = node.node("pos2", "z").getInt();
        return new BoundedRegion(new Vector3i(p1x, p1y, p1z), new Vector3i(p2x, p2y, p2z));
    }

    public static void save(@NotNull ConfigurationNode node, @NotNull BoundedRegion region) throws
            SerializationException {
        node.node("pos1", "x").set(region.position1.x());
        node.node("pos1", "y").set(region.position1.y());
        node.node("pos1", "z").set(region.position1.z());
        node.node("pos2", "x").set(region.position2.x());
        node.node("pos2", "y").set(region.position2.y());
        node.node("pos2", "z").set(region.position2.z());
    }
}
