package org.zone.region.flag;

import org.jetbrains.annotations.NotNull;
import org.zone.region.flag.entity.monster.move.PreventMonsterFlagType;
import org.zone.region.flag.entity.player.damage.attack.EntityDamagePlayerFlagType;
import org.zone.region.flag.entity.player.damage.fall.PlayerFallDamageFlagType;
import org.zone.region.flag.entity.player.interact.block.destroy.BlockBreakFlagType;
import org.zone.region.flag.entity.player.interact.block.place.BlockPlaceFlagType;
import org.zone.region.flag.entity.player.interact.door.DoorInteractionFlagType;
import org.zone.region.flag.entity.player.interact.itemframe.ItemFrameInteractFlagType;
import org.zone.region.flag.entity.player.move.greetings.GreetingsFlagType;
import org.zone.region.flag.entity.player.move.leaving.LeavingFlagType;
import org.zone.region.flag.entity.player.move.preventing.PreventPlayersFlagType;
import org.zone.region.flag.meta.eco.EcoFlagType;
import org.zone.region.flag.meta.edit.EditingFlagType;
import org.zone.region.flag.meta.member.MembersFlagType;
import org.zone.region.flag.meta.tag.TagsFlagType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * All known default flag types found within the zones plugin
 */
public final class FlagTypes {

    public static final PreventMonsterFlagType PREVENT_MONSTER = new PreventMonsterFlagType();
    public static final MembersFlagType MEMBERS = new MembersFlagType();
    public static final DoorInteractionFlagType DOOR_INTERACTION = new DoorInteractionFlagType();
    public static final BlockBreakFlagType BLOCK_BREAK = new BlockBreakFlagType();
    public static final EcoFlagType ECO = new EcoFlagType();
    public static final EditingFlagType EDITING = new EditingFlagType();
    public static final BlockPlaceFlagType BLOCK_PLACE = new BlockPlaceFlagType();
    public static final GreetingsFlagType GREETINGS = new GreetingsFlagType();
    public static final PreventPlayersFlagType PREVENT_PLAYERS = new PreventPlayersFlagType();
    public static final LeavingFlagType LEAVING = new LeavingFlagType();
    public static final TagsFlagType TAGS = new TagsFlagType();
    public static final ItemFrameInteractFlagType ITEM_FRAME_INTERACT = new ItemFrameInteractFlagType();
    public static final EntityDamagePlayerFlagType ENTITY_DAMAGE_PLAYER_FLAG_TYPE = new EntityDamagePlayerFlagType();
    public static final PlayerFallDamageFlagType PLAYER_FALL_DAMAGE_FLAG_TYPE = new PlayerFallDamageFlagType();

    private FlagTypes() {
        throw new RuntimeException("Should not init");
    }

    public static @NotNull Collection<FlagType<?>> getVanillaFlags() {
        return Arrays
                .stream(FlagTypes.class.getDeclaredFields())
                .parallel()
                .filter(field -> FlagType.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        return (FlagType<?>) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        //noinspection ReturnOfNull
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
