package org.zone.commands.structure.region.flags.members;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.sponge.UserArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.arguments.zone.ZoneGroupArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.group.DefaultGroups;
import org.zone.region.group.Group;
import org.zone.utils.Messages;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Used to set the member of a zone in {@link org.zone.region.flag.meta.member.MembersFlag}
 */
public class ZoneFlagMemberGroupAddCommand implements ArgumentCommand {

    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_MEMBER_CHANGE));
    public static final ZoneGroupArgument GROUP = new ZoneGroupArgument("groupId", ZONE);
    public static final UserArgument USER = new UserArgument("user");

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("member"),
                ZONE,
                new ExactArgument("set", false, "set", "change", "apply", "add"),
                USER,
                GROUP);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("Add a member to a group");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_MEMBER_CHANGE);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        Group group = commandContext.getArgument(this, GROUP);
        GameProfile profile = commandContext.getArgument(this, USER);

        this.addToGroup(commandContext, zone, group, profile);
        return CommandResult.success();
    }

    private void addToGroup(CommandContext context, Zone zone, Group group, GameProfile profile) {
        Group previous = zone.getMembers().getGroup(profile.uniqueId());
        if (!previous.equals(DefaultGroups.VISITOR)) {
            zone.getMembers().removeMember(profile.uniqueId());
        }
        context
                .getCause()
                .sendMessage(Identity.nil(),
                        Component
                                .text("Moved " +
                                        profile.name().orElse("Unknown name") +
                                        " from " +
                                        previous.getName() +
                                        " to " +
                                        group.getName())
                                .color(NamedTextColor.AQUA));
        if (Sponge.isServerAvailable()) {
            Optional<ServerPlayer> opPlayer = Sponge
                    .server()
                    .onlinePlayers()
                    .stream()
                    .filter(p -> p.uniqueId().equals(profile.uuid()))
                    .findAny();
            opPlayer.ifPresent(player -> player.sendMessage(Identity.nil(),
                    Component.text("You have been moved in '" +
                            zone.getName() +
                            "' from '" +
                            previous.getName() +
                            "' to '" +
                            group.getName() +
                            "'")));
        }

        zone.getMembers().addMember(group, profile.uniqueId());
        try {
            zone.save();
        } catch (IOException e) {
            e.printStackTrace();
            context.sendMessage(Messages.getFormattedErrorMessage(e.getMessage()));
        }
    }
}
