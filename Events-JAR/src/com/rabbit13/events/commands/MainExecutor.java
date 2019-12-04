package com.rabbit13.events.commands;

import com.rabbit13.events.events.PlayerJoinContestEvent;
import com.rabbit13.events.events.PlayerLeaveContestEvent;
import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.ContestManager;
import com.rabbit13.events.managers.FileManager;
import com.rabbit13.events.managers.PlayerManager;
import com.rabbit13.events.objects.Event;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.rabbit13.events.main.Misc.debugMessage;
import static com.rabbit13.events.main.Misc.sendLM;

public final class MainExecutor implements CommandExecutor {
    private static List<String> usages;
    private static List<String> adminUsages;

    public MainExecutor() {
        usages = Main.getInstance().getConfig().getStringList("usages");
        adminUsages = Main.getInstance().getConfig().getStringList("usages-admin");
    }

    // TODO: 18.11.2019 pvp: TooManyHP, rapidDMG
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            // TODO: 03.12.2019 remove
            // TODO: 03.12.2019 kick/ban/unban
            // TODO: 03.12.2019 list
            // TODO: 03.12.2019 start/end
            // TODO: 03.12.2019 lock/unlock
            // TODO: 03.12.2019 invclear
            // TODO: 03.12.2019 broadcast
            // TODO: 03.12.2019 modify (text)
        }
        else {
            Player plsender = (Player) sender;
            if (args.length == 0) {
                if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.join")) { //done
                    if (ContestManager.getActiveEvent() != null) {
                        if (!ContestManager.getActiveEvent().getBanned().contains(plsender.getName())) {
                            PlayerJoinContestEvent event = new PlayerJoinContestEvent(plsender.getName(), ContestManager.getActiveEvent().getName());
                            Main.getInstance().getServer().getPluginManager().callEvent(event);
                            if (!event.isCanceled()) {
                                if (!ContestManager.getActiveEvent().isLocked()) {
                                    plsender.teleport(ContestManager.getActiveEvent().getTeleport(), PlayerTeleportEvent.TeleportCause.COMMAND);
                                    if (!PlayerManager.getJoinedEvent().containsKey(plsender)) {
                                        PlayerManager.getJoinedEvent().put(plsender, PlayerManager.playerEnteringEvent(plsender));
                                    }
                                    sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("teleport-success"))
                                                    .replace("%event%", ContestManager.getActiveEvent().getName())
                                            , true
                                            , plsender
                                    );
                                }
                                else {
                                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-locked"), true, plsender);
                                }
                            }
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("teleport-banned")).replace("%event%", ContestManager.getActiveEvent().getName()), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                    }
                }
                else {
                    sendLM(Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                }
            }
            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("info")) {
                    sendLM("&6" + Main.getPrefix() + " &eCreated by Rabbit_Hunter", true, plsender);
                    sendLM("&3Version &b" + Main.getPdf().getVersion(), true, plsender);
                }
                else if (args[0].equalsIgnoreCase("help")) {
                    //list of usages
                    sendLM(Main.getPrefix() + " " + Main.getPdf().getName() + " Usages:", true, plsender);
                    usages.forEach(usage -> sendLM("&3" + usage, true, plsender));
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator"))
                        adminUsages.forEach(usage -> sendLM("&3" + usage, true, plsender));

                }
                else if (args[0].equalsIgnoreCase("list")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.list")) {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-list"), true, plsender);
                        ContestManager.getEvents().forEach((name, event) -> sendLM("&3- " + name, true, plsender));
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    if (plsender.hasPermission("events.reload")) {
                        //config
                        Main.getInstance().reloadConfig();
                        //events
                        ContestManager.getEvents().clear();
                        Main.getFilMan().saveEvents();
                        FileManager.loadEventsFromYml(Main.getFilMan().getEventsYaml());
                        //lang
                        Main.getFilMan().setWords(YamlConfiguration.loadConfiguration(Main.getFilMan().getLangFile()).getConfigurationSection(Objects.requireNonNull(Main.getInstance().getConfig().getString("lang"))));
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("config-reloaded"), true, plsender);
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("quit")) { //done
                    if (plsender.hasPermission("events.join")) {
                        if (ContestManager.getActiveEvent() != null) {
                            if (PlayerManager.getJoinedEvent().containsKey(plsender)) {
                                PlayerLeaveContestEvent event = new PlayerLeaveContestEvent(plsender.getName(), ContestManager.getActiveEvent().getName());
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCanceled()) {
                                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-leave"), true, plsender);
                                    PlayerManager.playerLeavingEvent(plsender);
                                }
                            }
                            else {
                                sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-not-at-event"), true, plsender);
                            }
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("lock")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator")) {
                        Event event = ContestManager.getActiveEvent();
                        if (event != null) {
                            if (!event.isLocked()) {
                                event.setLocked(true);
                                sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-lock"), true, plsender);
                            }
                            else {
                                sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-already-locked"), true, plsender);
                            }
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("unlock")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator")) {
                        Event event = ContestManager.getActiveEvent();
                        if (event != null) {
                            if (event.isLocked()) {
                                event.setLocked(false);
                                sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-unlock"), true, plsender);
                            }
                            else {
                                sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-already-unlocked"), true, plsender);
                            }
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("tp")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.tp")) {
                        if (ContestManager.getActiveEvent() != null) {
                            plsender.teleport(ContestManager.getActiveEvent().getTeleport());
                            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("teleport-success"))
                                            .replace("%event%", ContestManager.getActiveEvent().getName())
                                    , true, plsender);
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("modify")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.modify")) {
                        if (ContestManager.getActiveEvent() != null) {
                            if (plsender.hasPermission("events.staff")) {
                                plsender.openInventory(ContestManager.getActiveEvent().getInventory());
                            }
                            else {
                                if (ContestManager.getActiveEvent().getOwner().equals(plsender.getName())) {
                                    plsender.openInventory(ContestManager.getActiveEvent().getInventory());
                                }
                                else {
                                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-not-owner"), true, plsender);
                                }
                            }
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("end")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator")) {
                        ContestManager.setActiveEvent(null);
                        Main.getInstance().getServer().getOnlinePlayers().forEach(player -> sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-end"), true, player));
                        PlayerManager.getJoinedEvent().forEach((player, data) -> player.teleport(data.getLocation()));
                        PlayerManager.getJoinedEvent().clear();
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("invclear")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator")) {
                        PlayerManager.getJoinedEvent().forEach((p, l) -> {
                            p.getInventory().clear();
                            p.getInventory().setHelmet(null);
                            p.getInventory().setChestplate(null);
                            p.getInventory().setLeggings(null);
                            p.getInventory().setBoots(null);
                        });
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("inventory-cleared"), true, plsender);
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else {
                    sendLM(Main.getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(Main.getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
                }
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("debug")) {
                    if (plsender.hasPermission("events.staff")) {
                        sendLM(Main.getPrefix() + " All maps and lists in plugin:", true, plsender);
                        sendLM("&c## &6Events &c##", true, plsender);
                        ContestManager.getEvents().forEach((s, e) -> sendLM("&6Name: &e" + s + " &6Owner: &e" + e.getOwner(), true, plsender));
                        sendLM("&c## &6Players Joined event &c##", true, plsender);
                        PlayerManager.getJoinedEvent().forEach((p, d) -> sendLM("&6" + p.getName() + " &fjoined &e" + d.toString(), true, plsender));
                        sendLM("&c## &6Player modifying events &c##", true, plsender);
                        PlayerManager.getModifyingEvent().forEach((p, e) -> sendLM("&6" + p.getName() + " &fmodifying &e" + e.getName(), true, plsender));
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("create")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.add")) {
                        Event event = new Event(args[1], plsender.getName(), plsender.getLocation(), null, null);
                        ContestManager.getEvents().put(args[1], event);
                        sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-created")).replace("%event%", args[1]), true, plsender);
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("remove")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.add")) {
                        if (ContestManager.getEventByName(args[1]) != null) {
                            ContestManager.getEvents().remove(args[1]);
                            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-deleted")).replace("%event%", args[1]), true, plsender);
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("tp")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.tp")) {
                        Event event = ContestManager.getEventByName(args[1]);
                        if (event != null) {
                            plsender.teleport(event.getTeleport());
                            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("teleport-success"))
                                            .replace("%event%", event.getName())
                                    , true, plsender);
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("start")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.start")) {
                        ContestManager.setActiveEvent(args[1]);
                        if (ContestManager.getActiveEvent() == null) {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-started"))
                                            .replace("%event%", ContestManager.getActiveEvent().getName())
                                    , true
                                    , plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("modify")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.modify")) {
                        Event chosenEvent = ContestManager.getEventByName(args[1]);
                        if (chosenEvent != null) {
                            if (plsender.hasPermission("events.staff")) {
                                plsender.openInventory(chosenEvent.getInventory());
                            }
                            else {
                                if (chosenEvent.getOwner().equals(plsender.getName())) {
                                    plsender.openInventory(chosenEvent.getInventory());
                                }
                                else {
                                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-not-owner"), true, plsender);
                                }
                            }
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("kick")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.kick")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        Event event = ContestManager.getActiveEvent();
                        if (event != null) {
                            if (target != null) {
                                if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                    if (!target.hasPermission("events.staff") || target.hasPermission("events.moderator")) {
                                        PlayerManager.playerLeavingEvent(target);
                                        sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("player-kicked-admin-side"))
                                                        .replace("%player%", args[1])
                                                        .replace("%event%", event.getName())
                                                , true
                                                , plsender);
                                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-kicked-player-side"), true, target);
                                    }
                                    else {
                                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-is-admin"), true, plsender);
                                    }
                                }
                                else {
                                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-not-found-at-event"), true, plsender);
                                }
                            }
                            else {
                                sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-not-found"), true, plsender);
                            }
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("ban")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.ban")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        Event event = ContestManager.getActiveEvent();
                        if (event != null) {
                            if (target != null) {
                                if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                    if (!target.hasPermission("events.staff") || target.hasPermission("events.moderator")) {
                                        sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("player-banned-admin-side"))
                                                        .replace("%player%", args[1])
                                                        .replace("%event%", event.getName())
                                                , true
                                                , plsender
                                        );
                                        sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("player-banned-player-side"))
                                                        .replace("%event%", event.getName())
                                                , true
                                                , target);
                                        PlayerManager.playerLeavingEvent(target);
                                        //banned
                                        List<String> banned = ContestManager.getActiveEvent().getBanned();
                                        debugMessage("Banned?: " + banned.add(target.getName()));
                                    }
                                    else {
                                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-is-admin"), true, plsender);
                                    }
                                }
                                else {
                                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-not-found-at-event"), true, plsender);
                                }
                            }
                            else {
                                sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-not-found"), true, plsender);
                            }
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else {
                    sendLM(Main.getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(Main.getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
                }
            }
            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("unban")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.ban")) {
                        Event event = ContestManager.getEventByName(args[2]);
                        String target = args[1];
                        if (event != null) {
                            if (event.getBanned().remove(target)) {
                                sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-unbanned"), true, plsender);
                            }
                            else {
                                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("banned-player-not-found")).replace("%event%", event.getName()), true, plsender);
                            }
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else {
                    sendLM(Main.getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(Main.getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
                }
            }
            else {
                if (args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("b")) { // TODO: 03.12.2019 broadcast rework
                    if (plsender.hasPermission("events.moderator") || plsender.hasPermission("events.broadcast")) {
                        if (ContestManager.getActiveEvent() != null) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Main.getFilMan().getWords().getString("event-broadcast"))
                                    .replace("%event%", ContestManager.getActiveEvent().getName())
                                    .replace("%time%", args[1])));
                        }
                    }
                    else
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                }
                else
                    sendLM(Main.getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(Main.getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
            }
        }
        return true;
    }
}