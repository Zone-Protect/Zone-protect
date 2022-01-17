package org.zone.region.bounds.mode;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.math.vector.Vector3i;

/**
 * A interact designed to shift the position of a creating/editing zone.
 * <p>
 * A example of this would be that if a chunk is required, a BoundMode which shifts to the nearest chunk edge would be used
 */
public interface BoundMode {

    /**
     * Shifts the location
     *
     * @param current The current location that should be shifted
     * @param other   The other point within the Zone (such as if the current is {@link org.zone.region.bounds.PositionType#ONE} then the other would be {@link org.zone.region.bounds.PositionType#TWO}
     *
     * @return The shifted position
     */
    @NotNull Location<? extends World<?, ?>, ?> shift(
            @NotNull Location<? extends World<?, ?>, ?> current, @NotNull Vector3i other);

    @NotNull Location<? extends World<?, ?>, ?> shiftOther(
            Location<? extends World<?, ?>, ?> other, @NotNull Vector3i current);
}
