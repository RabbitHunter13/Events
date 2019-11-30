package com.rabbit13.events.commands;

import com.rabbit13.events.events.PlayerJoinContestEvent;
import com.rabbit13.events.events.PlayerLeaveContestEvent;
import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.objects.Event;
import com.rabbit13.events.objects.EventLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.rabbit13.events.main.Misc.debugMessage;
import static com.rabbit13.events.main.Misc.sendLM;

public final class MainExecutor implements CommandExecutor {
    private static List<String> usages;

    public MainExecutor() {
        usages = Main.getInstance().getConfig().getStringList("usages");
    }

    // TODO: 18.11.2019 pvp: TooManyHP, rapidDMG
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sendLM("&cThis can be run just by player", true, sender);
        }
        else {
            Player plsender = (Player) sender;
            if (args.length == 0) {
                if (plsender.hasPermission("events.join")) { //done
                    if (Event.getActiveEvent() != null) {
                        if (!Event.getActiveEvent().getBanned().contains(plsender.getName())) {
                            PlayerJoinContestEvent event = new PlayerJoinContestEvent(plsender.getName(), Event.getActiveEvent().getName());
                            Main.getInstance().getServer().getPluginManager().callEvent(event);
                            if (!event.isCanceled()) {
                                if (!Event.getActiveEvent().isLocked()) {
                                    if (!plsender.hasPermission("event.moderator")) {
                                        Main.getLisMan().getJoinedEvent().put(plsender, plsender.getLocation().clone());
                                    }
                                    plsender.teleport(Objects.requireNonNull(EventLocation.parseBukkitLocation(Event.getActiveEvent().getTeleport())), PlayerTeleportEvent.TeleportCause.COMMAND);
                                    sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("teleport-success"))
                                                    .replace("%event%", Event.getActiveEvent().getName())
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
                            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("teleport-banned")).replace("%event%", Event.getActiveEvent().getName()), true, plsender);
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
                }
                else if (args[0].equalsIgnoreCase("list")) {
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.list")) {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-list"), true, plsender);
                        EventManager.getEvents().forEach((name, event) -> sendLM("&3- " + name, true, plsender));
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("quit")) { //done
                    if (plsender.hasPermission("events.join")) {
                        if (Event.getActiveEvent() != null) {
                            if (Main.getLisMan().getJoinedEvent().containsKey(plsender)) {
                                PlayerLeaveContestEvent event = new PlayerLeaveContestEvent(plsender.getName(),Event.getActiveEvent().getName());
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCanceled()) {
                                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-leave"), true, plsender);
                                    plsender.teleport(Main.getLisMan().getJoinedEvent().get(plsender));
                                    Main.getLisMan().getJoinedEvent().remove(plsender);
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
                        Event event = Event.getActiveEvent();
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
                        Event event = Event.getActiveEvent();
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
                else if (args[0].equalsIgnoreCase("end")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator")) {
                        Event.setActiveEvent(null);
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-end"), true, (CommandSender[]) Main.getInstance().getServer().getOnlinePlayers().toArray());
                        Main.getLisMan().getJoinedEvent().forEach(Entity::teleport);
                        Main.getLisMan().getJoinedEvent().clear();
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("invclear")) {
                    Main.getLisMan().getJoinedEvent().forEach((p, l) -> {
                        p.getInventory().clear();
                        p.getInventory().setHelmet(null);
                        p.getInventory().setChestplate(null);
                        p.getInventory().setLeggings(null);
                        p.getInventory().setBoots(null);
                    });
                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("inventory-cleared"), true, plsender);
                }
                else {
                    sendLM(Main.getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(Main.getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
                }
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.add")) {
                        Event event = new Event(args[1], plsender.getName(), new EventLocation(plsender.getLocation()), null, null);
                        EventManager.getEvents().put(args[1], event);
                        sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-created")).replace("%event%", args[1]), true, plsender);
                    }
                    else {
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission"), true, plsender);
                    }
                }
                else if (args[0].equalsIgnoreCase("remove")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.add")) {
                        if (EventManager.getEventByName(args[1]) != null) {
                            EventManager.getEvents().remove(args[1]);
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
                else if (args[0].equalsIgnoreCase("activate")) { //done
                    if (plsender.hasPermission("events.staff") || plsender.hasPermission("events.moderator") || plsender.hasPermission("events.activate")) {
                        Event.setActiveEvent(args[1]);
                        if (Event.getActiveEvent() == null) {
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-not-found"), true, plsender);
                        }
                        else {
                            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-activated"))
                                            .replace("%event%", Event.getActiveEvent().getName())
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
                        Event event = Event.getActiveEvent();
                        if (event != null) {
                            if (target != null) {
                                if (Main.getLisMan().getJoinedEvent().containsKey(target)) {
                                    if (!target.hasPermission("events.staff") || target.hasPermission("events.moderator")) {
                                        sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("player-kicked-admin-side"))
                                                        .replace("%player%", args[1])
                                                        .replace("%event%", event.getName())
                                                , true
                                                , plsender);
                                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("player-kicked-player-side"), true, target);
                                        target.teleport(Main.getLisMan().getJoinedEvent().get(target));
                                        Main.getLisMan().getJoinedEvent().remove(target);
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
                        Event event = Event.getActiveEvent();
                        if (event != null) {
                            if (target != null) {
                                if (Main.getLisMan().getJoinedEvent().containsKey(target)) {
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
                                        target.teleport(Main.getLisMan().getJoinedEvent().get(target));
                                        Main.getLisMan().getJoinedEvent().remove(target);
                                        //banned
                                        List<String> banned = Event.getActiveEvent().getBanned();
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
                else if (args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("b")) {
                    if (plsender.hasPermission("events.moderator") || plsender.hasPermission("events.broadcast")) {
                        if (Event.getActiveEvent() != null) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Main.getFilMan().getWords().getString("event-broadcast"))
                                    .replace("%event%", Event.getActiveEvent().getName())
                                    .replace("%time%", args[1])));
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
                        Event event = EventManager.getEventByName(args[2]);
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
                sendLM(Main.getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(Main.getInstance().getCommand("event")).getUsage() + " for help.", true, plsender);
            }
        }
        return true;
    }
}