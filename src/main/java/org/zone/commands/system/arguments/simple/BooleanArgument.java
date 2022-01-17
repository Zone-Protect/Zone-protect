package org.zone.commands.system.arguments.simple;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.zone.commands.system.CommandArgumentResult;
import org.zone.commands.system.GUICommandArgument;
import org.zone.commands.system.context.CommandArgumentContext;
import org.zone.commands.system.context.CommandContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BooleanArgument implements GUICommandArgument<Boolean> {

    private final String id;
    private final String asTrue;
    private final String asFalse;

    public BooleanArgument(String id) {
        this(id, "true", "false");
    }

    public BooleanArgument(String id, String trueString, String falseString) {
        this.id = id;
        this.asFalse = falseString;
        this.asTrue = trueString;
    }

    public ItemStack getFalseItem() {
        return ItemStack
                .builder()
                .itemType(ItemTypes.BARRIER)
                .quantity(1)
                .add(Keys.DISPLAY_NAME, Component.text(this.asFalse))
                .build();

    }

    public ItemStack getTrueItem() {
        return ItemStack
                .builder()
                .itemType(ItemTypes.STICK)
                .quantity(1)
                .add(Keys.DISPLAY_NAME, Component.text(this.asTrue))
                .build();

    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public CommandArgumentResult<Boolean> parse(
            CommandContext context, CommandArgumentContext<Boolean> argument) throws IOException {
        String arg = context.getCommand()[argument.getFirstArgument()];
        if (arg.equals(this.asTrue)) {
            return CommandArgumentResult.from(argument, true);
        }
        if (arg.equals(this.asFalse)) {
            return CommandArgumentResult.from(argument, false);
        }
        throw new IOException("'" +
                arg +
                "' is not either '" +
                this.asTrue +
                "' or '" +
                this.asFalse +
                "'");
    }

    @Override
    public @NotNull Set<CommandCompletion> suggest(
            CommandContext commandContext, CommandArgumentContext<Boolean> argument) {
        String peek = commandContext.getCommand()[argument.getFirstArgument()];
        Set<CommandCompletion> list = new HashSet<>();
        if (this.asTrue.startsWith(peek.toLowerCase())) {
            list.add(CommandCompletion.of(this.asTrue));
        }
        if (this.asFalse.startsWith(peek.toLowerCase())) {
            list.add(CommandCompletion.of(this.asFalse));
        }
        return list;
    }

    @Override
    public Map<ItemStack, String> createMenuOptions(CommandContext context) {
        Map<ItemStack, String> map = new HashMap<>();
        ItemStack falseItem = this.getFalseItem();
        ItemStack trueItem = this.getTrueItem();
        map.put(falseItem, "false");
        map.put(trueItem, "true");
        return map;
    }
}