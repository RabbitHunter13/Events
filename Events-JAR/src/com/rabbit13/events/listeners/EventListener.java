package com.rabbit13.events.listeners;

import com.rabbit13.events.events.ePlayerDeathAtContestEvent;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.PlayerManager;
import com.rabbit13.events.objects.EventMods;
import com.rabbit13.events.objects.eEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static com.rabbit13.events.main.Main.getFilMan;
import static com.rabbit13.events.main.Main.getPrefix;
import static com.rabbit13.events.main.Misc.debugMessage;
import static com.rabbit13.events.main.Misc.sendLM;

public final class EventListener implements Listener {

    /**
     * modifying events handler
     */
    @EventHandler
    public void onInventoryItemPickup(InventoryClickEvent e) {
        for (Map.Entry<String, eEvent> entry : EventManager.getEvents().entrySet()) {
            eEvent event = entry.getValue();
            if (e.getWhoClicked().getOpenInventory().getTopInventory().equals(event.getInventory())) {
                event.modificator = e.getSlot();
                ItemStack item = event.getInventory().getItem(e.getSlot());
                if (item != null) {
                    if (e.getSlot() == 0 || e.getSlot() == 1) {
                        debugMessage("editing string values");
                        PlayerManager.getModifyingEvent().put((Player) e.getWhoClicked(), event);
                        e.getWhoClicked().closeInventory();
                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-init"))
                                       .replace("%value%", (e.getSlot() == 0) ? "Name" : "Owner")
                                , true, e.getWhoClicked());
                    }
                    else {
                        debugMessage("editing someting else than string values, no chat listener needed");
                        event.updateItems(event.modificator, (Player) e.getWhoClicked());
                    }
                }
                debugMessage("Clicked item: " + ((item != null) ? item.getType().toString() : "Null"));
                e.setCancelled(true);
                break;

            }
            else if (e.getWhoClicked().getOpenInventory().getTopInventory().equals(event.getMods().getInventory())) {
                //Mods inventory
                EventMods mods = event.getMods();
                ItemStack item = mods.getInventory().getItem(e.getSlot());
                if (item != null) {
                    mods.updateItems(e.getSlot(), (Player) e.getWhoClicked());
                }
                debugMessage("Clicked item: " + ((item != null) ? item.getType().toString() : "Null"));
                e.setCancelled(true);
                break;
            }
        }
    }

    /**
     * remove player from event if disconnected
     */
    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer()))
            PlayerManager.playerLeavingEvent(e.getPlayer());
        PlayerManager.getModifyingEvent().remove(e.getPlayer());
    }

    /**
     * all commands are prohibited, excluding /e quit (for players)
     */
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getPlayer().hasPermission("events.staff") || !e.getPlayer().hasPermission("events.moderator")) {
            String[] args = e.getMessage().split(" ");
            debugMessage("Arguments: " + Arrays.toString(args));
            //commands here are permitted, or sub-permitted
            if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
                if (!args[0].equalsIgnoreCase("/e") && !args[0].equalsIgnoreCase("/event")) {
                    e.setCancelled(true);
                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission-commands"), true, e.getPlayer());
                }
                else {
                    if (args.length > 1) {
                        String argument = args[1];
                        debugMessage("Argument 1: " + argument);
                        if (!argument.equalsIgnoreCase("quit") && !argument.equalsIgnoreCase("checkpoint")) {
                            e.setCancelled(true);
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission-commands"), true, e.getPlayer());
                        }
                    }
                }
            }
        }
    }

    /**
     * Monitoring Chat if someone modifying string values
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void chooseValueChatEvent(AsyncPlayerChatEvent e) {
        if (PlayerManager.getModifyingEvent().containsKey(e.getPlayer())) {
            eEvent event = PlayerManager.getModifyingEvent().remove(e.getPlayer());
            e.setCancelled(true);
            if (event.modificator == 0) {
                EventManager.getEvents().remove(event.getName());
                event.updateItems(event.modificator, e.getMessage(), e.getPlayer());
                EventManager.getEvents().put(event.getName(), event);
            }
            else {
                event.updateItems(event.modificator, e.getMessage(), e.getPlayer());
            }
        }
    }

    /**
     * Call death at contest event
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getEntity())) {
            ePlayerDeathAtContestEvent event = new ePlayerDeathAtContestEvent(e.getEntity().getName(), EventManager.getActiveEvent());
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    /**
     * After player respawn, it would check if he is still at event, if it is, then te respawn point will be at event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
            if (PlayerManager.getCheckpointed().containsKey(e.getPlayer())) {
                e.setRespawnLocation(PlayerManager.getCheckpointed().get(e.getPlayer()));
            }
            else {
                e.setRespawnLocation(EventManager.getActiveEvent().getTeleport());
            }
        }
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
            if (EventManager.getActiveEvent().getCheckpoints().contains(e.getTo().getBlock().getLocation())) {
                if (!PlayerManager.getCheckpointed().containsKey(e.getPlayer())) {
                    PlayerManager.getCheckpointed().put(e.getPlayer(), e.getTo());
                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-reached"), true, e.getPlayer());
                }
            }
        }
    }
}
