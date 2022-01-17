package org.zone.commands.structure.region.flags.members;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.user.UserManager;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.arguments.operation.ExactArgument;
import org.zone.commands.system.arguments.operation.OptionalArgument;
import org.zone.commands.system.arguments.simple.number.IntegerArgument;
import org.zone.commands.system.arguments.simple.number.RangeArgument;
import org.zone.commands.system.arguments.zone.ZoneArgument;
import org.zone.commands.system.arguments.zone.ZoneGroupArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.permissions.ZonePermission;
import org.zone.permissions.ZonePermissions;
import org.zone.region.Zone;
import org.zone.region.group.Group;
import org.zone.utils.Messages;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Used to view the member of a zone in {@link org.zone.region.flag.meta.member.MembersFlag}
 */
public class ZoneFlagMemberGroupViewCommand implements ArgumentCommand {

    public final OptionalArgument<Integer> PAGE = new OptionalArgument<>(new RangeArgument<>(new IntegerArgument(
            "page"), (c, a) -> CommandArgumentResult.from(a, 1), (c, a) -> {
        Zone zone = c.getArgument(this, ZONE);
        Group group = c.getArgument(this, GROUP);
        int pages = (zone.getMembers().getMembers(group).size() / 10) + 1;
        return CommandArgumentResult.from(a, pages);

    }), 1);
    public static final ZoneArgument ZONE = new ZoneArgument("zoneId",
            new ZoneArgument.ZoneArgumentPropertiesBuilder().setBypassSuggestionPermission(
                    ZonePermissions.OVERRIDE_FLAG_MEMBERS_VIEW));
    public static final ZoneGroupArgument GROUP = new ZoneGroupArgument("groupId", ZONE);

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Arrays.asList(new ExactArgument("region"),
                new ExactArgument("members"),
                ZONE,
                new ExactArgument("view"),
                GROUP,
                this.PAGE);
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.text("View the members in a zone by group");
    }

    @Override
    public @NotNull Optional<ZonePermission> getPermissionNode() {
        return Optional.of(ZonePermissions.FLAG_MEMBERS_VIEW);
    }

    @Override
    public @NotNull CommandResult run(CommandContext commandContext, String... args) {
        Zone zone = commandContext.getArgument(this, ZONE);
        Group group = commandContext.getArgument(this, GROUP);
        int page = commandContext.getArgument(this, this.PAGE);
        if (page <= 0) {
            return CommandResult.error(Messages.getPageTooLow());
        }

        Collection<UUID> memberIds = zone.getMembers().getMembers(group);
        UserManager userManager = Sponge.server().userManager();
        commandContext.getCause().sendMessage(Identity.nil(), Messages.getGroupStart(zone));
        commandContext.getCause().sendMessage(Identity.nil(), Messages.getGroupInfo(group));
        commandContext.getCause().sendMessage(Identity.nil(), Messages.getTotalInfo(memberIds));
        commandContext.getCause().sendMessage(Identity.nil(), Messages.getPagesInfo(page));
        int count = 0;
        int pageStart = (page - 1) * 10;
        int pageEnd = (page) * 10;
        for (UUID uuid : memberIds) {
            count++;
            if (count < pageStart) {
                continue;
            }
            if (count > pageEnd) {
                break;
            }
            CompletableFuture<Optional<User>> loader = userManager.load(uuid);
            loader.thenAccept(opUser -> opUser.ifPresent(user -> commandContext
                    .getCause()
                    .sendMessage(Identity.nil(), Messages.getEntryName(user))));
        }
        return CommandResult.success();
    }
}
