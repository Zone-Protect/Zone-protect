package org.zone.region;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.plugin.PluginContainer;
import org.zone.Identifiable;
import org.zone.ZonePlugin;
import org.zone.event.zone.FlagChangeEvent;
import org.zone.region.bounds.ChildRegion;
import org.zone.region.flag.Flag;
import org.zone.region.flag.FlagType;
import org.zone.region.flag.FlagTypes;
import org.zone.region.flag.meta.eco.EcoFlag;
import org.zone.region.flag.meta.member.MembersFlag;
import org.zone.region.flag.meta.tag.TagsFlag;

import java.util.*;

/**
 * A area that follows specific rules
 */
public class Zone implements Identifiable {

    private final @NotNull PluginContainer container;
    private final @NotNull ChildRegion region;
    private final @NotNull String key;
    private final @NotNull String name;
    private final @NotNull Collection<Flag> flags = new TreeSet<>(Comparator.comparing(flag -> flag
            .getType()
            .getId()));
    private final @Nullable String parentId;
    private final @Nullable ResourceKey world;

    public Zone(@NotNull ZoneBuilder builder) {
        this.parentId = builder.getParentId();
        this.flags.addAll(builder.getFlags());
        this.name = builder.getName();
        this.key = builder.getKey();
        this.region = builder.getRegion();
        this.container = builder.getContainer();
        this.world = builder.getWorldKey();
        if (this.world == null && !Sponge.isClientAvailable()) {
            throw new IllegalArgumentException("World is needed to be set when in server mode");
        }
    }

    /**
     * Gets the world that the zone is in. If this returns {@link Optional#empty()} then use the
     * default world on the client
     *
     * @return The world key
     */
    public Optional<ResourceKey> getWorldKey() {
        return Optional.ofNullable(this.world);
    }

    /**
     * Gets the world that the zone is in. If this returns {@link Optional#empty()} then the
     * world is not loaded.
     *
     * @return The world object
     */
    public Optional<? extends World<?, ?>> getWorld() {
        if (this.world == null) {
            return Sponge.client().world();
        }
        return Sponge.server().worldManager().world(this.world);
    }

    /**
     * Gets the parent to this zone. If the zone is a sub region then it will have a parent, if
     * the zone is a regular zone then it wont. Note use {@link Zone#getParentId()} where
     * possible as this searches for the zone
     *
     * @return The parent zone
     */
    public @NotNull Optional<Zone> getParent() {
        return ZonePlugin.getZonesPlugin().getZoneManager().getZone(this.parentId);
    }

    /**
     * Gets the ID of the parent zone. If the zone is a sub region then it will have a parent, if
     * the zone is a regular zone then it wont.
     *
     * @return The id of the parent zone
     */
    public @NotNull Optional<String> getParentId() {
        return Optional.ofNullable(this.parentId);
    }

    /**
     * Checks if the exact instance of a flag is registered to the zone
     *
     * @param flag The instance to check
     *
     * @return true if the flag is present
     */
    public boolean containsFlag(@NotNull Flag flag) {
        if (this.flags.contains(flag)) {
            return true;
        }
        //noinspection SuspiciousMethodCalls
        return this.getTags().getTags().contains(flag);
    }

    /**
     * Checks if the type of flag is found within the flags of this zone
     *
     * @param type the type of flag
     *
     * @return true if a flag has the same type as provided
     */
    public boolean containsFlag(@NotNull FlagType<?> type) {
        if (this.flags.parallelStream().anyMatch(flag -> flag.getType().equals(type))) {
            return true;
        }
        if (type instanceof FlagType.TaggedFlagType tagType) {
            //noinspection unchecked
            return this.getTags().getTag(tagType).isPresent();
        }
        return false;
    }

    /**
     * Gets the flags of this zone
     *
     * @return A collection of the flags that zone has
     */
    public @NotNull Collection<Flag> getFlags() {
        return Collections.unmodifiableCollection(this.flags);
    }

    /**
     * Removes a flag
     *
     * @param type The flag type to remove
     *
     * @return if the flag was removed
     */
    public boolean removeFlag(@NotNull FlagType<?> type) {
        return this.removeFlag(type, true);
    }

    private boolean removeFlag(@NotNull FlagType<?> type, boolean runEvent) {
        if (runEvent) {
            FlagChangeEvent.RemoveFlag removeFlag = new FlagChangeEvent.RemoveFlag(this,
                    type,
                    Cause.builder().append(type).append(this).build());
            Sponge.eventManager().post(removeFlag);
            if (removeFlag.isCancelled()) {
                return false;
            }
        }

        Optional<Flag> opFlag = this.flags
                .parallelStream()
                .filter(flag -> type.getId().equals(flag.getType().getId()))
                .findFirst();

        if (opFlag.isEmpty()) {
            if (type instanceof FlagType.TaggedFlagType tagType) {
                this.getTags().removeTag(tagType);
                return true;
            }
            return false;
        }
        return this.flags.remove(opFlag.get());
    }

    /**
     * Adds a flag to this zone
     *
     * @param flag The flag to add
     *
     * @return If the flag was added
     */
    public boolean addFlag(@NotNull Flag flag) {
        return this.addFlag(flag, true);
    }

