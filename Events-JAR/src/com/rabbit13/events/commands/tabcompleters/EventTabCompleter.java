package com.rabbit13.events.commands.tabcompleters;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.BackupManager;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.objects.event.Event;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EventTabCompleter implements TabCompleter {
    private static final String[] subCommands = new String[]{
            "info", "help", "list", "reload", "debug",
            "backup", "give", "clearinv", "addeff", "cleareff", "tp",
            "modify", "broadcast", "win", "start", "quit", "end",
            "create", "remove", "lock", "unlock", "sethealth",
            "kick", "ban", "unban", "checkpoint",
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
            switch (args[0]) {
                case "backup": {
                    if (args.length == 2) {
                        results.addAll(Arrays.asList("get", "reload"));
                    }
                    else if (args.length == 3) {
                        if (args[1].equals("get")) {
                            results.addAll(BackupManager.getBackups().keySet());
                        }
                    }
                    break;
                }
                case "win": {
                    if (args.length == 2) {
                        results.addAll(Arrays.asList("list", "add"));
                    }
                    else if (args.length == 3) {
                        if (args[1].equals("add")) {
                            for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
                                results.add(p.getName());
                            }
                        }
                    }
                    break;
                }
                case "checkpoint": {
                    if (args.length == 2) {
                        results.addAll(Arrays.asList("set", "remove", "removeall", "view", "list"));
                    }
                    else if (args.length == 3) {
                        for (Map.Entry<String, Event> entries : EventManager.getEvents().entrySet()) {
                            results.add(entries.getKey());
                        }
                    }
                    break;
                }
                case "kick":
                case "ban": {
                    if (args.length == 2) {
                        for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
                            results.add(p.getName());
                        }
                    }
                    break;
                }
                case "list": {
                    if (args.length == 2) {
                        for (Map.Entry<String, Event> entry : EventManager.getEvents().entrySet()) {
                            Event event = entry.getValue();
                            if (!results.contains(event.getOwner())) {
                                results.add(event.getOwner());
                            }
                        }
                    }
                    break;
                }
                case "give": {
                    if (args.length == 2) {
                        for (Material material : Material.values()) {
                            results.add(material.toString().toLowerCase());
                        }
                    }
                    else if (args.length == 3) {
                        results.add("64");
                        results.add("1");
                    }
                    break;
                }
                case "addeff": {
                    if (args.length == 2) {
                        for (PotionEffectType effectType : PotionEffectType.values()) {
                            results.add(effectType.getName().toLowerCase());
                        }
                    }
                    else if (args.length == 3) {
                        results.add("1");
                        results.add("64");
                    }
                    break;
                }
                case "sethealth": {
                    if (args.length == 2) {
                        results.add("1");
                        results.add("20");
                    }
                    break;
                }
                default: {
                    if (args.length == 2) {
                        for (Map.Entry<String, Event> entries : EventManager.getEvents().entrySet()) {
                            results.add(entries.getKey());
                        }
                    }
                    break;
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
                    if (subCommand.substring(0, Math.min((args[0].length()), subCommand.length())).equals(args[0])) {
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
                    if (key.substring(0, Math.min((args[i].length()), key.length())).equals(args[i])) {
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
