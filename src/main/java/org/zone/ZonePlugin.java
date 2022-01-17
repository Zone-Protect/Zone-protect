package org.zone;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;
import org.zone.commands.structure.ZoneCommands;
import org.zone.event.listener.PlayerListener;
import org.zone.memory.MemoryHolder;
import org.zone.region.Zone;
import org.zone.region.ZoneManager;
import org.zone.region.flag.Flag;
import org.zone.region.flag.FlagManager;
import org.zone.region.flag.FlagType;
import org.zone.region.flag.entity.monster.move.MonsterPreventionListener;
import org.zone.region.flag.entity.player.damage.attack.EntityDamagePlayerListener;
import org.zone.region.flag.entity.player.damage.fall.PlayerFallDamageListener;
import org.zone.region.flag.entity.player.interact.block.destroy.BlockBreakListener;
import org.zone.region.flag.entity.player.interact.block.place.BlockPlaceListener;
import org.zone.region.flag.entity.player.interact.door.DoorInteractListener;
import org.zone.region.flag.entity.player.interact.itemframe.ItemFrameInteractionListener;
import org.zone.region.flag.entity.player.move.greetings.GreetingsFlagListener;
import org.zone.region.flag.entity.player.move.leaving.LeavingFlagListener;
import org.zone.region.flag.entity.player.move.preventing.PreventPlayersListener;
import org.zone.region.group.key.GroupKeyManager;
import org.zone.utils.Messages;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * The zone plugin's boot and main class, use {@link ZonePlugin#getZonesPlugin()} to gain an
 * instance of this class
 */
@Plugin("zones")
public class ZonePlugin {

    private final PluginContainer plugin;
    private final Logger logger;
    private FlagManager flagManager;
    private ZoneManager zoneManager;
    private GroupKeyManager groupKeyManager;
    private MemoryHolder memoryHolder;
    private static ZonePlugin zonePlugin;

    @SuppressWarnings("SpongeInjection")
    @Inject
    public ZonePlugin(final PluginContainer plugin, final Logger logger) {
        zonePlugin = this;
        this.plugin = plugin;
        this.logger = logger;
    }

    /**
     * Gets the flag manager
     *
     * @return The instance of the flag manager
     */
    public @NotNull FlagManager getFlagManager() {
        return this.flagManager;
    }

    /**
     * Gets the zone manager
     *
     * @return The instance of the zone manager
     */
    public @NotNull ZoneManager getZoneManager() {
        return this.zoneManager;
    }

    /**
     * Gets the Memory holder
     *
     * @return the instance of the memory holder
     */
    public @NotNull MemoryHolder getMemoryHolder() {
        return this.memoryHolder;
    }

    /**
     * Gets the Group key manager
     *
     * @return The instance of the group key manager
     */
    public @NotNull GroupKeyManager getGroupKeyManager() {
        return this.groupKeyManager;
    }

    /**
     * Gets the logger for this plugin (Oh no! log4j!)
     *
     * @return The logger for this plugin
     */
    public @NotNull Logger getLogger() {
        return this.logger;
    }

    @Listener
    public void onConstructor(ConstructPluginEvent event) {
        this.flagManager = new FlagManager();
        this.zoneManager = new ZoneManager();
        this.groupKeyManager = new GroupKeyManager();
        this.memoryHolder = new MemoryHolder();
    }

    private void registerListeners() {
        EventManager eventManager = Sponge.eventManager();
        eventManager.registerListeners(this.plugin, new PlayerListener());
        eventManager.registerListeners(this.plugin, new MonsterPreventionListener());
        eventManager.registerListeners(this.plugin, new DoorInteractListener());
        eventManager.registerListeners(this.plugin, new BlockBreakListener());
        eventManager.registerListeners(this.plugin, new BlockPlaceListener());
        eventManager.registerListeners(this.plugin, new GreetingsFlagListener());
        eventManager.registerListeners(this.plugin, new PreventPlayersListener());
        eventManager.registerListeners(this.plugin, new LeavingFlagListener());
        eventManager.registerListeners(this.plugin, new ItemFrameInteractionListener());
        eventManager.registerListeners(this.plugin, new EntityDamagePlayerListener());
        eventManager.registerListeners(this.plugin, new PlayerFallDamageListener());
    }

    @Listener
    public void onServerStarting(final StartingEngineEvent<Server> event) {
        this.registerListeners();
    }

    @Listener
    public void onServerStarted(final StartedEngineEvent<Server> event) {
        FlagManager manager = this.getFlagManager();
        for (FlagType<?> type : this.getFlagManager().getRegistered()) {
            if (type instanceof FlagType.TaggedFlagType) {
                continue;
            }
            Optional<?> opDefault = manager.getDefaultFlags().loadDefault(type);
            if (opDefault.isPresent()) {
                continue;
            }
            Optional<?> opFlag = type.createCopyOfDefaultFlag();
            if (opFlag.isEmpty()) {
                continue;
            }
            try {
                manager.getDefaultFlags().setDefault((Flag) opFlag.get());
            } catch (IOException e) {
                this
                        .getLogger()
                        .error("Could not set the defaults for '" +
                                type.getId() +
                                "' " +
                                "despite a copy of default found. Is saving done " +
                                "correctly?");
                e.printStackTrace();
            }
        }
        try {
            manager.getDefaultFlags().save();
        } catch (ConfigurateException e) {
            this.getLogger().error("Could not save defaults file");
            e.printStackTrace();
        }

        Sponge.systemSubject().sendMessage(Messages.getLoadingZonesStart());
        File zonesFolder = new File("config/zone/zones/");
        Sponge.systemSubject().sendMessage(Messages.getZonesLoadingFrom(zonesFolder.getPath()));

        for (PluginContainer container : Sponge.pluginManager().plugins()) {
            File keyFolder = new File(zonesFolder, container.metadata().id());
            File[] keyFiles = keyFolder.listFiles();
            if (keyFiles == null) {
                continue;
            }
            for (File file : keyFiles) {
                try {
                    Zone zone = this.zoneManager.load(file);
                    this.zoneManager.register(zone);
                } catch (ConfigurateException e) {
                    Sponge
                            .systemSubject()
                            .sendMessage(Messages.getZonesLoadingFail(file.getPath()));
                    e.printStackTrace();
                }
            }
        }
        Sponge
                .systemSubject()
                .sendMessage(Messages.getZonesLoaded(this.getZoneManager().getZones()));
    }

    @Listener
    public void onRegisterCommands(@SuppressWarnings("BoundedWildcard") final RegisterCommandEvent<Command.Raw> event) {
        event.register(this.plugin,
                ZoneCommands.createCommand(),
                "zone",
                "region",
                "claim",
                "protect");
    }

    /**
     * Gets the PluginContainer for this plugin
     *
     * @return The plugin container for this plugin
     */
    public @NotNull PluginContainer getPluginContainer() {
        return this.plugin;
    }

    /**
     * Gets the instance of this class
     *
     * @return The instance of this class
     */
    public static @NotNull ZonePlugin getZonesPlugin() {
        return zonePlugin;
    }
}
