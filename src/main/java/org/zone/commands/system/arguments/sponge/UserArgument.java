package org.zone.commands.system.arguments.sponge;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.user.UserManager;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserArgument implements CommandArgument<GameProfile> {

    private final @NotNull String id;

    public UserArgument(@NotNull String id) {
        this.id = id;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public CommandArgumentResult<GameProfile> parse(
            @NotNull CommandContext context,
            @NotNull CommandArgumentContext<GameProfile> argument) throws IOException {
        UserManager manager;
        if (Sponge.isServerAvailable()) {
            manager = Sponge.server().userManager();
        } else {
            if (Sponge.client().server().isEmpty()) {
                throw new IOException("Cannot get user, must be in LAN or multiplayer mode");
            }
            manager = Sponge.client().server().get().userManager();
        }

        String arg = argument.getFocusArgument();

        GameProfile ret = manager
                .streamAll()
                .filter(GameProfile::hasName)
                .filter(name -> name.name().orElse("").toLowerCase().equalsIgnoreCase(arg))
                .findAny()
                .orElseThrow(() -> new IOException("Cannot find user"));
        return CommandArgumentResult.from(argument, ret);
    }

    @Override
    public @NotNull Collection<CommandCompletion> suggest(
            @NotNull CommandContext commandContext,
            @NotNull CommandArgumentContext<GameProfile> argument) {
        UserManager manager;
        if (Sponge.isServerAvailable()) {
            manager = Sponge.server().userManager();
        } else {
            if (Sponge.client().server().isEmpty()) {
                return Collections.emptyList();
            }
            manager = Sponge.client().server().get().userManager();
        }

        String peek = argument.getFocusArgument().toLowerCase();

        return manager
                .streamAll()
                .filter(GameProfile::hasName)
                .map(gp -> gp.name().orElse(null))
                .filter(Objects::nonNull)
                .filter(name -> name.toLowerCase().startsWith(peek))
                .map(CommandCompletion::of)
                .collect(Collectors.toSet());
    }
}
