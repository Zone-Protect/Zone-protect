package org.zone.commands.structure;

import org.zone.ZonePlugin;
import org.zone.commands.structure.create.bounds.ZoneCreateEndCommand;
import org.zone.commands.structure.create.bounds.chunk.ZoneCreateChunkStartCommand;
import org.zone.commands.structure.create.bounds.chunk.ZoneCreateChunkSubStartCommand;
import org.zone.commands.structure.create.bounds.exact.ZoneCreateStartCommand;
import org.zone.commands.structure.create.bounds.exact.ZoneCreateSubStartCommand;
import org.zone.commands.structure.info.ZonePluginInfoCommand;
import org.zone.commands.structure.leave.LeaveZoneCommand;
import org.zone.commands.structure.region.flags.damage.attack.ZoneFlagDamageAttackSetEnabledCommand;
import org.zone.commands.structure.region.flags.damage.attack.ZoneFlagDamageAttackSetGroupCommand;
import org.zone.commands.structure.region.flags.damage.attack.ZoneFlagDamageAttackView;
import org.zone.commands.structure.region.flags.damage.fall.ZoneFlagPlayerFallDamageEnableDisable;
import org.zone.commands.structure.region.flags.damage.fall.ZoneFlagPlayerFallDamageSetGroup;
import org.zone.commands.structure.region.flags.damage.fall.ZoneFlagPlayerFallDamageView;
import org.zone.commands.structure.region.flags.eco.ZoneFlagViewBalanceCommand;
import org.zone.commands.structure.region.flags.entry.monster.ZoneFlagMonsterEntryEnabledCommand;
import org.zone.commands.structure.region.flags.entry.monster.ZoneFlagMonsterEntryViewCommand;
import org.zone.commands.structure.region.flags.entry.player.ZoneFlagPlayerEntrySetEnabledCommand;
import org.zone.commands.structure.region.flags.entry.player.ZoneFlagPlayerEntrySetGroupCommand;
import org.zone.commands.structure.region.flags.entry.player.ZoneFlagPlayerEntryViewCommand;
import org.zone.commands.structure.region.flags.interact.destroy.ZoneFlagBlockBreakSetEnabledCommand;
import org.zone.commands.structure.region.flags.interact.destroy.ZoneFlagBlockBreakSetGroupCommand;
import org.zone.commands.structure.region.flags.interact.destroy.ZoneFlagBlockBreakViewCommand;
import org.zone.commands.structure.region.flags.interact.door.ZoneFlagInteractDoorEnabledCommand;
import org.zone.commands.structure.region.flags.interact.door.ZoneFlagInteractDoorGroupCommand;
import org.zone.commands.structure.region.flags.interact.door.ZoneFlagInteractDoorViewCommand;
import org.zone.commands.structure.region.flags.interact.itemframe.ZoneFlagInteractItemframesEnableDisableCommand;
import org.zone.commands.structure.region.flags.interact.itemframe.ZoneFlagInteractItemframesGroupCommand;
import org.zone.commands.structure.region.flags.interact.itemframe.ZoneFlagInteractItemframesViewCommand;
import org.zone.commands.structure.region.flags.interact.place.ZoneFlagBlockPlaceSetEnabledCommand;
import org.zone.commands.structure.region.flags.interact.place.ZoneFlagBlockPlaceSetGroupCommand;
import org.zone.commands.structure.region.flags.interact.place.ZoneFlagBlockPlaceViewCommand;
import org.zone.commands.structure.region.flags.members.ZoneFlagMemberGroupAddCommand;
import org.zone.commands.structure.region.flags.members.ZoneFlagMemberGroupViewCommand;
import org.zone.commands.structure.region.flags.messages.greetings.ZoneFlagGreetingsRemoveCommand;
import org.zone.commands.structure.region.flags.messages.greetings.ZoneFlagGreetingsSetMessageCommand;
import org.zone.commands.structure.region.flags.messages.greetings.ZoneFlagGreetingsViewCommand;
import org.zone.commands.structure.region.flags.messages.leaving.ZoneFlagLeavingRemoveCommand;
import org.zone.commands.structure.region.flags.messages.leaving.ZoneFlagLeavingSetMessageCommand;
import org.zone.commands.structure.region.flags.messages.leaving.ZoneFlagLeavingViewCommand;
import org.zone.commands.structure.region.info.ZoneInfoCommand;
import org.zone.commands.structure.region.info.bounds.ZoneInfoBoundsShowCommand;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A list of all known commands within the Zones plugin
 */
