package com.rabbit13.events.commands.tablisteners;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.objects.eEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.rabbit13.events.main.Misc.debugMessage;

public class EventTabCompleter implements TabCompleter {
    private int argsLength = 0;
    private static String[] subCommands = new String[]{
            "info", "help", "list",
            "reload", "quit", "lock",
            "unlock", "tp", "modify",
            "end", "invclear", "debug",
            "create", "remove", "start",
            "kick", "ban", "unban",
            "broadcast", "win", "backup"
    };
    private static String[] winCommands = new String[]{
            "list", "add"
    };

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            Collections.addAll(results, subCommands);
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("backup")) {
                List<String> names = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    names.add(player.getName());
                }
                results.addAll(names);
            }
            else if (args[0].equalsIgnoreCase("win")) {
                results.addAll(Arrays.asList(winCommands));
            }
            else {
                for (Map.Entry<String, eEvent> entries : EventManager.getEvents().entrySet()) {
                    results.add(entries.getKey());
                }
            }
        }
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("win")) {
                if (args[1].equalsIgnoreCase("add")) {
                    for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
                        debugMessage("Player:" + p.toString());
                        results.add(p.getName());
                    }
                }
            }
        }
        if(argsLength != args.length) {
            argsLength = args.length;
            debugMessage("args: " + Arrays.toString(args));
        }
        return searchAlgoritm(args, results);
    }

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

    public static String[] getSubCommands() {
        return subCommands;
    }
}
