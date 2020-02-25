package com.rabbit13.events.managers;

import com.rabbit13.events.objects.event.Event;
import com.rabbit13.events.objects.event.RabEvent;
import com.rabbit13.events.objects.event.mods.Effects;
import com.rabbit13.events.objects.event.mods.MoreHP;
import com.rabbit13.events.objects.event.mods.RewardItems;
import com.rabbit13.events.objects.event.mods.StartingItems;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @SuppressWarnings("SuspiciousToArrayCall")
    public void loadEvents() {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(eventsFile);
        if (!EventManager.getEvents().isEmpty()) {
            EventManager.getEvents().clear();
        }
        //keys = events
        for (String key : yml.getKeys(false)) {
            ConfigurationSection keyconf = yml.getConfigurationSection(key);
            if (keyconf != null) {
                //<editor-fold desc="Checkpoints">
                Location[] checkpoints = null;
                List<?> checklist = keyconf.getList("checkpoints");
                if (checklist != null) {
                    checkpoints = checklist.toArray(new Location[0]);
                }
                //</editor-fold>
                //<editor-fold desc="Banned">
                String[] banned = keyconf.getStringList("banned").toArray(new String[0]);
                //</editor-fold>

                //<editor-fold desc="Loading Event into Map">
                try {
                    //noinspection ConstantConditions
                    EventManager.getEvents().put(key, new RabEvent(
                            key,
                            keyconf.getString("owner"),
                            keyconf.getLocation("teleport"),
                            keyconf.getLocation("finish"),
                            checkpoints,
                            banned,
                            keyconf.getConfigurationSection("mods")
                    ));
                } catch (Exception e) {
                    error(" The Event " + key + " couldn't be loaded");
                    error("error had this stacktrace: ");
                    e.printStackTrace();
                }
            }
            //</editor-fold>
        }
    }


    public void saveEvents() {
        YamlConfiguration yml = new YamlConfiguration();
        for (Map.Entry<String, Event> entry : EventManager.getEvents().entrySet()) {
            Event event = entry.getValue();
            ConfigurationSection eventSection = yml.createSection(event.getName());
            //<editor-fold desc="Save Event">
            eventSection.set("owner", event.getOwner());
            eventSection.set("teleport", event.getTeleport());
            eventSection.set("finish", event.getFinish());
            eventSection.set("checkpoints", event.getCheckpoints());
            eventSection.set("banned", event.getBanned());
            //</editor-fold>
            //<editor-fold desc="Save Mods">
            ConfigurationSection mods = eventSection.createSection("mods");
            //<editor-fold desc="Fall Damage">
            ConfigurationSection mod = mods.createSection("fall-damage");
            mod.set("enabled", event.getMods().getFallDamage().isEnabled());
            //</editor-fold>
            //<editor-fold desc="Lava equals fail">
            mod = mods.createSection("lava-equals-fail");
            mod.set("enabled", event.getMods().getLavaEqualFail().isEnabled());
            //</editor-fold>
            //<editor-fold desc="Checkpoints">
            mod = mods.createSection("checkpoints");
            mod.set("enabled", event.getMods().getCheckpoints().isEnabled());
            //</editor-fold>
            //<editor-fold desc="MoreHP">
            mod = mods.createSection("more-hp");
            MoreHP moreHP = event.getMods().getMoreHP();
            mod.set("enabled", moreHP.isEnabled());
            mod.set("value", moreHP.getHealth());
            //</editor-fold>
            //<editor-fold desc="Starting Items">
            mod = mods.createSection("starting-items");
            StartingItems startingItems = event.getMods().getStartingItems();
            mod.set("enabled", startingItems.isEnabled());
            mod.set("items", removeNullValues(startingItems.getStartingItems().getContents()));
            //</editor-fold>
            //<editor-fold desc="Effects">
            mod = mods.createSection("effects");
            Effects effects = event.getMods().getEffects();
            mod.set("enabled", effects.isEnabled());
            mod.set("items", removeNullValues(effects.getEffectsInv().getContents()));
            //</editor-fold>
            //<editor-fold desc="Rewards">
            mod = mods.createSection("rewards");
            RewardItems rewardItems = event.getMods().getRewards();
            mod.set("enabled", event.getMods().getRewards().isEnabled());
            mod.set("items", rewardItems.getRewards().getContents());
            //</editor-fold>
            //</editor-fold>
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
        boolean changed = false;
        if (oldWords != null) {
            for (String key : newWords.getKeys(false)) {
                if (!oldWords.contains(key)) {
                    oldWords.set(key, newWords.getString(key));
                    changed = true;
                }
            }
        }
        else {
            oldWords = yml.createSection(languagePreset);
            for (String key : newWords.getKeys(false)) {
                if (!oldWords.contains(key)) {
                    oldWords.set(key, newWords.getString(key));
                    changed = true;
                }
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
