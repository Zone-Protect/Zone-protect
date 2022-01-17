package org.zone.region.bounds.mode;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.math.vector.Vector3i;

/**
 * Shifts the position to the closest edge of a chunk
 */
public class ChunkBoundMode implements BoundMode {
    @Override
    public @NotNull Location<? extends World<?, ?>, ?> shift(
            @NotNull Location<? extends World<?, ?>, ?> current, @NotNull Vector3i other) {
        Vector3i chunkPos = current.chunkPosition();
        WorldChunk chunk = current.world().chunk(chunkPos);
        Vector3i min = chunk.min();
        Vector3i max = chunk.max();

        Vector3i flatMin = new Vector3i(min.x(), current.y(), min.z());
        Vector3i flatMinOnX = new Vector3i(min.x(), current.y(), max.z());
        Vector3i flatMinOnZ = new Vector3i(max.x(), current.y(), min.z());
        Vector3i flatMax = new Vector3i(max.x(), current.y(), max.z());

        Vector3i[] compare = new Vector3i[]{flatMax, flatMin, flatMinOnX, flatMinOnZ};

        Vector3i best = null;
        int distance = 0;
        for (Vector3i vector : compare) {
            int distanceResult = other.distanceSquared(vector);
            if (best == null || distance < distanceResult) {
                best = vector;
                distance = distanceResult;
            }
        }
        return current.world().location(best);
    }

    @Override
    public @NotNull Location<? extends World<?, ?>, ?> shiftOther(
            Location<? extends World<?, ?>, ?> other, @NotNull Vector3i current) {
        Vector3i chunkPos = other.chunkPosition();
        WorldChunk chunk = other.world().chunk(chunkPos);
        Vector3i min = chunk.min();
        Vector3i max = chunk.max();

        Vector3i flatMin = new Vector3i(min.x(), other.y(), min.z());
        Vector3i flatMinOnX = new Vector3i(min.x(), other.y(), max.z());
        Vector3i flatMinOnZ = new Vector3i(max.x(), other.y(), min.z());
        Vector3i flatMax = new Vector3i(max.x(), other.y(), max.z());

        Vector3i[] compare = new Vector3i[]{flatMax, flatMin, flatMinOnX, flatMinOnZ};

        Vector3i best = null;
        int distance = 0;
        for (Vector3i vector : compare) {
            int distanceResult = current.distanceSquared(vector);
            if (best == null || distance < distanceResult) {
                best = vector;
                distance = distanceResult;
            }
        }
        return other.world().location(best);
    }
}