@SuppressWarnings("unused")
public interface ZoneCommands {

    ZonePluginInfoCommand ZONE_PLUGIN_INFO_COMMAND = new ZonePluginInfoCommand();
    ZoneCreateEndCommand ZONE_CREATE_END = new ZoneCreateEndCommand();
    ZoneCreateStartCommand ZONE_CREATE_START_COMMAND = new ZoneCreateStartCommand();
    ZoneCreateSubStartCommand ZONE_CREATE_SUB_START_COMMAND = new ZoneCreateSubStartCommand();
    ZoneInfoBoundsShowCommand ZONE_INFO_BOUNDS_SHOW_COMMAND = new ZoneInfoBoundsShowCommand();
    ZoneCreateChunkStartCommand ZONE_CREATE_CHUNK_START_COMMAND = new ZoneCreateChunkStartCommand();
    ZoneCreateChunkSubStartCommand ZONE_CREATE_CHUNK_SUB_START_COMMAND = new ZoneCreateChunkSubStartCommand();
    ZoneFlagInteractDoorEnabledCommand ZONE_FLAG_INTERACT_DOOR_ENABLED_COMMAND = new ZoneFlagInteractDoorEnabledCommand();
    ZoneFlagInteractDoorGroupCommand ZONE_FLAG_INTERACT_DOOR_GROUP_COMMAND = new ZoneFlagInteractDoorGroupCommand();
    ZoneFlagInteractDoorViewCommand ZONE_FLAG_INTERACT_DOOR_VIEW_COMMAND = new ZoneFlagInteractDoorViewCommand();
    ZoneFlagMemberGroupAddCommand ZONE_FLAG_MEMBER_GROUP_ADD_COMMAND = new ZoneFlagMemberGroupAddCommand();
    ZoneFlagMemberGroupViewCommand ZONE_FLAG_MEMBER_GROUP_VIEW_COMMAND = new ZoneFlagMemberGroupViewCommand();
    ZoneFlagBlockBreakSetEnabledCommand ZONE_FLAG_BLOCK_BREAK_SET_ENABLED_COMMAND = new ZoneFlagBlockBreakSetEnabledCommand();
    ZoneFlagBlockBreakViewCommand ZONE_FLAG_BLOCK_BREAK_VIEW_COMMAND = new ZoneFlagBlockBreakViewCommand();
    ZoneFlagBlockBreakSetGroupCommand ZONE_FLAG_BLOCK_BREAK_SET_GROUP_COMMAND = new ZoneFlagBlockBreakSetGroupCommand();
    ZoneFlagBlockPlaceSetEnabledCommand ZONE_FLAG_BLOCK_PLACE_SET_ENABLED_COMMAND = new ZoneFlagBlockPlaceSetEnabledCommand();
    ZoneFlagBlockPlaceViewCommand ZONE_FLAG_BLOCK_PLACE_VIEW_COMMAND = new ZoneFlagBlockPlaceViewCommand();
    ZoneFlagBlockPlaceSetGroupCommand ZONE_FLAG_BLOCK_PLACE_SET_GROUP_COMMAND = new ZoneFlagBlockPlaceSetGroupCommand();
    ZoneFlagViewBalanceCommand ZONE_FLAG_VIEW_BALANCE_COMMAND = new ZoneFlagViewBalanceCommand();
    ZoneFlagGreetingsSetMessageCommand ZONE_FLAG_GREETINGS_MESSAGE_COMMAND = new ZoneFlagGreetingsSetMessageCommand();
    ZoneFlagGreetingsViewCommand ZONE_FLAG_GREETINGS_VIEW_COMMAND = new ZoneFlagGreetingsViewCommand();
    ZoneFlagInteractItemframesViewCommand ZONE_FLAG_INTERACT_ITEMFRAMES_VIEW_COMMAND = new ZoneFlagInteractItemframesViewCommand();

