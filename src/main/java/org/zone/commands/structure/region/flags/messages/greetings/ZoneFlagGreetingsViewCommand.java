package org.zone.commands.structure.region.flags.messages.greetings;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.flag.FlagTypes;
import org.zone.region.flag.entity.player.move.greetings.GreetingsFlag;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ZoneFlagGreetingsViewCommand implements ArgumentCommand {
    public static final ExactArgument REGION = new ExactArgument("region");
    public static final ExactArgument FLAGS = new ExactArgument("flag");
    public static final ZoneArgument ZONE_VALUE = new ZoneArgument("zone_value",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_GREETINGS_VIEW));
    public static final ExactArgument GREETINGS = new ExactArgument("greetings");
    public static final ExactArgument VIEW = new ExactArgument("view");

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(REGION, FLAGS, ZONE_VALUE, GREETINGS, VIEW);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Command for viewing the greeting message of a specific zone");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_GREETINGS_VIEW);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE_VALUE);
        GreetingsFlag greetingsFlag = zone.getFlag(FlagTypes.GREETINGS).orElse(new GreetingsFlag());
        Component message = greetingsFlag.getMessage().orElse(Messages.getNoMessageSet());
        commandContext.sendMessage(Messages.getFlagMessageView(message));
        return CommandResult.success();
    }

}