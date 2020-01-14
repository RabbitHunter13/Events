package com.rabbit13.events.commands;

import com.rabbit13.events.events.PlayerJoinContestEvent;
import com.rabbit13.events.events.PlayerLeaveContestEvent;
import com.rabbit13.events.events.RabPlayerJoinContestEvent;
import com.rabbit13.events.events.RabPlayerLeaveContestEvent;
import com.rabbit13.events.managers.BackupItemsManager;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.PlayerManager;
import com.rabbit13.events.objects.PlayerData;
import com.rabbit13.events.objects.RabPlayerData;
import com.rabbit13.events.objects.event.Event;
import com.rabbit13.events.objects.event.RabEvent;
import com.rabbit13.events.objects.event.tools.RabEventLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.rabbit13.events.main.Main.*;
import static com.rabbit13.events.main.Misc.debugMessage;
import static com.rabbit13.events.main.Misc.sendLM;
import static org.bukkit.Bukkit.getPlayer;

public final class EventExecutor implements CommandExecutor {
    private final List<String> usages;
    private final List<String> adminUsages;

    public EventExecutor() {
        usages = getInstance().getConfig().getStringList("usages");
        adminUsages = getInstance().getConfig().getStringList("usages-admin");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sendLM(getPluginPrefix() + " This command have to be executed as player!", false, sender);
                return true;
            }
            else {
                if (args[0].equalsIgnoreCase("info")) {
                    if (args.length == 1) {
                        sendLM("&6" + getPrefix() + " &eCreated by Rabbit_Hunter13", false, sender);
                        sendLM("&3Version &b" + getPdf().getVersion(), false, sender);
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("b")) {
                    if (args.length > 1) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', buildStringFromArgs(args)));
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("help")) {
                    if (args.length == 1) {
                        //list of usages
                        sendLM(getPrefix() + " " + getPdf().getName() + " Usages:", false, sender);
                        usages.forEach(usage -> sendLM("&3" + usage, false, sender));
                        adminUsages.forEach(usage -> sendLM("&3" + usage, false, sender));
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("list")) {
                    if (args.length == 1) {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-official"), false, sender);
                        List<Event> otherEvents = new ArrayList<>();
                        if (!EventManager.getEvents().isEmpty()) {
                            EventManager.getEvents().forEach((name, event) -> {
                                assert event.getTeleport().getLocation().getWorld() != null;
                                if (event.getTeleport().getLocation().getWorld().getName().equalsIgnoreCase(getInstance().getConfig().getString("event-world"))) {
                                    sendLM("&3\u2022 " + name, false, sender);
                                }
                                else {
                                    otherEvents.add(event);
                                }
                            });
                        }
                        if (!otherEvents.isEmpty()) {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-other"), false, sender);
                            otherEvents.forEach(event -> {
                                assert event.getTeleport().getLocation().getWorld() != null;
                                sendLM("&3[" + event.getTeleport().getLocation().getWorld().getName() + "] &3\u2022 " + event.getName(), false, sender);
                            });
                        }
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("invclear")) {
                    if (args.length == 1) {
                        clearInventory();
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("inventory-cleared"), false, sender);
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    if (args.length == 1) {
                        //config
                        getInstance().reloadConfig();
                        //events & counter
                        getFilMan().loadEvents();
                        getFilMan().loadCounter();
                        //lang
                        getFilMan().setWords(YamlConfiguration.loadConfiguration(getFilMan().getLangFile()).getConfigurationSection(Objects.requireNonNull(getInstance().getConfig().getString("lang"))));
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("config-reloaded"), false, sender);
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("lock")) {
                    if (args.length == 1) {
                        Event event = EventManager.getActiveEvent();
                        if (event != null) {
                            if (!event.isLockedTeleport()) {
                                event.setLockedTeleport(true);
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
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("unlock")) {
                    if (args.length == 1) {
                        Event event = EventManager.getActiveEvent();
                        if (event != null) {
                            if (event.isLockedTeleport()) {
                                event.setLockedTeleport(false);
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
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("end")) {
                    if (args.length == 1) {
                        EventManager.setActiveEvent(null);
                        getInstance().getServer().getOnlinePlayers().forEach(player -> sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-end"), true, player));
                        Map<Player, PlayerData> tempHolder = new HashMap<>(PlayerManager.getJoinedEvent());
                        tempHolder.forEach((p, d) -> PlayerManager.playerLeavingEvent(p));
                        PlayerManager.getJoinedEvent().clear();
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("start")) {
                    if (args.length == 2) {
                        if (EventManager.getActiveEvent() == null) {
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
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-already-started"), false, sender);
                        }
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 2) {
                        if (EventManager.getEventByName(args[1]) != null) {
                            EventManager.getEvents().remove(args[1]);
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-deleted")).replace("%event%", args[1]), false, sender);
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), false, sender);
                        }
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("kick")) {
                    if (args.length == 2) {
                        Player target = getPlayer(args[1]);
                        Event event = EventManager.getActiveEvent();
                        if (event != null) {
                            if (target != null) {
                                if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                    if (!target.hasPermission("events.staff")
                                            || target.hasPermission("events.moderator")) {
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
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("ban")) {
                    if (args.length == 2) {
                        Player target = getPlayer(args[1]);
                        Event event = EventManager.getActiveEvent();
                        if (event != null) {
                            if (target != null) {
                                if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                    if (!target.hasPermission("events.staff")
                                            || !target.hasPermission("events.moderator")) {
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
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("checkpoint")) {
                    if (args.length == 2) {
                        if (EventManager.getActiveEvent() != null) {
                            if (args[1].equalsIgnoreCase("removeall")) {
                                EventManager.getActiveEvent().getCheckpoints().clear();
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed-all"), false, sender);
                            }
                            else if (args[1].equalsIgnoreCase("list")) {
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-list"))
                                               .replace("%event%", EventManager.getActiveEvent().getName())
                                        , false, sender);
                                EventManager.getActiveEvent().getCheckpoints().forEach(chp -> sendLM("&3\u2022 "
                                                                                                             + "[" + chp.getBlockX()
                                                                                                             + ", " + chp.getBlockY()
                                                                                                             + ", " + chp.getBlockZ()
                                                                                                             + "]"
                                        , false, sender)
                                );
                            }
                            else {
                                sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", false, sender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), false, sender);
                        }
                    }
                    if (args.length == 3) {
                        Event event = EventManager.getEventByName(args[2]);
                        if (event != null) {
                            if (args[1].equalsIgnoreCase("removeall")) {
                                event.getCheckpoints().clear();
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed-all"), false, sender);
                            }
                            else if (args[1].equalsIgnoreCase("list")) {
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-list"))
                                               .replace("%event%", event.getName())
                                        , false, sender);
                                EventManager.getActiveEvent().getCheckpoints().forEach(chp -> sendLM("&3\u2022 "
                                                                                                             + "[" + chp.getBlockX()
                                                                                                             + ", " + chp.getBlockY()
                                                                                                             + ", " + chp.getBlockZ()
                                                                                                             + "]"
                                        , false, sender)
                                );
                            }
                            else {
                                sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", false, sender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), false, sender);
                        }
                    }
                    else {
                        wrongCommand(sender);
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("unban")) {
                    if (args.length == 3) {
                        Event event = EventManager.getEventByName(args[2]);
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
                        wrongCommand(sender);
                    }
                    return true;
                }
                else {
                    wrongCommand(sender);
                    return true;
                }
            }
        }
        else {
            Player plsender = (Player) sender;
            if (args.length == 0) {
                if (plsender.hasPermission("events.staff")
                        || plsender.hasPermission("events.join")) {
                    if (EventManager.getActiveEvent() != null) {
                        if (!EventManager.getActiveEvent().isLockedTeleport()) {
                            if (!EventManager.getActiveEvent().getBanned().contains(plsender.getName())) {
                                PlayerData data = new RabPlayerData(plsender.getInventory().getContents().clone(),
                                                                    plsender.getActivePotionEffects(),
                                                                    Objects.requireNonNull(plsender.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue(),
                                                                    plsender.getExp(),
                                                                    plsender.getLevel(),
                                                                    plsender.getLocation());
                                PlayerJoinContestEvent event = new RabPlayerJoinContestEvent(plsender.getName(), EventManager.getActiveEvent(), data);
                                getInstance().getServer().getPluginManager().callEvent(event);
                                if (!PlayerManager.getJoinedEvent().containsKey(plsender)) {
                                    if (!event.isCanceled()) {
                                        PlayerManager.getJoinedEvent().put(plsender, data);
                                        PlayerManager.playerEnteringEvent(plsender);
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("teleport-success"))
                                                       .replace("%event%", EventManager.getActiveEvent().getName())
                                                , true
                                                , plsender
                                        );
                                    }
                                }
                                if (!event.isCanceled()) {
                                    if (PlayerManager.getCheckpointed().containsKey(plsender)) {
                                        plsender.teleport(PlayerManager.getCheckpointed().get(plsender).getSavedLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                                    }
                                    else {
                                        plsender.teleport(EventManager.getActiveEvent().getTeleport().getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                                    }
                                }
                            }
                            else {
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("teleport-banned"))
                                        .replace("%event%", EventManager.getActiveEvent().getName()), true, plsender);
                            }
                        }
                        else {
                            if (PlayerManager.getCheckpointed().containsKey(plsender))
                                plsender.teleport(PlayerManager.getCheckpointed().get(plsender).getSavedLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                            else
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-locked"), true, plsender);
                        }
                    }
                    else {
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                    }
                }
                else {
                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                }
                return true;
            }
            else {
                if (args[0].equalsIgnoreCase("info")) {
                    if (args.length == 1) {
                        sendLM("&6" + getPrefix() + " &eCreated by Rabbit_Hunter13", true, plsender);
                        sendLM("&3Version &b" + getPdf().getVersion(), true, plsender);
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("b")) {
                    if (args.length > 1) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.broadcast")) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', buildStringFromArgs(args)));
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                        }
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("debug")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.staff")) {
                            sendLM(getPrefix() + " All maps and lists in plugin:", true, plsender);
                            sendLM("&c## &6Events &c##", true, plsender);
                            EventManager.getEvents().forEach((s, e) -> sendLM("&6Name: &e" + s + " &6Owner: &e" + e.getOwner(), true, plsender));
                            sendLM("&c## &6Players Joined event &c##", true, plsender);
                            PlayerManager.getJoinedEvent().forEach((p, d) -> sendLM("&6" + p.getName() + " &fjoined &e" + d.toString(), true, plsender));
                            sendLM("&c## &6Players modifying &c##", true, plsender);
                            PlayerManager.getModifyingEvent().forEach((p, entry) -> sendLM("&6" +
                                                                                                   p.getName() +
                                                                                                   " &fModifying Event &e" + entry.getValue().getName() +
                                                                                                   " | Slot: " + entry.getKey(), true, plsender));
                            PlayerManager.getModifyingMods().forEach((p, entry) -> sendLM("&6" +
                                                                                                  p.getName() +
                                                                                                  " &fModifying Mods &e" +
                                                                                                  " | Slot: " + entry.getKey(), true, plsender));
                            sendLM("&c## &6Players checkpointed &c##", true, plsender);
                            PlayerManager.getCheckpointed().forEach((p, c) -> sendLM("&6" + p.getName() + "&fcheckpoint: " + c.toString()
                                    , true, plsender)
                            );
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                        }
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("help")) {
                    if (args.length == 1) {
                        //list of usages
                        sendLM(getPrefix() + " " + getPdf().getName() + " Usages:", true, plsender);
                        usages.forEach(usage -> sendLM("&3" + usage, true, plsender));
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator"))
                            adminUsages.forEach(usage -> sendLM("&3" + usage, true, plsender));
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("list")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.list")) {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-official"), true, plsender);
                            List<Event> otherEvents = new ArrayList<>();
                            EventManager.getEvents().forEach((name, event) -> {
                                assert event.getTeleport().getLocation().getWorld() != null;
                                if (event.getTeleport().getLocation().getWorld().getName().equalsIgnoreCase(getInstance().getConfig().getString("event-world"))) {
                                    sendLM("&3\u2022 " + name, true, plsender);
                                }
                                else {
                                    otherEvents.add(event);
                                }
                            });
                            if (!otherEvents.isEmpty()) {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-other"), true, plsender);
                                otherEvents.forEach(event -> {
                                    assert event.getTeleport().getLocation().getWorld() != null;
                                    sendLM("&3[" + event.getTeleport().getLocation().getWorld().getName() + "] &3\u2022 " + event.getName(), true, plsender);
                                });
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                        }
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("win")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.join")) {
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
                    else if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("list")) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.win.list")) {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("win-counter-list"), true, plsender);
                                PlayerManager.getWinCounter().forEach((name, number) -> sendLM("&3\u2022 " + name + ": " + number, true, plsender));
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                            }
                        }
                        else if (args[1].equalsIgnoreCase(plsender.getName())
                                || args[1].equalsIgnoreCase("me")) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.win.list.himself")) {
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
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                            }
                        }
                        else {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.win.list.others")) {
                                if (PlayerManager.getWinCounter().get(args[1]) != null) {
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
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                            }
                        }
                    }
                    else if (args.length == 3) {
                        if (args[1].equalsIgnoreCase("add")) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.win")) {
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
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    if (args.length == 1) {
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("checkpoint") || args[0].equalsIgnoreCase("ch")) {
                    if (args.length == 2) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.checkpoint.set")) {
                            if (EventManager.getActiveEvent() != null) {
                                if (plsender.getWorld().equals(EventManager.getActiveEvent().getTeleport().getLocation().getWorld())) {
                                    if (args[1].equalsIgnoreCase("set")) {
                                        if (plsender.getWorld().equals(EventManager.getActiveEvent().getTeleport().getLocation().getWorld())) {
                                            EventManager.getActiveEvent().getCheckpoints().add(plsender.getLocation().getBlock().getLocation());
                                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-added"))
                                                           .replace("%location%", "[" + plsender.getLocation().getBlockX()
                                                                   + "," + plsender.getLocation().getBlockY()
                                                                   + "," + plsender.getLocation().getBlockZ() + "]")
                                                    , true, plsender);
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("remove")) {
                                        if (EventManager.getActiveEvent().getCheckpoints().remove(plsender.getLocation().getBlock().getLocation()))
                                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-removed"))
                                                    .replace("%location%", ("[X: " + plsender.getLocation().getBlockX() +
                                                            "Y: " + plsender.getLocation().getBlockY() +
                                                            "Z: " + plsender.getLocation().getBlockZ() + "]")), true, plsender);
                                        else
                                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-not-found"), true, plsender);
                                    }
                                    else if (args[1].equalsIgnoreCase("removeall")) {
                                        EventManager.getActiveEvent().getCheckpoints().clear();
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed-all"), true, plsender);
                                    }
                                    else if (args[1].equalsIgnoreCase("view")) {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-show"), true, plsender);
                                        for (Location chp : EventManager.getActiveEvent().getCheckpoints()) {
                                            sendFakeBlocks(plsender, chp);
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("list")) {
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-list"))
                                                       .replace("%event%", EventManager.getActiveEvent().getName())
                                                , true, plsender);
                                        EventManager.getActiveEvent().getCheckpoints().forEach(chp -> sendLM("&3\u2022 "
                                                                                                                     + "[" + chp.getBlockX()
                                                                                                                     + ", " + chp.getBlockY()
                                                                                                                     + ", " + chp.getBlockZ()
                                                                                                                     + "]"
                                                , true, plsender)
                                        );
                                    }
                                    else {
                                        sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", false, sender);
                                    }
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("not-same-world-as-event"), true, plsender);
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
                    else if (args.length == 3) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.checkpoint.set")) {
                            Event event = EventManager.getEventByName(args[2]);
                            if (event != null) {
                                if (plsender.getWorld().equals(event.getTeleport().getLocation().getWorld())) {
                                    if (args[1].equalsIgnoreCase("set")) {
                                        if (plsender.getWorld().equals(event.getTeleport().getLocation().getWorld())) {
                                            event.getCheckpoints().add(plsender.getLocation().getBlock().getLocation());
                                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-added"))
                                                           .replace("%location%", "[" + plsender.getLocation().getBlockX()
                                                                   + "," + plsender.getLocation().getBlockY()
                                                                   + "," + plsender.getLocation().getBlockZ() + "]")
                                                    , true, plsender);
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("remove")) {
                                        if (event.getCheckpoints().remove(plsender.getLocation().getBlock().getLocation()))
                                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed"), true, plsender);
                                        else
                                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-not-found"), true, plsender);
                                    }
                                    else if (args[1].equalsIgnoreCase("removeall")) {
                                        event.getCheckpoints().clear();
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed-all"), true, plsender);
                                    }
                                    else if (args[1].equalsIgnoreCase("view")) {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-show"), true, plsender);
                                        for (Location chp : event.getCheckpoints()) {
                                            sendFakeBlocks(plsender, chp);
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("list")) {
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-list"))
                                                       .replace("%event%", event.getName())
                                                , true, plsender);
                                        event.getCheckpoints().forEach(chp -> sendLM("&3\u2022 "
                                                                                             + "[" + chp.getBlockX()
                                                                                             + ", " + chp.getBlockY()
                                                                                             + ", " + chp.getBlockZ()
                                                                                             + "]"
                                                , true, plsender)
                                        );
                                    }
                                    else {
                                        sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", false, sender);
                                    }
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("not-same-world-as-event"), true, plsender);
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("quit")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.join")) {
                            if (EventManager.getActiveEvent() != null) {
                                if (PlayerManager.getJoinedEvent().containsKey(plsender)) {
                                    PlayerLeaveContestEvent event = new RabPlayerLeaveContestEvent(plsender.getName(), EventManager.getActiveEvent());
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("lock")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")) {
                            Event event = EventManager.getActiveEvent();
                            if (event != null) {
                                if (!event.isLockedTeleport()) {
                                    event.setLockedTeleport(true);
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("unlock")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")) {
                            Event event = EventManager.getActiveEvent();
                            if (event != null) {
                                if (event.isLockedTeleport()) {
                                    event.setLockedTeleport(false);
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("tp")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.tp")) {
                            if (EventManager.getActiveEvent() != null) {
                                plsender.teleport(EventManager.getActiveEvent().getTeleport().getLocation());
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
                    else if (args.length == 2) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.tp")) {
                            Event event = EventManager.getEventByName(args[1]);
                            if (event != null) {
                                plsender.teleport(event.getTeleport().getLocation());
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("modify")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.modify")) {
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
                    else if (args.length == 2) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.modify")) {
                            Event chosenEvent = EventManager.getEventByName(args[1]);
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("end")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")) {
                            EventManager.setActiveEvent(null);
                            getInstance().getServer().getOnlinePlayers().forEach(player -> sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-end"), true, player));
                            Map<Player, PlayerData> tempHolder = new HashMap<>(PlayerManager.getJoinedEvent());
                            tempHolder.forEach((p, d) -> PlayerManager.playerLeavingEvent(p));
                            PlayerManager.getJoinedEvent().clear();
                            PlayerManager.getCheckpointed().clear();
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                        }
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("invclear")) {
                    if (args.length == 1) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")) {
                            clearInventory();
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("inventory-cleared"), true, plsender);
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                        }
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("create")) {
                    if (args.length == 2) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.add")) {
                            RabEvent event = new RabEvent(args[1], plsender.getName(), new RabEventLocation(plsender.getLocation()));
                            EventManager.getEvents().put(args[1], event);
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-created")).replace("%event%", args[1]), true, plsender);
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                        }
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 2) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.add")) {
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("start")) {
                    if (args.length == 2) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.start")) {
                            if (EventManager.getActiveEvent() == null) {
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
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-already-started"), true, plsender);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                        }
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("backup")) {
                    if (args.length == 2) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.backup")) {
                            Inventory backup = BackupItemsManager.getBackup(plsender, args[1]);
                            if (backup != null) {
                                plsender.openInventory(backup);
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                        }
                    }
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("kick")) {
                    if (args.length == 2) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.kick")) {
                            Player target = getPlayer(args[1]);
                            Event event = EventManager.getActiveEvent();
                            if (event != null) {
                                if (target != null) {
                                    if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                        if (!target.hasPermission("events.staff")
                                                || !target.hasPermission("events.moderator")) {
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("ban")) {
                    if (args.length == 2) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.ban")) {
                            Player target = getPlayer(args[1]);
                            Event event = EventManager.getActiveEvent();
                            if (event != null) {
                                if (target != null) {
                                    if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                        if (!target.hasPermission("events.staff")
                                                || !target.hasPermission("events.moderator")) {
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
                        wrongCommand(plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("unban")) {
                    if (args.length == 3) {
                        if (plsender.hasPermission("events.staff")
                                || plsender.hasPermission("events.moderator")
                                || plsender.hasPermission("events.ban")) {
                            Event event = EventManager.getEventByName(args[2]);
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
                    else {
                        wrongCommand(plsender);
                    }
                }
                else {
                    wrongCommand(plsender);
                }
            }
        }
        return true;
    }

    //<editor-fold desc="Other Methods">
    private void sendFakeBlocks(Player plsender, Location chp) {
        Location checkpoint = chp.getBlock().getLocation().clone();
        Location checkpointUP = checkpoint.clone().add(0, 1, 0);
        plsender.sendBlockChange(checkpoint, Material.GREEN_WOOL.createBlockData());
        plsender.sendBlockChange(checkpointUP, Material.GREEN_WOOL.createBlockData());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                plsender.sendBlockChange(checkpoint, Material.AIR.createBlockData());
                plsender.sendBlockChange(checkpointUP, Material.AIR.createBlockData());
                debugMessage("Def location: " + checkpoint.toString());
                debugMessage("Chp Location: " + checkpointUP.toString());
                timer.cancel();
            }
        }, 10000);
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

    private void wrongCommand(CommandSender sender) {
        sendLM(getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", true, sender);
    }
    //</editor-fold>
}
