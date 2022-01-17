package org.zone.region.bounds.mode;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.math.vector.Vector3i;

/**
 * Used to wrap the shift. Does not changes the position
 */
public class BlockBoundMode implements BoundMode {
    @Override
    public @NotNull Location<? extends World<?, ?>, ?> shift(
            @NotNull Location<? extends World<?, ?>, ?> current, @NotNull Vector3i other) {
        return current;
    }

    @Override
    public @NotNull Location<? extends World<?, ?>, ?> shiftOther(
            Location<? extends World<?, ?>, ?> current, @NotNull Vector3i other) {
        return current;
    }
}
