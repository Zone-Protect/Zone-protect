package org.zone.region.flag;

import org.jetbrains.annotations.NotNull;
import org.zone.region.flag.entity.monster.move.PreventMonsterFlagType;
import org.zone.region.flag.entity.nonliving.block.farmland.FarmTramplingFlagType;
import org.zone.region.flag.entity.nonliving.block.tnt.TnTDefuseFlagType;
import org.zone.region.flag.entity.player.damage.attack.EntityDamagePlayerFlagType;
import org.zone.region.flag.entity.player.damage.fall.PlayerFallDamageFlagType;
import org.zone.region.flag.entity.player.damage.fire.PlayerFireDamageFlagType;
import org.zone.region.flag.entity.player.interact.block.destroy.BlockBreakFlagType;
import org.zone.region.flag.entity.player.interact.block.place.BlockPlaceFlagType;
import org.zone.region.flag.entity.player.interact.door.DoorInteractionFlagType;
import org.zone.region.flag.entity.player.interact.itemframe.ItemFrameInteractFlagType;
import org.zone.region.flag.entity.player.move.greetings.GreetingsFlagType;
import org.zone.region.flag.entity.player.move.leaving.LeavingFlagType;
import org.zone.region.flag.entity.player.move.preventing.PreventPlayersFlagType;
import org.zone.region.flag.meta.eco.balance.BalanceFlagType;
import org.zone.region.flag.meta.eco.payment.buy.BuyFlagType;
import org.zone.region.flag.meta.edit.EditingFlagType;
import org.zone.region.flag.meta.member.MembersFlagType;
import org.zone.region.flag.meta.request.join.JoinRequestFlagType;
import org.zone.region.flag.meta.request.visibility.ZoneVisibilityFlagType;
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
    public static final ZoneVisibilityFlagType ZONE_VISIBILITY = new ZoneVisibilityFlagType();
    public static final FarmTramplingFlagType FARM_TRAMPLING = new FarmTramplingFlagType();
    public static final JoinRequestFlagType JOIN_REQUEST = new JoinRequestFlagType();
    public static final MembersFlagType MEMBERS = new MembersFlagType();
    public static final DoorInteractionFlagType DOOR_INTERACTION = new DoorInteractionFlagType();
    public static final BlockBreakFlagType BLOCK_BREAK = new BlockBreakFlagType();
    public static final BalanceFlagType ECO = new BalanceFlagType();
    public static final EditingFlagType EDITING = new EditingFlagType();
    public static final BlockPlaceFlagType BLOCK_PLACE = new BlockPlaceFlagType();
    public static final GreetingsFlagType GREETINGS = new GreetingsFlagType();
    public static final PreventPlayersFlagType PREVENT_PLAYERS = new PreventPlayersFlagType();
    public static final LeavingFlagType LEAVING = new LeavingFlagType();
    public static final TagsFlagType TAGS = new TagsFlagType();
    public static final ItemFrameInteractFlagType ITEM_FRAME_INTERACT = new ItemFrameInteractFlagType();
    public static final PlayerFireDamageFlagType PLAYER_FIRE_DAMAGE_FLAG_TYPE = new PlayerFireDamageFlagType();
    public static final EntityDamagePlayerFlagType ENTITY_DAMAGE_PLAYER = new EntityDamagePlayerFlagType();
    public static final PlayerFallDamageFlagType PLAYER_FALL_DAMAGE = new PlayerFallDamageFlagType();
    public static final BuyFlagType BUY = new BuyFlagType();
    public static final TnTDefuseFlagType TNT_DEFUSE = new TnTDefuseFlagType();

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
                        // noinspection ReturnOfNull
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
