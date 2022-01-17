package org.zone.region.flag.meta.eco;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.plugin.PluginContainer;
import org.zone.ZonePlugin;
import org.zone.region.Zone;
import org.zone.region.flag.FlagType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Flag used to hold the zones balance
 */
public class EcoFlagType implements FlagType<EcoFlag> {

    public static final String NAME = "Eco";
    public static final String KEY = "eco";

    @Override
    public @NotNull String getName() {
        return NAME;
    }

    @Override
    public @NotNull PluginContainer getPlugin() {
        return ZonePlugin.getZonesPlugin().getPluginContainer();
    }

    @Override
    public @NotNull String getKey() {
        return KEY;
    }

    @Override
    public @NotNull EcoFlag load(@NotNull ConfigurationNode node) throws IOException {
        if (Sponge.serviceProvider().provide(EconomyService.class).isEmpty()) {
            throw new IOException("No Eco service found");
        }

        Map<Currency, Double> map = Sponge
                .server()
                .registry(RegistryTypes.CURRENCY)
                .stream()
                .map(currency -> {
                    double value = node
                            .node("currency")
                            .node(currency.key(RegistryTypes.CURRENCY).asString())
                            .getDouble(-1);
                    return new AbstractMap.SimpleImmutableEntry<>(currency, value);
                })
                .filter(entry -> entry.getValue() < 0)
                .collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey,
                        AbstractMap.SimpleImmutableEntry::getValue));
        return new EcoFlag(map);
    }

    @Override
    public void save(@NotNull ConfigurationNode node, @Nullable EcoFlag save) throws IOException {
        if (save == null) {
            node.set(null);
            return;
        }
        for (Map.Entry<Currency, BigDecimal> entry : save.getMoney().entrySet()) {
            node
                    .node("currency")
                    .node(entry.getKey().key(RegistryTypes.CURRENCY).asString())
                    .set(entry.getValue().doubleValue());
        }
    }

    @Override
    public boolean canApply(Zone zone) {
        return zone.getParentId().isEmpty();
    }

    @Override
    public @NotNull Optional<EcoFlag> createCopyOfDefaultFlag() {
        return Optional.of(new EcoFlag());
    }
}
