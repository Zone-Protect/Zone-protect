package org.zone.config.node.title;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.zone.ZonePlugin;
import org.zone.commands.system.CommandArgument;
import org.zone.commands.system.arguments.simple.EnumArgument;
import org.zone.commands.system.arguments.simple.number.IntegerArgument;
import org.zone.commands.system.context.CommandContext;
import org.zone.config.command.ConfigCommandNode;
import org.zone.config.node.ZoneNode;
import org.zone.utils.Messages;
import org.zone.utils.time.TimeUnits;
import org.zone.utils.time.duration.TimeDuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class DefaultTitleFadeInNode implements ZoneNode.WithDefault<TimeDuration> {

    private class DefaultTitleFadeInConfigCommandTimeNode implements ConfigCommandNode<Integer> {

        @Override
        public String getDisplayId() {
            return "zone.message.title.fadeIn.length";
        }

        @Override
        public CommandArgument<Integer> getCommandArgument() {
            return new IntegerArgument("length");
        }

        @Override
        public CommandResult onChange(
                CommandContext context, Integer newValue) {
            try {
                if (DefaultTitleFadeInNode.this.get(ZonePlugin.getZonesPlugin().getConfig()).isEmpty()) {
                    DefaultTitleFadeInNode.this.set(ZonePlugin.getZonesPlugin().getConfig(),
                            new TimeDuration(TimeUnits.SECONDS, newValue));
                } else {
                    DefaultTitleFadeInNode.this.set(ZonePlugin.getZonesPlugin().getConfig(),
                            new TimeDuration(DefaultTitleFadeInNode.this
                                    .get(ZonePlugin.getZonesPlugin().getConfig())
                                    .get()
                                    .getTimeUnit(), newValue));
                }
                return CommandResult.success();
            } catch (SerializationException se) {
                se.printStackTrace();
                return CommandResult.error(Messages.getZoneSavingError(se));
            }
        }
    }

    private class DefaultTitleFadeInConfigCommandTimeUnitsNode implements ConfigCommandNode<TimeUnits> {

        @Override
        public String getDisplayId() {
            return "zone.message.title.fadeIn.timeUnit";
        }

        @Override
        public CommandArgument<TimeUnits> getCommandArgument() {
            return new EnumArgument<>("timeUnit", TimeUnits.class);
        }

        @Override
        public CommandResult onChange(CommandContext context, TimeUnits newValue) {
            try {
                if (DefaultTitleFadeInNode.this.get(ZonePlugin.getZonesPlugin().getConfig()).isEmpty()) {
                    DefaultTitleFadeInNode.this.set(ZonePlugin.getZonesPlugin().getConfig(),
                            new TimeDuration(newValue, 5));
                } else {
                    DefaultTitleFadeInNode.this.set(ZonePlugin.getZonesPlugin().getConfig(),
                            new TimeDuration(newValue,
                                    DefaultTitleFadeInNode.this.get(ZonePlugin.getZonesPlugin()
                                            .getConfig()).get().getLength()));
                }
                return CommandResult.success();
            } catch (SerializationException se) {
                se.printStackTrace();
                return CommandResult.error(Messages.getZoneSavingError(se));
            }
        }
    }

    @Override
    public @NotNull TimeDuration getDefault() {
        return this.getInitialValue();
    }

    @Override
    public Object[] getNode() {
        return new Object[] {"zone", "message", "title", "fadeIn"};
    }

    @Override
    public @NotNull TimeDuration getInitialValue() {
        return new TimeDuration(TimeUnits.SECONDS, 5);
    }

    @Override
    public @NotNull Collection<ConfigCommandNode<?>> getNodes() {
        return Arrays.asList(new DefaultTitleFadeInConfigCommandTimeNode(), new DefaultTitleFadeInConfigCommandTimeUnitsNode());
    }

    @Override
    public void set(@NotNull CommentedConfigurationNode node, @NotNull TimeDuration timeDuration)
            throws SerializationException {
        node.node("Length").set(timeDuration.getLength());
        node.node("Unit").set(timeDuration.getTimeUnit().name());
    }

    @Override
    public @NotNull Optional<TimeDuration> get(@NotNull CommentedConfigurationNode node) {
        int length = node.node("Length").getInt();
        String unitAsString = node.node("Unit").getString();
        TimeUnits unit = TimeUnits.valueOf(unitAsString);
        if (length == 0 || unitAsString == null) {
            return Optional.empty();
        }
        return Optional.of(new TimeDuration(unit, length));
    }

}
