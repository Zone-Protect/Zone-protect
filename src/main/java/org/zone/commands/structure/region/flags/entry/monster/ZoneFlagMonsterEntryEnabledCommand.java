package org.zone.commands.structure.region.flags.entry.monster;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.configurate.ConfigurateException;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.simple.BooleanArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;
import org.zone.region.flag.entity.monster.move.PreventMonsterFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ZoneFlagMonsterEntryEnabledCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE_VALUE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_ENTRY_MONSTER_ENABLE));
    public static final BooleanArgument ENABLED = new BooleanArgument("enabledValue",
            "enable",
            "disable");

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("flag"),
                ZONE_VALUE,
                new ExactArgument("entry"),
                new ExactArgument("monster"),
                new ExactArgument("set"),
                ENABLED);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Command to enable/disable monster prevention flag");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_ENTRY_MONSTER_ENABLE);
    }

    @Override
    public @NotNull CommandResult run(
            @NotNull CommandContext commandContext, @NotNull String... args) {
        boolean enable = commandContext.getArgument(this, ENABLED);
        Zone zone = commandContext.getArgument(this, ZONE_VALUE);
        PreventMonsterFlag preventMonsterFlag = zone
                .getFlag(FlagTypes.PREVENT_MONSTER)
                .orElse(FlagTypes.PREVENT_MONSTER.createCopyOfDefault());
        if (enable) {
            zone.addFlag(preventMonsterFlag);
        } else {
            zone.removeFlag(FlagTypes.PREVENT_MONSTER);
        }
        try {
            zone.save();
            commandContext.sendMessage(Messages.getUpdatedMessage(FlagTypes.PREVENT_MONSTER));
        } catch (ConfigurateException ce) {
            return CommandResult.error(Messages.getZoneSavingError(ce));
        }
        return CommandResult.success();
    }
}