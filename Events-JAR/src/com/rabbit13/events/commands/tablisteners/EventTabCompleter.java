package com.rabbit13.events.commands.tablisteners;

import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.objects.Event;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.rabbit13.events.main.Misc.debugMessage;

public class EventTabCompleter implements TabCompleter {
    // TODO: 06.12.2019 permission sorted tab-completing
    private static String[] subCommands = new String[]{
            "info", "help", "list",
            "reload", "quit", "lock",
            "unlock", "tp", "modify",
            "end", "invclear", "debug",
            "create", "remove", "start",
            "kick", "ban", "unban", "broadcast"
    };

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> results = new ArrayList<>();

        if (args.length == 1) {
            if (args[0].length() > 0) {
                for (String subCommand : subCommands) {
                    debugMessage("Substring: " + subCommand.substring(0, Math.min((args[0].length()), subCommand.length())));
                    if (subCommand.substring(0, Math.min((args[0].length()), subCommand.length())).equalsIgnoreCase(args[0])) {
                        results.add(subCommand);
                    }
                }
            }
            else {
                Collections.addAll(results, subCommands);
            }
        }
        else if (args.length == 2) {
            if (args[1].length() > 0) {
                for (Map.Entry<String, Event> entry : EventManager.getEvents().entrySet()) {
                    String name = entry.getKey();
                    debugMessage("Substring: " + name.substring(0, Math.min((args[1].length()), name.length())));
                    if (name.substring(0, Math.min((args[1].length()), name.length())).equalsIgnoreCase(args[1])) {
                        results.add(name);
                    }
                }
            }
            else {
                for (Map.Entry<String, Event> entry : EventManager.getEvents().entrySet()) {
                    results.add(entry.getKey());
                }
            }
        }
        return results;
    }

    public static String[] getSubCommands() {
        return subCommands;
    }
}
