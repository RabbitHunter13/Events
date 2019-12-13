package com.rabbit13.events.managers;

import com.rabbit13.events.objects.eEvent;
import com.rabbit13.events.objects.eEventLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import static com.rabbit13.events.main.Main.*;
import static com.rabbit13.events.main.Misc.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class FileManager {
    private final File langFile;

    private final File eventsFile;
    private final File counterFile;

    private ConfigurationSection words;
    private final String languagePreset;

    public FileManager() {
        //main path
        File path = new File(getInstance().getDataFolder().getPath());
        langFile = new File(path, "lang.yml");
        //data path
        File data = new File(path, "data");
        eventsFile = new File(data, "events.yml");
        counterFile = new File(data, "win_counter.yml");
        //other
        languagePreset = getInstance().getConfig().getString("lang");
        assert languagePreset != null;
        words = YamlConfiguration.loadConfiguration(langFile).getConfigurationSection(languagePreset);
        //save files to dataFolder (first time)
        final File readme = new File(path, "readme.txt");
        final File[] files = new File[]{langFile, readme};
        for (File f : files) {
            debugMessage("Proceeding file: " + f.getName());
            if (!f.exists()) {
                try {
                    getInstance().saveResource(f.getName(), false);
                } catch (IllegalArgumentException e) {
                    try {
                        sendLM(getPluginPrefix() + f.getName() + " couldn't be copied (" + e.getMessage() + ")", false, getSender());
                        f.createNewFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        //different dir
        final File[] files2 = new File[]{eventsFile, counterFile};
        for (File f : files2) {
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdir();
            }
            if (!f.exists()) {
                try {
                    copy(getInstance().getResource(f.getName()), f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadEvents() {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(eventsFile);
        if (!EventManager.getEvents().isEmpty()) {
            EventManager.getEvents().clear();
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
                    error("The information about \"teleport\" in " + keyconf.getName() + "is missing!");
                }
                String[] checkpoints = keyconf.getStringList("checkpoints").toArray(new String[0]);
                String[] banned = keyconf.getStringList("banned").toArray(new String[0]);
                //loading into objects (Map)
                if (coords.length == 6) {
                    try {
                        World world = Bukkit.getWorld(coords[0]);
                        if (world == null) {
                            throw new InvalidConfigurationException();
                        }
                        EventManager.getEvents().put(key, new eEvent(
                                key,
                                keyconf.getString("owner"),
                                new eEventLocation(world, Double.parseDouble(coords[1]), Double.parseDouble(coords[2]), Double.parseDouble(coords[3]), Float.parseFloat(coords[4]), Float.parseFloat(coords[5])),
                                checkpoints,
                                banned,
                                keyconf.getConfigurationSection("mods")
                        ));
                    } catch (NumberFormatException | InvalidConfigurationException e) {
                        error(" Error Loading location in" + keyconf.getName() + " Location have to be \"world_name, x , y, z, yaw, pitch\".");
                    }
                }
                else {
                    error(" Error Loading location in" + keyconf.getName() + " Location must have 6 positions. (world_name, x, y, z, yaw, pitch)");
                }
            }
            else {
                getInstance().getLogger().log(Level.WARNING, " The Event" + key + " couldn't be loaded");
            }
        }
    }

    public void saveEvents() {
        YamlConfiguration yml = new YamlConfiguration();
        for (Map.Entry<String, eEvent> entry : EventManager.getEvents().entrySet()) {
            eEvent event = entry.getValue();
            ConfigurationSection eventSection = yml.createSection(event.getName());

            eventSection.set("owner", event.getOwner());
            eventSection.set("teleport", event.getTeleport().toString());
            eventSection.set("checkpoints", event.getCheckpoints());
            eventSection.set("banned", event.getBanned());
            ConfigurationSection mods = eventSection.createSection("mods");

            mods.set("fall-damage", event.getMods().getFallDamage());
            mods.set("lava-equals-fail", event.getMods().getLavaEqualsFail());
            mods.set("more-hp", event.getMods().getMoreHP());
            mods.set("rapid-damage", event.getMods().getRapidDamage());
        }
        try {
            yml.save(eventsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCounter() {
        YamlConfiguration countYml = YamlConfiguration.loadConfiguration(counterFile);
        if (!PlayerManager.getWinCounter().isEmpty()) {
            PlayerManager.getWinCounter().clear();
        }
        for (String key : countYml.getKeys(false)) {
            PlayerManager.getWinCounter().put(key, countYml.getInt(key));
        }
    }

    public void saveCounter() {
        YamlConfiguration yml = new YamlConfiguration();
        PlayerManager.getWinCounter().forEach(yml::set);
        try {
            yml.save(counterFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**
     * Updates lang.yml file if needed
     */
    public boolean checkWords() {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(langFile);
        ConfigurationSection oldWords = yml.getConfigurationSection(languagePreset);

        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(getInstance().getResource("lang.yml")));
        ConfigurationSection newWords = YamlConfiguration.loadConfiguration(reader).getConfigurationSection(languagePreset);
        assert newWords != null;
        assert oldWords != null;
        boolean changed = false;
        for (String key : newWords.getKeys(false)) {
            if (!oldWords.contains(key)) {
                oldWords.set(key, newWords.getString(key));
                changed = true;
            }
        }
        if (changed) {
            words = oldWords;
        }
        try {
            yml.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return changed;
    }
}
