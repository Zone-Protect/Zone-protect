package org.zone.commands.structure.info;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.zone.ZonePlugin;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.utils.Messages;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ZonePluginInfoCommand implements ArgumentCommand {

    public static final ExactArgument PLUGIN = new ExactArgument("plugin");
    public static final ExactArgument INFO = new ExactArgument("info");

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(PLUGIN, INFO);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Command to show the plugin info");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.empty();
    }

    @Override
    public @NotNull CommandResult run(
            @NotNull CommandContext commandContext, @NotNull String... args) {
        String pluginName = ZonePlugin
                .getZonesPlugin()
                .getPluginContainer()
                .metadata()
                .name()
                .orElse(ZonePlugin.getZonesPlugin().getPluginContainer().metadata().id());
        String pluginVersion = ZonePlugin
                .getZonesPlugin()
                .getPluginContainer()
                .metadata()
                .version()
                .toString();
        String pluginGithub = "https://github.com/Zone-Protect/Zone-protect";
        String pluginZonesNumber = ZonePlugin
                .getZonesPlugin()
                .getZoneManager()
                .getZones()
                .stream()
                .filter(zone -> zone.getParentId().isEmpty())
                .count() + "";
        commandContext.sendMessage(Messages.getNameInfo(pluginName));
        commandContext.sendMessage(Messages.getVersionInfo(pluginVersion));
        commandContext.sendMessage(Messages.getSourceInfo(pluginGithub));
        commandContext.sendMessage(Messages.getZonesCountInfo(pluginZonesNumber));
        return CommandResult.success();
    }

}