package org.zone.region.flag.meta.edit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector3i;
import org.zone.region.bounds.BoundedRegion;
import org.zone.region.bounds.PositionType;
import org.zone.region.bounds.mode.BoundMode;
import org.zone.region.flag.Flag;
import org.zone.region.flag.FlagTypes;

import java.util.Optional;
import java.util.UUID;

/**
 * Flag used to state that a zone is being modified
 */
public class EditingFlag implements Flag {

    private final @Nullable UUID playerEditing;
    private final @NotNull PositionType positionType;
    private final @NotNull BoundedRegion region;
    private final @NotNull BoundMode boundMode;
    private @NotNull Vector3i newPosition;

    public EditingFlag(
            @NotNull BoundedRegion region,
            @NotNull PositionType positionType,
            @NotNull Vector3i newPosition,
            @NotNull BoundMode boundMode,
            @Nullable UUID uuid) {
        this.playerEditing = uuid;
        this.positionType = positionType;
        this.newPosition = newPosition;
        this.region = region;
        this.boundMode = boundMode;
    }

    /**
     * Gets the shift for the zone
     *
     * @return The BoundMode for this editing state
     */
    public @NotNull BoundMode getBoundMode() {
        return this.boundMode;
    }

    /**
     * Gets the region being edited
     *
     * @return The BoundedRegion for this editing state
     */
    public @NotNull BoundedRegion getRegion() {
        return this.region;
    }

    /**
     * Gets the player editing the region
     *
     * @return The UUID of the player editing the region
     */
    public Optional<UUID> getPlayer() {
        return Optional.ofNullable(this.playerEditing);
    }

    /**
     * Gets the position type being used to edit
     *
     * @return The position type being edited
     */
    public @NotNull PositionType getPositionType() {
        return this.positionType;
    }

    /**
     * Gets the current new position to be
     *
     * @return The new position after editing
     */
    public @NotNull Vector3i getNewPosition() {
        return this.newPosition;
    }

    /**
     * Sets the current new position to be
     *
     * @param vector3i The new position to set
     */
    public void setNewPosition(@NotNull Vector3i vector3i) {
        this.newPosition = vector3i;
    }

    @Override
    public @NotNull EditingFlagType getType() {
        return FlagTypes.EDITING;
    }
}