    private boolean addFlag(@NotNull Flag flag, boolean runEvent) {
        if (runEvent) {
            FlagChangeEvent.AddFlag addFlag = new FlagChangeEvent.AddFlag(this,
                    flag,
                    Cause.builder().append(flag).append(this).build());
            Sponge.eventManager().post(addFlag);
            if (addFlag.isCancelled()) {
                return false;
            }
        }

        if (flag instanceof Flag.TaggedFlag tag) {
            return this.getTags().addTag(tag);
        }
        return this.flags.add(flag);
    }

    /**
     * Overrides the flag with the provided flag, this is used to prevent your new flag not being
     * applied
     *
     * @param flag The flag to add
     *
     * @return If the flag was added
     */
    public boolean setFlag(@NotNull Flag flag) {
        Optional<?> opFlag = this.getFlag(flag.getType());
        if (opFlag.isPresent()) {
            FlagChangeEvent.UpdateFlag event = new FlagChangeEvent.UpdateFlag(this,
                    (Flag) opFlag.get(),
                    flag,
                    Cause.builder().append(flag).append(this).build());
            Sponge.eventManager().post(event);
            if (event.isCancelled()) {
                return false;
            }
            this.removeFlag(flag.getType(), false);
        }
        return this.addFlag(flag, opFlag.isEmpty());
    }

    /**
     * Gets the regions that the zone covers. A zone can cover multiple regions at once
     *
     * @return The region the zone is covering
     */
    public @NotNull ChildRegion getRegion() {
        return this.region;
    }

    /**
     * saves the zone
     *
     * @throws ConfigurateException If the zone cannot be saved
     */
    public void save() throws ConfigurateException {
        ZonePlugin.getZonesPlugin().getZoneManager().save(this);
    }

    /**
     * Gets the flag from the type
     *
     * @param type The FlagType
     * @param <F>  the class of the flag
     * @param <T>  the class of the flag type
     *
     * @return The flag that is assigned to the type, if {@link Optional#empty()} then the flag
     * is not applied to this zone
     */
    public <F extends Flag, T extends FlagType<F>> @NotNull Optional<F> getFlag(@NotNull T type) {
        Optional<F> opFlag = this
                .getFlags()
                .parallelStream()
                .filter(flag -> flag.getType().equals(type))
                .map(flag -> (F) flag)
                .findFirst();
        if (opFlag.isPresent()) {
            return opFlag;
        }
        if (type instanceof FlagType.TaggedFlagType<?> tagType) {
            return this.getTags().getTag(tagType).map(tag -> (F) tag);
        }
        Optional<F> defaultFlag = ZonePlugin
                .getZonesPlugin()
                .getFlagManager()
                .getDefaultFlags()
                .loadDefault(type);
        defaultFlag.ifPresent(f -> this.addFlag(f, false));
        return defaultFlag;
    }

    /**
     * Gets the member flag of this zone. This is a flag that should never be removed from the zone
     *
     * @return The membersFlag
     */
    public MembersFlag getMembers() {
        //noinspection no-member-method
        return this
                .getFlag(FlagTypes.MEMBERS)
                .orElseThrow(() -> new IllegalStateException("MembersFlag is missing in zone: " +
                        this.getId()));
    }

    /**
     * Gets the economy flag of this zone. This is a flag that should never be removed from the
     * zone
     *
     * @return The economy flag
     */
    public EcoFlag getEconomy() {
        //noinspection no-eco-method
        @NotNull Optional<EcoFlag> opEco = this.getFlag(FlagTypes.ECO);
        if (opEco.isPresent()) {
            return opEco.get();
        }
        EcoFlag flag = new EcoFlag();
        this.addFlag(flag, false);
        return flag;
    }

    public TagsFlag getTags() {
        @NotNull Optional<TagsFlag> opTag = this.getFlag(FlagTypes.TAGS);
        if (opTag.isPresent()) {
            return opTag.get();
        }
        TagsFlag flag = new TagsFlag();
        this.addFlag(flag, false);
        return flag;
    }


    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull PluginContainer getPlugin() {
        return this.container;
    }

    @Override
    public @NotNull String getKey() {
        return this.key;
    }

    /**
     * Checks if the provided information is within the zone
     *
     * @param world    The world to compare
     * @param vector3i the block position to compare
     *
     * @return true if found within the zone
     */
    public boolean inRegion(@Nullable World<?, ?> world, @NotNull Vector3d vector3i) {
        if (world != null) {
            Optional<? extends World<?, ?>> opWorld = this.getWorld();
            if (opWorld.isPresent() && !opWorld.get().equals(world)) {
                return false;
            }
        }
        return this.getRegion().contains(vector3i, this.getParent().isEmpty());
    }

    /**
     * Checks if the provided location is within the zone. This does compare the position and the
     * world
     *
     * @param location The location to compare
     *
     * @return true if found within the zone
     */
    public boolean inRegion(@NotNull Location<?, ?> location) {
        return this.inRegion(location.world(), location.position());
    }

    /**
     * Checks if the provided locatable (such as a player) is within the zone. This does compare
     * the position and the world
     *
     * @param locatable The locatable to compare
     *
     * @return true if found within the zone
     */
    public boolean inRegion(@NotNull Locatable locatable) {
        return this.inRegion(locatable.location());
    }
}
