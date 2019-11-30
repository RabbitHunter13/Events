package com.rabbit13.events.managers;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.main.Misc;
import com.rabbit13.events.objects.Event;
import com.rabbit13.events.objects.EventLocation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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

    private final ConfigurationSection words;

    public FileManager() {
        //save files to dataFolder (first time)
        final File readme = new File(path, "readme.txt");
        final File langFile = new File(path, "lang.yml");

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
            try {
                Misc.copy(Main.getInstance().getResource(f.getName()), f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadEventsFromYml(YamlConfiguration.loadConfiguration(eventsFile));
        }
    }

    public void saveEvents() {
        YamlConfiguration yml = new YamlConfiguration();
        for (Map.Entry<String, Event> entry : EventManager.getEvents().entrySet()) {
            Event event = entry.getValue();
            ConfigurationSection eventSection = yml.createSection(event.getName());

            eventSection.set("owner", event.getOwner());
            eventSection.set("teleport", event.getTeleport().toString());
            eventSection.set("checkpoints", event.getCheckpoints());
            eventSection.set("banned", event.getBanned());
            eventSection.set("fall-damage", event.getFallDamage());
            eventSection.set("lava-equals-fail", event.getLavaEqualsFail());
        }
        try {
            yml.save(eventsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadEventsFromYml(YamlConfiguration yml) {
        if (EventManager.getEvents().size() > 0) {
            EventManager.getEvents().clear();
        }
        //keys = events
        for (String key : yml.getKeys(false)) {
            ConfigurationSection keyconf = yml.getConfigurationSection(key);
            if(keyconf != null) {
                String[] coords = new String[0];
                String teleport = keyconf.getString("teleport");
                if (teleport != null) {
                    coords = teleport.split(",");
                }
                else {
                    Bukkit.getLogger().log(Level.SEVERE, "The information about \"teleport\" in " + keyconf.getName() + "is missing!");
                }
                String[] checkpoints = keyconf.getStringList("checkpoints").toArray(new String[0]);
                String[] banned = keyconf.getStringList("banned").toArray(new String[0]);
                //loading into objects (Map)
                if (coords.length == 3) {
                    try {
                        EventManager.getEvents().put(key, new Event(key
                                , keyconf.getString("owner")
                                , new EventLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]))
                                , checkpoints
                                , banned
                                , keyconf.getBoolean("fall-damage")
                                , keyconf.getBoolean("lava-equals-fail")
                        ));
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().log(Level.SEVERE, " Error Loading location in" + keyconf.getName() + " Location have to be \"int,int,int\".");
                    }
                }
                else {
                    Bukkit.getLogger().log(Level.SEVERE, " Error Loading location in" + keyconf.getName() + " Location must have 3 positions. (int,int,int)");
                }
            } else {
                Bukkit.getLogger().log(Level.WARNING, " The Event" + key + " couldn't be loaded");
            }
        }
    }

    public YamlConfiguration getEvents() {
        return YamlConfiguration.loadConfiguration(eventsFile);
    }

    public ConfigurationSection getWords() {
        return words;
    }
}
