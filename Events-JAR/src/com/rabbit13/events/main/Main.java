package com.rabbit13.events.main;

import com.rabbit13.events.commands.MainExecutor;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.FileManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

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

    @Override
    public void onEnable() {
        instance = this;
        filMan = new FileManager();
        pdf = getDescription();
        sender = getServer().getConsoleSender();
        prefix = this.getConfig().getString("prefix");
        pluginPrefix = this.getConfig().getString("plugin-prefix");
        debugMode = getConfig().getBoolean("debug");

        FileManager.loadEventsFromYml(filMan.getEventsYaml());
        saveDefaultConfig();
        Misc.sendLM(pluginPrefix + " Setting up Events", false, sender); //Events
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        Misc.sendLM(pluginPrefix + " Setting up Executors", false, sender); //Executors
        Objects.requireNonNull(this.getCommand("event")).setExecutor(new MainExecutor());
        super.onEnable();
    }

    @Override
    public void onDisable() {
        filMan.saveEvents();
        super.onDisable();
    }

    static boolean isDebugMode() {
        return debugMode;
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

    public static String getPluginPrefix() {
        return pluginPrefix;
    }

}
