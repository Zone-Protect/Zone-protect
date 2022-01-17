package org.zone.region.bounds;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;
import org.zone.region.Zone;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The region of the zone
 * The locations that the region covers
 */
public interface Region {

    /**
     * Checks if the position in the world is contained within this region
     *
     * @param location The block position to compare
     * @param ignoreY  true will ignore the height in the provided location
     *
     * @return true if the region contains that block position
     */
    boolean contains(@NotNull Vector3d location, boolean ignoreY);

    Collection<? extends Entity> getEntities(World<?, ?> world);

    /**
     * Gets the location within the region that is nearest to the provided block position
     *
     * @param vector3i The block position to compare
     *
     * @return The closes block position, if {@link Optional#empty()} then the closes could not be found. This is highly unlikely but is possible
     */
    Optional<Vector3i> getNearestPosition(@NotNull Vector3i vector3i);

    /**
     * Serializes the region to the provided node
     *
     * @param node The node to serialize this region to
     *
     * @throws SerializationException If the zone could not be serialized
     */
    void save(ConfigurationNode node) throws SerializationException;

    /**
     * Gets the children of this region
     *
     * @return The children of this region, the collection should be unmodifiable and can be any type of Collection
     */
    Collection<Region> getChildren();

    /**
     * Gets all the {@link BoundedRegion} found within this region. This is mainly for {@link ChildRegion}
     *
     * @return All the {@link BoundedRegion} within this region
     */
    default Collection<BoundedRegion> getTrueChildren() {
        Collection<Region> children = this.getChildren();
        Collection<BoundedRegion> trueChildren = children
                .parallelStream()
                .filter(region -> region instanceof BoundedRegion)
                .map(region -> (BoundedRegion) region)
                .collect(Collectors.toSet());
        Collection<BoundedRegion> childTrueChildren = children
                .parallelStream()
                .filter(region -> !(region instanceof BoundedRegion))
                .flatMap(region -> region.getTrueChildren().parallelStream())
                .collect(Collectors.toSet());
        trueChildren.addAll(childTrueChildren);
        return trueChildren;
    }

    /**
     * Checks if the provided position is contained within the region
     *
     * @param vector3i The block position to compare
     * @param ignoreY  if true, the height will be ignored in the check
     *
     * @return if true, the position is contained
     */
    default boolean contains(@NotNull Vector3i vector3i, boolean ignoreY) {
        return this.contains(vector3i.toDouble(), ignoreY);
    }

    /**
     * Checks if the provided position is contained within the region. Note that the world is not compared, use {@link Zone#inRegion(Location)} for the world to also be compared
     *
     * @param location The block position to be compared
     * @param ignoreY  if true, the height will be ignored in the chunk
     *
     * @return if true, the position is contained
     */
    default boolean contains(
            @NotNull Location<? extends World<?, ?>, ?> location, boolean ignoreY) {
        return this.contains(location.position(), ignoreY);
    }


}
