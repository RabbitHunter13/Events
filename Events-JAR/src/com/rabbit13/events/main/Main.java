package com.rabbit13.events.main;

import com.rabbit13.events.commands.EventExecutor;
import com.rabbit13.events.commands.tabcompleters.EventTabCompleter;
import com.rabbit13.events.listeners.BackupListener;
import com.rabbit13.events.listeners.EventListener;
import com.rabbit13.events.listeners.ModListener;
import com.rabbit13.events.managers.FileManager;
import com.rabbit13.events.managers.PlayerManager;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static com.rabbit13.events.main.Misc.sendLM;

public final class Main extends JavaPlugin {
    private static boolean debugMode;
    private static Main instance;
    //files

    private static FileManager filMan;
    private static PluginDescriptionFile pdf;
    //values
    private static CommandSender sender;
    private static String prefix;
    private static String pluginPrefix;

    @Override public void onLoad() {
        instance = this;
        saveDefaultConfig();
        filMan = new FileManager();
        pdf = getDescription();
        sender = getServer().getConsoleSender();
        prefix = this.getConfig().getString("prefix");
        pluginPrefix = this.getConfig().getString("plugin-prefix");
        debugMode = getConfig().getBoolean("debug");
    }

    @Override
    public void onEnable() {
        filMan.checkWords();
        filMan.loadEvents();
        filMan.loadCounter();
        sendLM(pluginPrefix + " Setting up Listeners", false, sender); //Events
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new ModListener(), this);
        getServer().getPluginManager().registerEvents(new BackupListener(), this);
        sendLM(pluginPrefix + " Setting up Executors", false, sender); //Executors
        val eCommand = Objects.requireNonNull(this.getCommand("event"));
        eCommand.setExecutor(new EventExecutor());
        eCommand.setTabCompleter(new EventTabCompleter());
        super.onEnable();
    }

    @Override
    public void onDisable() {
        PlayerManager.getJoinedEvent().forEach((p, d) -> PlayerManager.playerLeavingEvent(p, null, true));
        filMan.saveEvents();
        filMan.saveCounter();
        super.onDisable();
    }

    //<editor-fold desc="Getters">
    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Main.debugMode = debugMode;
    }

    public static Main getInstance() {
        return instance;
    }

    public static FileManager getFilMan() {
        return filMan;
    }

    public static PluginDescriptionFile getPdf() {
        return pdf;
    }

    public static CommandSender getSender() {
        return sender;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static void setPrefix(String prefix) {
        Main.prefix = prefix;
    }

    public static String getPluginPrefix() {
        return pluginPrefix;
    }

    public static void setPluginPrefix(String pluginPrefix) {
        Main.pluginPrefix = pluginPrefix;
    }

    //</editor-fold>
}