    ZoneFlagPlayerFallDamageSetGroup ZONE_FLAG_PLAYER_FALL_DAMAGE_SET_GROUP = new ZoneFlagPlayerFallDamageSetGroup();
    ZoneFlagDamageAttackSetGroupCommand ZONE_FLAG_DAMAGE_ATTACK_SET_GROUP_COMMAND = new ZoneFlagDamageAttackSetGroupCommand();
    ZoneFlagMonsterEntryViewCommand ZONE_FLAG_MONSTER_ENTRY_VIEW_COMMAND = new ZoneFlagMonsterEntryViewCommand();
    ZoneFlagMonsterEntryEnabledCommand ZONE_FLAG_MONSTER_ENTRY_ENABLED_COMMAND = new ZoneFlagMonsterEntryEnabledCommand();
    ZoneFlagPlayerEntryViewCommand ZONE_FLAG_ENTRY_VIEW_COMMAND = new ZoneFlagPlayerEntryViewCommand();
    ZoneFlagPlayerEntrySetGroupCommand ZONE_FLAG_ENTRY_SET_GROUP_COMMAND = new ZoneFlagPlayerEntrySetGroupCommand();
    ZoneFlagDamageAttackView ZONE_FLAG_DAMAGE_ATTACK_VIEW = new ZoneFlagDamageAttackView();
    ZoneFlagPlayerFallDamageView ZONE_FLAG_PLAYER_FALL_DAMAGE_VIEW = new ZoneFlagPlayerFallDamageView();
    ZoneFlagPlayerFallDamageEnableDisable ZONE_FLAG_PLAYER_FALL_DAMAGE_ENABLE_DISABLE = new ZoneFlagPlayerFallDamageEnableDisable();
    ZoneFlagDamageAttackSetEnabledCommand ZONE_FLAG_DAMAGE_ATTACK_SET_ENABLED_COMMAND = new ZoneFlagDamageAttackSetEnabledCommand();
    ZoneFlagInteractItemframesEnableDisableCommand ZONE_FLAG_INTERACT_ITEMFRAMES_ENABLE_DISABLE_COMMAND = new ZoneFlagInteractItemframesEnableDisableCommand();
    ZoneFlagInteractItemframesGroupCommand ZONE_FLAG_INTERACT_ITEMFRAMES_GROUP_COMMAND = new ZoneFlagInteractItemframesGroupCommand();
    ZoneFlagGreetingsRemoveCommand ZONE_FLAG_GREETINGS_REMOVE_COMMAND = new ZoneFlagGreetingsRemoveCommand();
    ZoneFlagPlayerEntrySetEnabledCommand ZONE_FLAG_PREVENTION_PLAYER_ENABLED_COMMAND = new ZoneFlagPlayerEntrySetEnabledCommand();
    ZoneFlagLeavingRemoveCommand ZONE_FLAG_LEAVING_REMOVE_COMMAND = new ZoneFlagLeavingRemoveCommand();
    ZoneFlagLeavingSetMessageCommand ZONE_FLAG_LEAVING_SET_MESSAGE_COMMAND = new ZoneFlagLeavingSetMessageCommand();
    ZoneFlagLeavingViewCommand ZONE_FLAG_LEAVING_VIEW_COMMAND = new ZoneFlagLeavingViewCommand();
    ZoneInfoCommand ZONE_INFO_COMMAND = new ZoneInfoCommand();
    LeaveZoneCommand ZONE_LEAVE_COMMAND = new LeaveZoneCommand();

    static ZoneSpongeCommand createCommand() {
        Collection<ArgumentCommand> collection = Arrays
                .stream(ZoneCommands.class.getDeclaredFields())
                .parallel()
                .filter(field -> ArgumentCommand.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        return (ArgumentCommand) field.get(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        //noinspection ReturnOfNull
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for (ArgumentCommand arg : collection) {
            for (ArgumentCommand argCmd : collection) {
                if (argCmd.equals(arg)) {
                    continue;
                }
                String argNode = arg
                        .getArguments()
                        .parallelStream()
                        .map(CommandArgument::getId)
                        .collect(Collectors.joining("."));
                String argCmdNode = argCmd
                        .getArguments()
                        .parallelStream()
                        .map(CommandArgument::getId)
                        .collect(Collectors.joining("."));
                if (argNode.equals(argCmdNode)) {
                    ZonePlugin
                            .getZonesPlugin()
                            .getLogger()
                            .error("The two commands have the same:");
                    ZonePlugin
                            .getZonesPlugin()
                            .getLogger()
                            .error(" - " + arg.getClass().getSimpleName());
                    ZonePlugin
                            .getZonesPlugin()
                            .getLogger()
                            .error(" - " + argCmd.getClass().getSimpleName());
                    ZonePlugin.getZonesPlugin().getLogger().error(" -/- (" + argNode + ")");
                    ZonePlugin.getZonesPlugin().getLogger().error(" -/- (" + argCmdNode + ")");

                    break;
                }
            }
        }
        return new ZoneSpongeCommand(collection);
    }
}
