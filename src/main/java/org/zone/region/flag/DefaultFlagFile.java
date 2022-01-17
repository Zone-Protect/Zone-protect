package org.zone.region.flag;

import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.zone.ZonePlugin;
import org.zone.utils.Messages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

/**
 * All defaults will be saved and loaded in here.
 */
public class DefaultFlagFile {

    private final HoconConfigurationLoader loader;
    private final ConfigurationNode node;
    public static final File FILE = new File("config/zone/DefaultZone.conf");

    public DefaultFlagFile() {
        this.loader = HoconConfigurationLoader.builder().file(FILE).build();

        ConfigurationNode node1;
        try {
            node1 = this.loader.load();
            try {
                this.createFile();
            } catch (IOException e) {
                ZonePlugin
                        .getZonesPlugin()
                        .getLogger()
                        .error("Could not create default flags " + "file");
                e.printStackTrace();
            }
        } catch (ConfigurateException e) {
            node1 = this.loader.createNode();
            this.updateFile();
        }
        this.node = node1;
    }

    private void createFile() throws IOException {
        if (!FILE.exists()) {
            Files.createDirectories(FILE.getParentFile().toPath());
            Files.createFile(FILE.toPath());
        }
    }

    private void updateFile() {
        try {
            createFile();
            for (FlagType<? extends Flag> type : ZonePlugin
                    .getZonesPlugin()
                    .getFlagManager()
                    .getRegistered()) {
                Optional<? extends Flag> opFlag = this.loadDefault(type);
                if (opFlag.isEmpty()) {
                    this.removeDefault(type);
                    continue;
                }
                this.setDefault(opFlag.get());
            }
            this.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the default flag for the specified type
     *
     * @param type The flag type
     * @param <F>  The flag class
     * @param <T>  The flag type class
     *
     * @return The loaded flag, if it fails to load then the default from the type will be used.
     */
    public <F extends Flag, T extends FlagType<F>> Optional<F> loadDefault(T type) {
        try {
            return Optional.of(type.load(this.node.node("flags",
                    type.getPlugin().metadata().id(),
                    type.getKey())));
        } catch (IOException e) {
            return type.createCopyOfDefaultFlag();
        } catch (Throwable e) {
            Sponge.systemSubject().sendMessage(Messages.getFailedToLoadFlag(type));
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Sets the default values for the flag
     *
     * @param flag The new defaults
     * @param <F>  The flag class
     * @param <T>  The flag type class
     *
     * @throws IOException If fails to save
     */
    public <F extends Flag, T extends FlagType<F>> void setDefault(F flag) throws IOException {
        T type = (T) flag.getType();
        type.save(this.node.node("flags", type.getPlugin().metadata().id(), type.getKey()), flag);
    }

    /**
     * removes the default values for a flag
     *
     * @param type The type to remove
     *
     * @throws IOException if fails to save
     */
    public void removeDefault(FlagType<? extends Flag> type) throws IOException {
        type.save(this.node.node("flags"), null);
    }

    /**
     * Saves the file
     *
     * @throws ConfigurateException If fails to save
     */
    public void save() throws ConfigurateException {
        this.loader.save(this.node);
    }
}
