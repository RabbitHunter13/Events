package com.rabbit13.events.commands;

import com.rabbit13.events.commands.tablisteners.EventTabCompleter;
import com.rabbit13.events.events.PlayerJoinContestEvent;
import com.rabbit13.events.events.PlayerLeaveContestEvent;
import com.rabbit13.events.events.ePlayerJoinContestEvent;
import com.rabbit13.events.events.ePlayerLeaveContestEvent;
import com.rabbit13.events.managers.BackupItemsManager;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.PlayerManager;
import com.rabbit13.events.objects.eData;
import com.rabbit13.events.objects.eEvent;
import com.rabbit13.events.objects.eEventLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.rabbit13.events.main.Main.*;
import static com.rabbit13.events.main.Misc.debugMessage;
import static com.rabbit13.events.main.Misc.sendLM;

public final class EventExecutor implements CommandExecutor {
    private final List<String> usages;
    private final List<String> adminUsages;

    // TODO: 11.12.2019 /event backup list | <player_name> + tab complete
    public EventExecutor() {
        usages = getInstance().getConfig().getStringList("usages");
        adminUsages = getInstance().getConfig().getStringList("usages-admin");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean pass = false;
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("b")) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', buildStringFromArgs(args)));
                return true;
            }
            else {
                for (String subCommand : EventTabCompleter.getSubCommands()) {
                    if (args[0].equalsIgnoreCase(subCommand)) {
                        pass = true;
                        break;
                    }
                }
                if (!pass) {
                    if (!(sender instanceof Player)) {
                        sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", false, sender);
                    }
                    else {
                        sendLM(getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", true, sender);
                    }
                    return true;
                }
            }
        }

        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sendLM(getPluginPrefix() + " This command have to be executed as player!", false, sender);
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("info")) {
                    sendLM("&6" + getPrefix() + " &eCreated by Rabbit_Hunter", false, sender);
                    sendLM("&3Version &b" + getPdf().getVersion(), false, sender);
                }
                else if (args[0].equalsIgnoreCase("help")) {
                    //list of usages
                    sendLM(getPrefix() + " " + getPdf().getName() + " Usages:", false, sender);
                    usages.forEach(usage -> sendLM("&3" + usage, false, sender));
                    adminUsages.forEach(usage -> sendLM("&3" + usage, false, sender));

                }
                else if (args[0].equalsIgnoreCase("list")) {
                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-official"), false, sender);
                    List<eEvent> otherEvents = new ArrayList<>();
                    EventManager.getEvents().forEach((name, event) -> {
                        assert event.getTeleport().getWorld() != null;
                        if (event.getTeleport().getWorld().getName().equalsIgnoreCase(getInstance().getConfig().getString("event-world"))) {
                            sendLM("&3\u2022 " + name, false, sender);
                        }
                        else {
                            otherEvents.add(event);
                        }
                    });
                    if (!otherEvents.isEmpty()) {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-other"), false, sender);
                        otherEvents.forEach(event -> {
                            assert event.getTeleport().getWorld() != null;
                            sendLM("&3[" + event.getTeleport().getWorld().getName() + "] &3\u2022 " + event.getName(), false, sender);
                        });
                    }
                }
                else if (args[0].equalsIgnoreCase("invclear")) {
                    clearInventory();
                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("inventory-cleared"), false, sender);
                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    //config
                    getInstance().reloadConfig();
                    //events & counter
                    getFilMan().loadEvents();
                    getFilMan().loadCounter();
                    //lang
                    getFilMan().setWords(YamlConfiguration.loadConfiguration(getFilMan().getLangFile()).getConfigurationSection(Objects.requireNonNull(getInstance().getConfig().getString("lang"))));
                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("config-reloaded"), false, sender);
                }
                else if (args[0].equalsIgnoreCase("lock")) {
                    eEvent event = EventManager.getActiveEvent();
                    if (event != null) {
                        if (!event.isLocked()) {
                            event.setLocked(true);
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-lock"), false, sender);
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-already-locked"), false, sender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), false, sender);
                    }
                }
                else if (args[0].equalsIgnoreCase("unlock")) {
                    eEvent event = EventManager.getActiveEvent();
                    if (event != null) {
                        if (event.isLocked()) {
                            event.setLocked(false);
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-unlock"), false, sender);
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-already-unlocked"), false, sender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), false, sender);
                    }
                }
                else if (args[0].equalsIgnoreCase("end")) { //done
                    EventManager.setActiveEvent(null);
                    getInstance().getServer().getOnlinePlayers().forEach(player -> sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-end"), true, player));
                    Map<Player, eData> tempHolder = new HashMap<>(PlayerManager.getJoinedEvent());
                    tempHolder.forEach((p, d) -> PlayerManager.playerLeavingEvent(p));
                    PlayerManager.getJoinedEvent().clear();
                }
                else {
                    sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", false, sender);
                }
                return true;
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("start")) { //done
                    EventManager.setActiveEvent(args[1]);
                    if (EventManager.getActiveEvent() == null) {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), false, sender);
                    }
                    else {
                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-started"))
                                        .replace("%event%", EventManager.getActiveEvent().getName())
                                , true
                                , sender);
                    }
                }
                else if (args[0].equalsIgnoreCase("remove")) { //done
                    if (EventManager.getEventByName(args[1]) != null) {
                        EventManager.getEvents().remove(args[1]);
                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-deleted")).replace("%event%", args[1]), false, sender);
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), false, sender);
                    }
                }
                else if (args[0].equalsIgnoreCase("kick")) { //done
                    Player target = Bukkit.getPlayer(args[1]);
                    eEvent event = EventManager.getActiveEvent();
                    if (event != null) {
                        if (target != null) {
                            if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                if (!target.hasPermission("events.staff") || target.hasPermission("events.moderator")) {
                                    PlayerManager.playerLeavingEvent(target);
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("player-kicked-admin-side"))
                                                    .replace("%player%", args[1])
                                                    .replace("%event%", event.getName())
                                            , false
                                            , sender);
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-kicked-player-side"), true, target);
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-is-admin"), false, sender);
                                }
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-found-at-event"), false, sender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-found"), false, sender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), false, sender);
                    }
                }
                else if (args[0].equalsIgnoreCase("ban")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    eEvent event = EventManager.getActiveEvent();
                    if (event != null) {
                        if (target != null) {
                            if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                if (!target.hasPermission("events.staff") || target.hasPermission("events.moderator")) {
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("player-banned-admin-side"))
                                                    .replace("%player%", args[1])
                                                    .replace("%event%", event.getName())
                                            , false
                                            , sender
                                    );
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("player-banned-player-side"))
                                                    .replace("%event%", event.getName())
                                            , true
                                            , target);
                                    PlayerManager.playerLeavingEvent(target);
                                    //banned
                                    List<String> banned = EventManager.getActiveEvent().getBanned();
                                    debugMessage("Banned?: " + banned.add(target.getName()));
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-is-admin"), false, sender);
                                }
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-found-at-event"), false, sender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-found"), false, sender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), false, sender);
                    }
                }
                else {
                    sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", false, sender);
                }
                return true;
            }
            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("unban")) { //done
                    eEvent event = EventManager.getEventByName(args[2]);
                    String target = args[1];
                    if (event != null) {
                        if (event.getBanned().remove(target)) {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-unbanned"), false, sender);
                        }
                        else {
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("banned-player-not-found")).replace("%event%", event.getName()), false, sender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), false, sender);
                    }
                }
                else {
                    sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", false, sender);
                }
                return true;
            }
        }
        else {
            Player plsender = (Player) sender;
            if (args.length == 0) {
                if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.join")) { //done
                    if (EventManager.getActiveEvent() != null) {
                        if (!EventManager.getActiveEvent().getBanned().contains(plsender.getName())) {
                            eData data = new eData(plsender.getInventory().getHelmet()
                                    , plsender.getInventory().getChestplate()
                                    , plsender.getInventory().getLeggings()
                                    , plsender.getInventory().getBoots()
                                    , plsender.getInventory().getContents().clone()
                                    , plsender.getActivePotionEffects()
                                    , plsender.getLocation());
                            PlayerJoinContestEvent event = new ePlayerJoinContestEvent(plsender.getName(), EventManager.getActiveEvent(), data);
                            getInstance().getServer().getPluginManager().callEvent(event);
                            if (!event.isCanceled()) {
                                if (!EventManager.getActiveEvent().isLocked()) {
                                    if (!PlayerManager.getJoinedEvent().containsKey(plsender)) {
                                        PlayerManager.getJoinedEvent().put(plsender, PlayerManager.playerEnteringEvent(plsender));
                                    }
                                    plsender.teleport(EventManager.getActiveEvent().getTeleport(), PlayerTeleportEvent.TeleportCause.COMMAND);
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("teleport-success"))
                                                    .replace("%event%", EventManager.getActiveEvent().getName())
                                            , true
                                            , plsender
                                    );
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-locked"), true, plsender);
                                }
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("teleport-banned")).replace("%event%", EventManager.getActiveEvent().getName()), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                    }
                }
                else {
                    sendLM(getFilMan().getWords().getString("no-permission"), true, plsender);
                }
                return true;
            }
            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("info")) {
                    sendLM("&6" + getPrefix() + " &eCreated by Rabbit_Hunter", true, plsender);
                    sendLM("&3Version &b" + getPdf().getVersion(), true, plsender);
                }
                else if (args[0].equalsIgnoreCase("debug")) {
                    if (plsender.hasPermission("events.staff")) {
                        sendLM(getPrefix() + " All maps and lists in plugin:", true, plsender);
                        sendLM("&c## &6Events &c##", true, plsender);
                        EventManager.getEvents().forEach((s, e) -> sendLM("&6Name: &e" + s + " &6Owner: &e" + e.getOwner(), true, plsender));
                        sendLM("&c## &6Players Joined event &c##", true, plsender);
                        PlayerManager.getJoinedEvent().forEach((p, d) -> sendLM("&6" + p.getName() + " &fjoined &e" + d.toString(), true, plsender));
                        sendLM("&c## &6Player modifying events &c##", true, plsender);
                        PlayerManager.getModifyingEvent().forEach((p, e) -> sendLM("&6" + p.getName() + " &fmodifying &e" + e.getName(), true, plsender));
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("help")) {
                    //list of usages
                    sendLM(getPrefix() + " " + getPdf().getName() + " Usages:", true, plsender);
                    usages.forEach(usage -> sendLM("&3" + usage, true, plsender));
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator"))
                        adminUsages.forEach(usage -> sendLM("&3" + usage, true, plsender));

                }
                else if (args[0].equalsIgnoreCase("list")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.list")) {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-official"), true, plsender);
                        List<eEvent> otherEvents = new ArrayList<>();
                        EventManager.getEvents().forEach((name, event) -> {
                            assert event.getTeleport().getWorld() != null;
                            if (event.getTeleport().getWorld().getName().equalsIgnoreCase(getInstance().getConfig().getString("event-world"))) {
                                sendLM("&3\u2022 " + name, true, plsender);
                            }
                            else {
                                otherEvents.add(event);
                            }
                        });
                        if (!otherEvents.isEmpty()) {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-other"), true, plsender);
                            otherEvents.forEach(event -> {
                                assert event.getTeleport().getWorld() != null;
                                sendLM("&3[" + event.getTeleport().getWorld().getName() + "] &3\u2022 " + event.getName(), true, plsender);
                            });
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("win")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.join")) {
                        if (PlayerManager.getWinCounter().get(plsender.getName()) != null) {
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("win-counter-show"))
                                            .replace("%wins%", PlayerManager.getWinCounter().get(plsender.getName()).toString())
                                    , true, plsender);
                        }
                        else {
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("win-counter-show"))
                                            .replace("%wins%", "0")
                                    , true, plsender);
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    if (plsender.hasPermission("events.reload")) {
                        //config
                        getInstance().reloadConfig();
                        //events
                        getFilMan().loadEvents();
                        getFilMan().loadCounter();
                        //lang
                        getFilMan().setWords(YamlConfiguration.loadConfiguration(getFilMan().getLangFile()).getConfigurationSection(Objects.requireNonNull(getInstance().getConfig().getString("lang"))));
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("config-reloaded"), true, plsender);
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("quit")) { //done
                    if (plsender.hasPermission("events.join")) {
                        if (EventManager.getActiveEvent() != null) {
                            if (PlayerManager.getJoinedEvent().containsKey(plsender)) {
                                PlayerLeaveContestEvent event = new ePlayerLeaveContestEvent(plsender.getName(), EventManager.getActiveEvent());
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCanceled()) {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-leave"), true, plsender);
                                    PlayerManager.playerLeavingEvent(plsender);
                                }
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-at-event"), true, plsender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("lock")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator")) {
                        eEvent event = EventManager.getActiveEvent();
                        if (event != null) {
                            if (!event.isLocked()) {
                                event.setLocked(true);
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-lock"), true, plsender);
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-already-locked"), true, plsender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("unlock")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator")) {
                        eEvent event = EventManager.getActiveEvent();
                        if (event != null) {
                            if (event.isLocked()) {
                                event.setLocked(false);
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-unlock"), true, plsender);
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-already-unlocked"), true, plsender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("tp")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.tp")) {
                        if (EventManager.getActiveEvent() != null) {
                            plsender.teleport(EventManager.getActiveEvent().getTeleport());
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("teleport-success"))
                                            .replace("%event%", EventManager.getActiveEvent().getName())
                                    , true, plsender);
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("modify")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.modify")) {
                        if (EventManager.getActiveEvent() != null) {
                            if (plsender.hasPermission("events.staff")) {
                                plsender.openInventory(EventManager.getActiveEvent().getInventory());
                            }
                            else {
                                if (EventManager.getActiveEvent().getOwner().equals(plsender.getName())) {
                                    plsender.openInventory(EventManager.getActiveEvent().getInventory());
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-owner"), true, plsender);
                                }
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("end")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator")) {
                        EventManager.setActiveEvent(null);
                        getInstance().getServer().getOnlinePlayers().forEach(player -> sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-end"), true, player));
                        Map<Player, eData> tempHolder = new HashMap<>(PlayerManager.getJoinedEvent());
                        tempHolder.forEach((p, d) -> PlayerManager.playerLeavingEvent(p));
                        PlayerManager.getJoinedEvent().clear();
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("invclear")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator")) {
                        clearInventory();
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("inventory-cleared"), true, plsender);
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else {
                    sendLM(getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
                }
                return true;
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.add")) {
                        eEvent event = new eEvent(args[1], plsender.getName(), new eEventLocation(plsender.getLocation()), null, null);
                        EventManager.getEvents().put(args[1], event);
                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-created")).replace("%event%", args[1]), true, plsender);
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("remove")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.add")) {
                        if (EventManager.getEventByName(args[1]) != null) {
                            EventManager.getEvents().remove(args[1]);
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-deleted")).replace("%event%", args[1]), true, plsender);
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("tp")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.tp")) {
                        eEvent event = EventManager.getEventByName(args[1]);
                        if (event != null) {
                            plsender.teleport(event.getTeleport());
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("teleport-success"))
                                            .replace("%event%", event.getName())
                                    , true, plsender);
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("start")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.start")) {
                        EventManager.setActiveEvent(args[1]);
                        if (EventManager.getActiveEvent() == null) {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                        else {
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-started"))
                                            .replace("%event%", EventManager.getActiveEvent().getName())
                                    , true
                                    , plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("win")) {
                    if (args[1].equalsIgnoreCase("list")) {
                        if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.list")) {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("win-counter-list"), true, plsender);
                            PlayerManager.getWinCounter().forEach((name, number) -> sendLM("&3\u2022 " + name + ": " + number, true, plsender));
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                        }
                    }
                    else {
                        if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.list")) {
                            if (PlayerManager.getWinCounter().get(plsender.getName()) != null) {
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("win-counter-show"))
                                                .replace("%wins%", PlayerManager.getWinCounter().get(args[1]).toString())
                                        , true, plsender);
                            }
                            else {
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("win-counter-show"))
                                                .replace("%wins%", "0")
                                        , true, plsender);
                            }
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("backup")){
                    if(plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.backup")){
                        plsender.openInventory(BackupItemsManager.getBackup(plsender,args[1]));
                    } else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("modify")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.modify")) {
                        eEvent chosenEvent = EventManager.getEventByName(args[1]);
                        if (chosenEvent != null) {
                            if (plsender.hasPermission("events.staff")) {
                                plsender.openInventory(chosenEvent.getInventory());
                            }
                            else {
                                if (chosenEvent.getOwner().equals(plsender.getName())) {
                                    plsender.openInventory(chosenEvent.getInventory());
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-owner"), true, plsender);
                                }
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("kick")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.kick")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        eEvent event = EventManager.getActiveEvent();
                        if (event != null) {
                            if (target != null) {
                                if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                    if (!target.hasPermission("events.staff") || target.hasPermission("events.moderator")) {
                                        PlayerManager.playerLeavingEvent(target);
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("player-kicked-admin-side"))
                                                        .replace("%player%", args[1])
                                                        .replace("%event%", event.getName())
                                                , true
                                                , plsender);
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-kicked-player-side"), true, target);
                                    }
                                    else {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-is-admin"), true, plsender);
                                    }
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-found-at-event"), true, plsender);
                                }
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-found"), true, plsender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("ban")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.ban")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        eEvent event = EventManager.getActiveEvent();
                        if (event != null) {
                            if (target != null) {
                                if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                    if (!target.hasPermission("events.staff") || target.hasPermission("events.moderator")) {
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("player-banned-admin-side"))
                                                        .replace("%player%", args[1])
                                                        .replace("%event%", event.getName())
                                                , true
                                                , plsender
                                        );
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("player-banned-player-side"))
                                                        .replace("%event%", event.getName())
                                                , true
                                                , target);
                                        PlayerManager.playerLeavingEvent(target);
                                        //banned
                                        List<String> banned = EventManager.getActiveEvent().getBanned();
                                        debugMessage("Banned?: " + banned.add(target.getName()));
                                    }
                                    else {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-is-admin"), true, plsender);
                                    }
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-found-at-event"), true, plsender);
                                }
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-found"), true, plsender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else {
                    sendLM(getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
                }
                return true;
            }
            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("unban")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.ban")) {
                        eEvent event = EventManager.getEventByName(args[2]);
                        String target = args[1];
                        if (event != null) {
                            if (event.getBanned().remove(target)) {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-unbanned"), true, plsender);
                            }
                            else {
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("banned-player-not-found")).replace("%event%", event.getName()), true, plsender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("win")) {
                    if (args[1].equalsIgnoreCase("add")) {
                        if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.win")) {
                            if (!PlayerManager.getWinCounter().containsKey(args[2])) {
                                PlayerManager.getWinCounter().put(args[2], 1);
                            }
                            else {
                                PlayerManager.getWinCounter().put(args[2], PlayerManager.getWinCounter().get(args[2]) + 1);
                            }
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("win-counter-add"))
                                            .replace("%player%", args[2])
                                    , true, plsender);
                            getFilMan().saveCounter();
                        }
                    }
                    else {
                        sendLM(getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
                    }
                }
                else {
                    sendLM(getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
                }
                return true;
            }
            else {
                sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", false, sender);
            }
        }
        return true;
    }

    private void clearInventory() {
        PlayerManager.getJoinedEvent().forEach((p, l) -> {
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);
        });
    }

    private String buildStringFromArgs(@NotNull String[] args) {
        String prefix = null;
        if (EventManager.getActiveEvent() != null) {
            prefix = "&4[&c" + EventManager.getActiveEvent().getName() + "&4]&f";
        }
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (!args[i].isEmpty()) {
                message.append(" ").append(args[i]);
            }
        }
        return (prefix != null) ? prefix + message.toString() : getPrefix() + message.toString();
    }
}
