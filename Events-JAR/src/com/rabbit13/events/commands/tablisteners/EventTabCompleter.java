package com.rabbit13.events.commands.tablisteners;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.BackupItemsManager;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.objects.event.Event;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EventTabCompleter implements TabCompleter {
    private static final String[] subCommands = new String[]{
            "info", "help", "list", "reload", "debug",
            "backup", "tp", "modify", "broadcast", "win",
            "start", "quit", "end", "invclear",
            "create", "remove", "lock", "unlock",
            "kick", "ban", "unban", "checkpoint",
    };
    private static final String[] winCommands = new String[]{
            "list", "add"
    };
    private static final String[] checkpointCommands = new String[]{
            "set", "remove", "removeall", "view", "list"
    };

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            Collections.addAll(results, subCommands);
        }
        else {
            if (args[0].equalsIgnoreCase("backup")) {
                if (args.length == 2) {
                    String[] files = BackupItemsManager.getPath().list();
                    if (files != null) {
                        for (String fileName : files) {
                            results.add(fileName.replace(".yml", ""));
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("win")) {
                if (args.length == 2) {
                    results.addAll(Arrays.asList(winCommands));
                }
                else if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("add")) {
                        for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
                            results.add(p.getName());
                        }
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("checkpoint") || args[0].equalsIgnoreCase("ch")) {
                if (args.length == 2) {
                    results.addAll(Arrays.asList(checkpointCommands));
                }
                else if (args.length == 3) {
                    for (Map.Entry<String, Event> entries : EventManager.getEvents().entrySet()) {
                        results.add(entries.getKey());
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("kick")) {
                if (args.length == 2) {
                    for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
                        results.add(p.getName());
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("ban")) {
                if (args.length == 2) {
                    for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
                        results.add(p.getName());
                    }
                }
            }
            else {
                if (args.length == 2) {
                    for (Map.Entry<String, Event> entries : EventManager.getEvents().entrySet()) {
                        results.add(entries.getKey());
                    }
                }
            }
        }
        return searchAlgoritm(args, results);
    }


    //<editor-fold desc="Other Methods">
    @NotNull
    private <Col extends Collection<String>> List<String> searchAlgoritm(@NotNull String[] args, Col keys) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            if (args[0].length() > 0) {
                for (String subCommand : keys) {
                    if (subCommand.substring(0, Math.min((args[0].length()), subCommand.length())).equalsIgnoreCase(args[0])) {
                        results.add(subCommand);
                    }
                }
            }
            else {
                Collections.addAll(results, keys.toArray(new String[0]));
            }
        }
        else if (args.length > 1) {
            int i = args.length - 1;
            if (args[i].length() > 0) {
                for (String key : keys) {
                    if (key.substring(0, Math.min((args[i].length()), key.length())).equalsIgnoreCase(args[i])) {
                        results.add(key);
                    }
                }
            }
            else {
                Collections.addAll(results, keys.toArray(new String[0]));
            }
        }
        return results;
    }
    //</editor-fold>
}
