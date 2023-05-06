package com.rabbit13.events.commands;

import com.rabbit13.events.events.PlayerJoinContestEvent;
import com.rabbit13.events.events.PlayerLeaveContestEvent;
import com.rabbit13.events.events.RabPlayerJoinContestEvent;
import com.rabbit13.events.events.RabPlayerLeaveContestEvent;
import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.BackupManager;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.PlayerManager;
import com.rabbit13.events.objects.*;
import com.rabbit13.events.objects.event.Event;
import com.rabbit13.events.objects.event.RabEvent;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.rabbit13.events.main.Main.*;
import static com.rabbit13.events.main.Misc.*;
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
        boolean isPlayer = sender instanceof Player;
        if (args.length == 0) {
            if (isPlayer) {
                Player plsender = (Player) sender;
                if (plsender.hasPermission("events.staff")
                        || plsender.hasPermission("events.join")) {
                    if (EventManager.getActiveEvent() != null) {
                        if (!EventManager.getActiveEvent().isLockedTeleport()) {
                            if (!EventManager.getActiveEvent().getBanned().contains(plsender.getName())) {
                                PlayerData data = new RabPlayerData(plsender.getInventory().getContents().clone(),
                                                                    plsender.getEnderChest().getContents().clone(),
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
                                        plsender.teleport(EventManager.getActiveEvent().getTeleport(), PlayerTeleportEvent.TeleportCause.COMMAND);
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
                        debugMessage("FilMan: " + (getFilMan() != null));
                        debugMessage("Words: " + (getFilMan().getWords() != null));
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), true, plsender);
                    }
                }
                else {
                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                }
            }
            else {
                sendLM(getPluginPrefix() + " This command have to be executed as player!", false, sender);
                return true;
            }
        }
        else {
            switch (args[0]) {
                case "info": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            sendLM("&6" + getPrefix() + " &eCreated by Rabbit_Hunter13", true, plsender);
                            sendLM("&3Version &b" + getPdf().getVersion(), true, plsender);
                        }
                        else {
                            wrongCommand(plsender);
                        }
                    }
                    else {
                        if (args.length == 1) {
                            sendLM("&6" + getPrefix() + " &eCreated by Rabbit_Hunter13", false, sender);
                            sendLM("&3Version &b" + getPdf().getVersion(), false, sender);
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "debug": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            if (plsender.hasPermission("events.staff")) {
                                sendLM(getPrefix() + " All maps and lists in plugin:", true, plsender);
                                //<editor-fold desc="Plsender Debug">
                                //<editor-fold desc="Events">
                                sendLM("&c## &6Events &c##", true, plsender);
                                EventManager.getEvents().forEach((s, e) -> sendLM(
                                        "&6Name: &e" + s + "&f, &6Owner: &e" + e.getOwner(),
                                        true, plsender));
                                //</editor-fold>
                                //<editor-fold desc="Players Joined">
                                sendLM("&c## &6Players Joined event &c##", true, plsender);
                                PlayerManager.getJoinedEvent().forEach((p, d) -> sendLM(
                                        "&6Name: &e" + p.getName() + "&f, &6Data: &e" + d.toString(),
                                        true, plsender));
                                //</editor-fold>
                                //<editor-fold desc="Players Checkpointed">
                                sendLM("&c## &6Players checkpointed &c##", true, plsender);
                                PlayerManager.getCheckpointed().forEach((p, c) -> sendLM(
                                        "&6Name: &e" + p.getName() + "&f, &6Checkpoint: &e" + c.toString(),
                                        true, plsender)
                                );
                                //</editor-fold>
                                //<editor-fold desc="Admins modifying events">
                                sendLM("&c## &6Players modifying &c##", true, plsender);
                                PlayerManager.getModifyingEvent().forEach((p, entry) -> sendLM(
                                        "&6Name: &e" + p.getName() + "&f, &6Modifying Event &e" + entry.getValue().getName() + " &7Slot: &8" + entry.getKey(),
                                        true, plsender));
                                PlayerManager.getModifyingMods().forEach((p, entry) -> sendLM(
                                        "&6Name: &e" + p.getName() + "&f, &6Modifying Mods &e" + " &7Slot: &8" + entry.getKey(),
                                        true, plsender));
                                //</editor-fold>
                                //<editor-fold desc="Player Backups">
                                sendLM("&c## &6Player backups &c##", true, plsender);
                                BackupManager.getBackups().forEach((p, b) -> sendLM("&6Name: " + p, true, plsender));
                                //</editor-fold>
                                //</editor-fold>
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, sender);
                            }
                        }
                        else {
                            wrongCommand(plsender);
                        }
                    }
                    else {
                        if (args.length == 1) {

                            sendLM(getPrefix() + " All maps and lists in plugin:", false, sender);
                            //<editor-fold desc="Sender Debug">
                            //<editor-fold desc="Events">
                            sendLM("&c## &6Events &c##", false, sender);
                            EventManager.getEvents().forEach((s, e) -> sendLM(
                                    "&6Name: &e" + s + "&f, &6Owner: &e" + e.getOwner(),
                                    false, sender));
                            //</editor-fold>
                            //<editor-fold desc="Players Joined">
                            sendLM("&c## &6Players Joined event &c##", false, sender);
                            PlayerManager.getJoinedEvent().forEach((p, d) -> sendLM(
                                    "&6Name: &e" + p.getName() + "&f, &6Data: &e" + d.toString(),
                                    false, sender));
                            //</editor-fold>
                            //<editor-fold desc="Players Checkpointed">
                            sendLM("&c## &6Players checkpointed &c##", false, sender);
                            PlayerManager.getCheckpointed().forEach((p, c) -> sendLM(
                                    "&6Name: &e" + p.getName() + "&f, &6Checkpoint: &e" + c.toString(),
                                    false, sender)
                            );
                            //</editor-fold>
                            //<editor-fold desc="Admins modifying events">
                            sendLM("&c## &6Players modifying &c##", false, sender);
                            PlayerManager.getModifyingEvent().forEach((p, entry) -> sendLM(
                                    "&6Name: &e" + p.getName() + "&f, &6Modifying Event &e" + entry.getValue().getName() + " &7Slot: &8" + entry.getKey(),
                                    false, sender));
                            PlayerManager.getModifyingMods().forEach((p, entry) -> sendLM(
                                    "&6Name: &e" + p.getName() + "&f, &6Modifying Mods &e" + " &7Slot: &8" + entry.getKey(),
                                    true, sender));
                            //</editor-fold>
                            //<editor-fold desc="Player backups">
                            sendLM("&c## &6Player backups &c##", false, sender);
                            BackupManager.getBackups().forEach((p, b) -> sendLM("&6Name: " + p, false, sender));
                            //</editor-fold>
                            //</editor-fold>
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "b": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
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
                    else {
                        if (args.length > 1) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', buildStringFromArgs(args)));
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "help": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
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
                    else {
                        if (args.length == 1) {
                            //list of usages
                            sendLM(getPrefix() + " " + getPdf().getName() + " Usages:", false, sender);
                            usages.forEach(usage -> sendLM("&3" + usage, false, sender));
                            adminUsages.forEach(usage -> sendLM("&3" + usage, false, sender));
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "list": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.list")) {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-official"), true, plsender);
                                List<Event> otherEvents = new ArrayList<>();
                                EventManager.getEvents().forEach((name, event) -> {
                                    assert event.getTeleport().getWorld() != null;
                                    if (event.getTeleport().getWorld().getName().equalsIgnoreCase(getInstance().getConfig().getString("event-world"))) {
                                        sendLM("&3• " + name, true, plsender);
                                    }
                                    else {
                                        otherEvents.add(event);
                                    }
                                });
                                if (!otherEvents.isEmpty()) {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-other"), true, plsender);
                                    otherEvents.forEach(event -> {
                                        assert event.getTeleport().getWorld() != null;
                                        sendLM("&3[" + event.getTeleport().getWorld().getName() + "] &3• " + event.getName(), true, plsender);
                                    });
                                }
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                            }
                        }
                        else if (args.length == 2) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.list")) {
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-list-owner"))
                                               .replace("%owner%", args[1]),
                                       true,
                                       plsender);
                                for (Map.Entry<String, Event> entry : EventManager.getEvents().entrySet()) {
                                    if (entry.getValue().getOwner().equals(args[1])) {
                                        sendLM("&3• " + entry.getKey(), true, plsender);
                                    }
                                }
                            }
                        }
                        else {
                            wrongCommand(plsender);
                        }
                    }
                    else {
                        if (args.length == 1) {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-official"), false, sender);
                            List<Event> otherEvents = new ArrayList<>();
                            if (!EventManager.getEvents().isEmpty()) {
                                EventManager.getEvents().forEach((name, event) -> {
                                    assert event.getTeleport().getWorld() != null;
                                    if (event.getTeleport().getWorld().getName().equalsIgnoreCase(getInstance().getConfig().getString("event-world"))) {
                                        sendLM("&3• " + name, false, sender);
                                    }
                                    else {
                                        otherEvents.add(event);
                                    }
                                });
                            }
                            if (!otherEvents.isEmpty()) {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-list-other"), false, sender);
                                otherEvents.forEach(event -> {
                                    assert event.getTeleport().getWorld() != null;
                                    sendLM("&3[" + event.getTeleport().getWorld().getName() + "] &3• " + event.getName(), false, sender);
                                });
                            }
                        }
                        else if (args.length == 2) {
                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-list-owner"))
                                           .replace("%owner%", args[1]),
                                   false,
                                   sender);
                            for (Map.Entry<String, Event> entry : EventManager.getEvents().entrySet()) {
                                if (entry.getValue().getOwner().equals(args[1])) {
                                    sendLM("&3• " + entry.getKey(), false, sender);
                                }
                            }
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "win": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        switch (args.length) {
                            case 1: {
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
                                break;
                            }
                            case 2: {
                                if (args[1].equals("list")) {
                                    if (plsender.hasPermission("events.staff")
                                            || plsender.hasPermission("events.moderator")
                                            || plsender.hasPermission("events.win.list")) {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("win-counter-list"), true, plsender);
                                        PlayerManager.getTopWinners().forEach((name, number) -> sendLM("&3• " + name + ": " + number, true, plsender));
                                    }
                                    else {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                                    }
                                }
                                else if (args[1].equals(plsender.getName())
                                        || args[1].equals("me")) {
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
                                break;
                            }
                            case 3: {
                                if (args[1].equals("add")) {
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
                                    wrongCommand(plsender);
                                }
                                break;
                            }
                            default:
                                wrongCommand(plsender);
                                break;
                        }
                    }
                    break;
                }
                case "checkpoint": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 2) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.checkpoint.set")) {
                                if (EventManager.getActiveEvent() != null) {
                                    if (plsender.getWorld().equals(EventManager.getActiveEvent().getTeleport().getWorld())) {
                                        switch (args[1]) {
                                            case "set": {
                                                if (plsender.getWorld().equals(EventManager.getActiveEvent().getTeleport().getWorld())) {
                                                    EventManager.getActiveEvent().getCheckpoints().add(plsender.getLocation().getBlock().getLocation());
                                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-added"))
                                                                   .replace("%location%", "[" + plsender.getLocation().getBlockX()
                                                                           + "," + plsender.getLocation().getBlockY()
                                                                           + "," + plsender.getLocation().getBlockZ() + "]")
                                                            , true, plsender);
                                                }
                                                break;
                                            }
                                            case "remove": {
                                                if (EventManager.getActiveEvent().getCheckpoints().remove(plsender.getLocation().getBlock().getLocation()))
                                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-removed"))
                                                            .replace("%location%", ("[X: " + plsender.getLocation().getBlockX() +
                                                                    "Y: " + plsender.getLocation().getBlockY() +
                                                                    "Z: " + plsender.getLocation().getBlockZ() + "]")), true, plsender);
                                                else
                                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-not-found"), true, plsender);
                                                break;
                                            }
                                            case "removeall": {
                                                EventManager.getActiveEvent().getCheckpoints().clear();
                                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed-all"), true, plsender);
                                                break;
                                            }
                                            case "view": {
                                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-show"), true, plsender);
                                                for (Location chp : EventManager.getActiveEvent().getCheckpoints()) {
                                                    sendFakeBlocks(plsender, chp);
                                                }
                                                break;
                                            }
                                            case "list": {
                                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-list"))
                                                               .replace("%event%", EventManager.getActiveEvent().getName())
                                                        , true, plsender);
                                                EventManager.getActiveEvent().getCheckpoints().forEach(chp -> sendLM("&3• "
                                                                                                                             + "[" + chp.getBlockX()
                                                                                                                             + ", " + chp.getBlockY()
                                                                                                                             + ", " + chp.getBlockZ()
                                                                                                                             + "]"
                                                        , true, plsender)
                                                );
                                                break;
                                            }
                                            default: {
                                                sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", true, sender);
                                                break;
                                            }
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
                                    if (plsender.getWorld().equals(event.getTeleport().getWorld())) {
                                        switch (args[1]) {
                                            case "set": {
                                                if (plsender.getWorld().equals(event.getTeleport().getWorld())) {
                                                    event.getCheckpoints().add(plsender.getLocation().getBlock().getLocation());
                                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-added"))
                                                                   .replace("%location%", "[" + plsender.getLocation().getBlockX()
                                                                           + "," + plsender.getLocation().getBlockY()
                                                                           + "," + plsender.getLocation().getBlockZ() + "]")
                                                            , true, plsender);
                                                }
                                                break;
                                            }
                                            case "remove": {
                                                if (event.getCheckpoints().remove(plsender.getLocation().getBlock().getLocation()))
                                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed"), true, plsender);
                                                else
                                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-not-found"), true, plsender);
                                                break;
                                            }
                                            case "removeall": {
                                                event.getCheckpoints().clear();
                                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed-all"), true, plsender);
                                                break;
                                            }
                                            case "view": {
                                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-show"), true, plsender);
                                                for (Location chp : event.getCheckpoints()) {
                                                    sendFakeBlocks(plsender, chp);
                                                }
                                                break;
                                            }
                                            case "list": {
                                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-list"))
                                                               .replace("%event%", event.getName())
                                                        , true, plsender);
                                                event.getCheckpoints().forEach(chp -> sendLM("&3• "
                                                                                                     + "[" + chp.getBlockX()
                                                                                                     + ", " + chp.getBlockY()
                                                                                                     + ", " + chp.getBlockZ()
                                                                                                     + "]"
                                                        , true, plsender)
                                                );
                                                break;
                                            }
                                            default: {
                                                sendLM(getPluginPrefix() + " Wrong command, type " + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", true, sender);
                                                break;
                                            }
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
                    else {
                        if (args.length == 2) {
                            if (EventManager.getActiveEvent() != null) {
                                if (args[1].equals("removeall")) {
                                    EventManager.getActiveEvent().getCheckpoints().clear();
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed-all"), false, sender);
                                }
                                else if (args[1].equals("list")) {
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-list"))
                                                   .replace("%event%", EventManager.getActiveEvent().getName())
                                            , false, sender);
                                    EventManager.getActiveEvent().getCheckpoints().forEach(chp -> sendLM("&3• "
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
                        else if (args.length == 3) {
                            Event event = EventManager.getEventByName(args[2]);
                            if (event != null) {
                                if (args[1].equals("removeall")) {
                                    event.getCheckpoints().clear();
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-removed-all"), false, sender);
                                }
                                else if (args[1].equals("list")) {

                                    if (EventManager.getActiveEvent() != null) {
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("checkpoint-list"))
                                                       .replace("%event%", event.getName())
                                                , false, sender);
                                        EventManager.getActiveEvent().getCheckpoints().forEach(chp -> sendLM("&3• "
                                                                                                                     + "[" + chp.getBlockX()
                                                                                                                     + ", " + chp.getBlockY()
                                                                                                                     + ", " + chp.getBlockZ()
                                                                                                                     + "]"
                                                , false, sender)
                                        );
                                    }
                                    else {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("active-event-not-found"), false, sender);
                                    }
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
                    }
                    break;
                }
                case "quit": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            if (plsender.hasPermission("events.join")) {
                                if (EventManager.getActiveEvent() != null) {
                                    if (PlayerManager.getJoinedEvent().containsKey(plsender)) {
                                        PlayerLeaveContestEvent event = new RabPlayerLeaveContestEvent(plsender.getName(), EventManager.getActiveEvent());
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCanceled()) {
                                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-leave"), true, plsender);
                                            PlayerManager.playerLeavingEvent(plsender, null, false);
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
                    break;
                }
                case "lock": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
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
                    else {
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
                    }
                    break;
                }
                case "unlock": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
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
                    else {
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
                    }
                    break;
                }
                case "tp": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.tp")) {
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
                        else if (args.length == 2) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.tp")) {
                                Event event = EventManager.getEventByName(args[1]);
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
                        else {
                            wrongCommand(plsender);
                        }
                    }
                    break;
                }
                case "modify": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
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
                    break;
                }
                case "end": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")) {
                                endEvent();
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
                    else {
                        if (args.length == 1) {
                            endEvent();
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "give": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 2) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.give")) {
                                for (Map.Entry<Player, PlayerData> entry : PlayerManager.getJoinedEvent().entrySet()) {
                                    Player player = entry.getKey();
                                    Material material = Material.matchMaterial(args[1]);
                                    if (material != null) {
                                        player.getInventory().addItem(getSpecifiedItem(material, 1, null));
                                    }
                                    else {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("wrong-material"), true, plsender);
                                        break;
                                    }
                                }
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("items-given"))
                                               .replace("%material%", args[1])
                                               .replace("%amount%", "1"),
                                       true, plsender);
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                            }
                        }
                        else if (args.length == 3) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.give")) {
                                for (Map.Entry<Player, PlayerData> entry : PlayerManager.getJoinedEvent().entrySet()) {
                                    Player player = entry.getKey();
                                    Material material = Material.matchMaterial(args[1]);
                                    int amount = 1;
                                    try {
                                        amount = Integer.parseInt(args[2]);
                                        if (amount < 1) {
                                            amount = 1;
                                        }
                                        else if (amount > 64) {
                                            amount = 64;
                                        }
                                    } catch (NumberFormatException ignored) {
                                    }
                                    if (material != null) {
                                        player.getInventory().addItem(getSpecifiedItem(material, amount, null));
                                    }
                                    else {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("wrong-material"), true, plsender);
                                        break;
                                    }
                                }
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("items-given"))
                                               .replace("%material%", args[1])
                                               .replace("%amount%", args[2]),
                                       true, plsender);
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                            }
                        }
                        else {
                            wrongCommand(plsender);
                        }
                    }
                    break;
                }
                case "clearinv": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")) {
                                clearInventories();
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
                    else {
                        if (args.length == 1) {
                            clearInventories();
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("inventory-cleared"), false, sender);
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "addeff": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 2) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.addeff")) {
                                PotionEffectType effectType = PotionEffectType.getByName(args[1]);
                                int amplifier = 0;
                                if (effectType != null) {
                                    for (Map.Entry<Player, PlayerData> entry : PlayerManager.getJoinedEvent().entrySet()) {
                                        Player p = entry.getKey();
                                        Bukkit.getScheduler().runTask(getInstance(), () -> p.addPotionEffect(new PotionEffect(effectType,
                                                                                                                              Integer.MAX_VALUE,
                                                                                                                              amplifier)));
                                    }
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("effect-given"))
                                                   .replace("%effect%", effectType.getName()),
                                           true, plsender);
                                }
                            }
                        }
                        else if (args.length == 3) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.addeff")) {
                                PotionEffectType effectType = PotionEffectType.getByName(args[1]);
                                int amplifier = 0;
                                try {
                                    amplifier = Integer.parseInt(args[2]);
                                    if (amplifier < 0) {
                                        amplifier = 0;
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                                if (effectType != null) {
                                    int finalAmplifier = amplifier;
                                    for (Map.Entry<Player, PlayerData> entry : PlayerManager.getJoinedEvent().entrySet()) {
                                        Player p = entry.getKey();
                                        Bukkit.getScheduler().runTask(getInstance(), () -> p.addPotionEffect(new PotionEffect(effectType,
                                                                                                                              Integer.MAX_VALUE,
                                                                                                                              finalAmplifier)));
                                    }
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("effect-given"))
                                                   .replace("%effect%", effectType.getName()),
                                           true, plsender);
                                }
                                else {
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("wrong-effect"))
                                            .replace("%effect%", args[1]), true, plsender);
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
                    break;
                }
                case "cleareff": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.cleareff")) {
                                clearEffects();
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("effect-cleared"), true, plsender);
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
                        if (args.length == 1) {
                            clearEffects();
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("effects-cleared"), false, sender);
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "sethealth": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 2) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.sethealth")) {
                                for (Map.Entry<Player, PlayerData> entry : PlayerManager.getJoinedEvent().entrySet()) {
                                    Player p = entry.getKey();
                                    try {
                                        double health = Double.parseDouble(args[1]);
                                        if (health < 1) {
                                            health = 1;
                                        }
                                        Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(health);
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("health-changed"))
                                                       .replace("%health%", Double.toString(health)),
                                               true, plsender);
                                    } catch (NumberFormatException e) {
                                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-modification-number-error"), true, plsender);
                                        break;
                                    }
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
                    break;
                }
                case "create": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 2) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.add")) {
                                if (!EventManager.getEvents().containsKey(args[1])) {
                                    RabEvent event = new RabEvent(args[1], plsender.getName(), plsender.getLocation());
                                    EventManager.getEvents().put(args[1], event);
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-created")).replace("%event%", args[1]), true, plsender);
                                }
                                else {
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-already-exists"), true, plsender);
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
                    break;
                }
                case "remove": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
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
                    else {
                        if (args.length == 2) {
                            if (EventManager.getEventByName(args[1]) != null) {
                                EventManager.getEvents().remove(args[1]);
                                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-deleted"))
                                        .replace("%event%", args[1]), false, sender);
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-not-found"), false, sender);
                            }
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "start": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
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
                    else {
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
                    }
                    break;
                }
                case "backup": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 2) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.backup.reload")) {
                                if (args[1].equals("reload")) {
                                    BackupManager.initializeBackups();
                                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("backups-reloaded"), true, plsender);
                                }
                                else {
                                    wrongCommand(plsender);
                                }
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission"), true, plsender);
                            }
                        }
                        else if (args.length == 3) {
                            if (plsender.hasPermission("events.staff")
                                    || plsender.hasPermission("events.moderator")
                                    || plsender.hasPermission("events.backup")) {
                                debugMessage("args: " + Arrays.toString(args));
                                if (args[1].equals("get")) {
                                    Backup backup = BackupManager.getBackups().get(args[2]);
                                    if (backup != null) {
                                        plsender.openInventory(backup.getHolder().getInventory());
                                    }
                                    else {
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("backup-not-found"))
                                                       .replace("%name%", args[2]),
                                               true, plsender);
                                    }
                                }
                                else {
                                    wrongCommand(plsender);
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
                    break;
                }
                case "kick": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
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
                                                PlayerManager.playerLeavingEvent(target, null, false);
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
                    else {
                        if (args.length == 2) {
                            Player target = getPlayer(args[1]);
                            Event event = EventManager.getActiveEvent();
                            if (event != null) {
                                if (target != null) {
                                    if (PlayerManager.getJoinedEvent().containsKey(target)) {
                                        if (!target.hasPermission("events.staff")
                                                || target.hasPermission("events.moderator")) {
                                            PlayerManager.playerLeavingEvent(target, null, false);
                                            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("player-kicked-admin-side"))
                                                           .replace("%player%", args[1])
                                                           .replace("%event%", event.getName())
                                                    , false
                                                    , sender);
                                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-kicked-player-side"), false, target);
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
                    }
                    break;
                }
                case "ban": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
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
                                                PlayerManager.playerLeavingEvent(target, null, false);
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
                    else {
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
                                            PlayerManager.playerLeavingEvent(target, null, false);
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
                    }
                    break;
                }
                case "unban": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
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
                                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("banned-player-not-found"))
                                                .replace("%event%", event.getName()), true, plsender);
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
                    }
                    break;
                }
                case "reload": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        if (args.length == 1) {
                            if (plsender.hasPermission("events.reload")) {
                                reload(plsender);
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
                        if (args.length == 1) {
                            reload(sender);
                        }
                        else {
                            wrongCommand(sender);
                        }
                    }
                    break;
                }
                case "generateitem": {
                    if (isPlayer) {
                        Player plsender = (Player) sender;
                        switch (args.length) {
                            case 1: {
                                if (plsender.hasPermission("events.staff")
                                        || plsender.hasPermission("events.moderator")
                                        || plsender.hasPermission("events.generateitem")) {
                                    ItemStack generatedItem = RabItemGenerator.generateItem(ItemRarity.RANDOM, true);
                                    plsender.getInventory().addItem(generatedItem);
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("item-generated"))
                                                   .replace("%material%", generatedItem.getType().toString().replace("_", " ").toLowerCase())
                                                   .replace("%rarity%", ChatColor.stripColor(generatedItem.getItemMeta().getLore().get(0))
                                                           .replaceAll("&.", "").substring(8)),
                                           true,
                                           plsender);
                                }
                                break;
                            }
                            case 2: {
                                if (plsender.hasPermission("events.staff")
                                        || plsender.hasPermission("events.moderator")
                                        || plsender.hasPermission("events.generateitem")) {
                                    ItemStack generatedItem = RabItemGenerator.generateItem(ItemRarity.valueOf(args[1].toUpperCase()), false);
                                    plsender.getInventory().addItem(generatedItem);
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("item-generated"))
                                                   .replace("%material%", generatedItem.getType().toString().replace("_", " ").toLowerCase())
                                                   .replace("%rarity%", ChatColor.stripColor(generatedItem.getItemMeta().getLore().get(0))
                                                           .replaceAll("&.", "").substring(8)),
                                           true,
                                           plsender);
                                }
                                break;
                            }
                            case 3: {
                                if (plsender.hasPermission("events.staff")
                                        || plsender.hasPermission("events.moderator")
                                        || plsender.hasPermission("events.generateitem")) {
                                    ItemStack generatedItem = RabItemGenerator.generateItem(ItemRarity.valueOf(args[1].toUpperCase()), Boolean.parseBoolean(args[2]));
                                    plsender.getInventory().addItem(generatedItem);
                                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("item-generated"))
                                                   .replace("%material%", generatedItem.getType().toString().replace("_", " ").toLowerCase())
                                                   .replace("%rarity%", ChatColor.stripColor(generatedItem.getItemMeta().getLore().get(0))
                                                           .replaceAll("&.", "").substring(8)),
                                           true,
                                           plsender);
                                }
                                break;
                            }
                            default:
                                wrongCommand(sender);
                                break;
                        }
                    }
                    break;
                }
                default: {
                    wrongCommand(sender);
                    break;
                }
            }
        }
        return true;
    }

    //<editor-fold desc="Other Methods">
    private void endEvent() {
        getInstance().getServer().getOnlinePlayers().forEach(player -> sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-end"), true, player));
        Map<Player, PlayerData> tempHolder = new HashMap<>(PlayerManager.getJoinedEvent());
        tempHolder.forEach((p, d) -> PlayerManager.playerLeavingEvent(p, null, false));
        PlayerManager.getJoinedEvent().clear();
        EventManager.setActiveEvent(null);
    }

    private void sendFakeBlocks(Player plsender, Location chp) {
        Location checkpoint = chp.getBlock().getLocation().clone();
        Location checkpointUP = checkpoint.clone().add(0, 1, 0);
        plsender.sendBlockChange(checkpoint, Material.GREEN_WOOL.createBlockData());
        plsender.sendBlockChange(checkpointUP, Material.GREEN_WOOL.createBlockData());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                plsender.sendBlockChange(checkpoint, checkpoint.getBlock().getBlockData());
                plsender.sendBlockChange(checkpointUP, checkpointUP.getBlock().getBlockData());
                debugMessage("Def location: " + checkpoint);
                debugMessage("Chp Location: " + checkpointUP);
                timer.cancel();
            }
        }, 10000);
    }

    private void clearInventories() {
        PlayerManager.getJoinedEvent().forEach((p, l) -> {
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);
        });
    }

    private void clearEffects() {
        PlayerManager.getJoinedEvent().forEach((player, value) -> {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
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
        return (prefix != null) ? prefix + message : getPrefix() + message;
    }

    private void reload(CommandSender sender) {
        boolean isPlayer = sender instanceof Player;
        //config
        getInstance().reloadConfig();
        Main.setPrefix(getInstance().getConfig().getString("prefix", "&4[&cEvents&4]&f"));
        Main.setPluginPrefix(getInstance().getConfig().getString("plugin-prefix", "[Events]"));
        Main.setDebugMode(getInstance().getConfig().getBoolean("debug"));
        //events
        getFilMan().saveEvents();
        getFilMan().saveCounter();
        getFilMan().loadEvents();
        getFilMan().loadCounter();
        //lang
        getFilMan().setWords(YamlConfiguration.loadConfiguration(getFilMan().getLangFile()).getConfigurationSection(Objects.requireNonNull(getInstance().getConfig().getString("lang"))));
        sendLM(getPrefix() + " " + getFilMan().getWords().getString("config-reloaded"), isPlayer, sender);
    }

    private void wrongCommand(CommandSender sender) {
        boolean isPlayer = sender instanceof Player;
        sendLM(getPrefix() + " &6Wrong command, type &e" + Objects.requireNonNull(getInstance().getCommand("event")).getUsage() + " for help.", isPlayer, sender);
    }
    //</editor-fold>
}
