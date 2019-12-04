package com.rabbit13.events.managers;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.main.Misc;
import com.rabbit13.events.objects.Event;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import static com.rabbit13.events.main.Main.getSender;
import static com.rabbit13.events.main.Misc.sendLM;

public final class FileManager {
    private final File path = new File(Main.getInstance().getDataFolder().getPath());
    private final File eventsFile = new File(Main.getInstance().getDataFolder().getPath() + File.separatorChar + "Data", "events.yml");
    private final File langFile = new File(path, "lang.yml");
    private ConfigurationSection words;

    public FileManager() {
        //save files to dataFolder (first time)
        final File readme = new File(path, "readme.txt");
        final File[] files = new File[]{langFile, readme};
        for (File f : files) {
            Misc.debugMessage("Proceeding file: " + f.getName());
            if (!f.exists()) {
                try {
                    Main.getInstance().saveResource(f.getName(), false);
                } catch (IllegalArgumentException e) {
                    try {
                        sendLM(Main.getPluginPrefix() + f.getName() + " couldn't be copied (" + e.getMessage() + ")", false, getSender());
                        f.createNewFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        final String languagePreset = Main.getInstance().getConfig().getString("lang");
        assert languagePreset != null;
        words = YamlConfiguration.loadConfiguration(langFile).getConfigurationSection(languagePreset);

        //different dir
        final File[] files2 = new File[]{eventsFile};
        for (File f : files2) {
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdir();
            }
            if (!f.exists()) {
                try {
                    Misc.copy(Main.getInstance().getResource(f.getName()), f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveEvents() {
        YamlConfiguration yml = new YamlConfiguration();
        for (Map.Entry<String, Event> entry : ContestManager.getEvents().entrySet()) {
            Event event = entry.getValue();
            ConfigurationSection eventSection = yml.createSection(event.getName());

            eventSection.set("owner", event.getOwner());
            eventSection.set("teleport", event.getTeleport().toString());
            eventSection.set("checkpoints", event.getCheckpoints());
            eventSection.set("banned", event.getBanned());
            ConfigurationSection mods = eventSection.createSection("mods");

            mods.set("fall-damage", event.getMods().getFallDamage());
            mods.set("lava-equals-fail", event.getMods().getLavaEqualsFail());
            mods.set("more-hp",event.getMods().getMoreHP());
            mods.set("rapid-damage", event.getMods().getRapidDamage());
        }
        try {
            yml.save(eventsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadEventsFromYml(YamlConfiguration yml) {
        if (ContestManager.getEvents().size() > 0) {
            ContestManager.getEvents().clear();
        }
        //keys = events
        for (String key : yml.getKeys(false)) {
            ConfigurationSection keyconf = yml.getConfigurationSection(key);
            if (keyconf != null) {
                String[] coords = new String[0];
                String teleport = keyconf.getString("teleport");
                if (teleport != null) {
                    coords = teleport.split(",");
                }
                else {
                    Main.getInstance().getLogger().log(Level.SEVERE, "The information about \"teleport\" in " + keyconf.getName() + "is missing!");
                }
                String[] checkpoints = keyconf.getStringList("checkpoints").toArray(new String[0]);
                String[] banned = keyconf.getStringList("banned").toArray(new String[0]);
                //loading into objects (Map)
                if (coords.length == 6) {
                    try {
                        World world = Bukkit.getWorld(coords[0]);
                        if (world == null) {
                            throw new InvalidConfigurationException();
                        } // TODO: 04.12.2019 end of school class here
                        ContestManager.getEvents().put(key, new Event(
                                key,
                                keyconf.getString("owner"),
                                new Location(world, Double.parseDouble(coords[1]), Double.parseDouble(coords[2]), Double.parseDouble(coords[3]), Float.parseFloat(coords[4]), Float.parseFloat(coords[5])),
                                checkpoints,
                                banned,
                                keyconf.getConfigurationSection("mods")
                        ));
                    } catch (NumberFormatException | InvalidConfigurationException e) {
                        Main.getInstance().getLogger().log(Level.SEVERE, " Error Loading location in" + keyconf.getName() + " Location have to be \"world_name, x , y, z, yaw, pitch\".");
                    }
                }
                else {
                    Main.getInstance().getLogger().log(Level.SEVERE, " Error Loading location in" + keyconf.getName() + " Location must have 6 positions. (world_name, x, y, z, yaw, pitch)");
                }
            }
            else {
                Main.getInstance().getLogger().log(Level.WARNING, " The Event" + key + " couldn't be loaded");
            }
        }
    }

    public YamlConfiguration getEventsYaml() {
        return YamlConfiguration.loadConfiguration(eventsFile);
    }

    public File getLangFile() {
        return langFile;
    }

    public ConfigurationSection getWords() {
        return words;
    }

    public void setWords(ConfigurationSection words) {
        this.words = words;
    }
}
