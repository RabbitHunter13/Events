package com.rabbit13.events.listeners;

import com.rabbit13.events.events.RabPlayerDeathAtContestEvent;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.PlayerManager;
import com.rabbit13.events.objects.event.Event;
import com.rabbit13.events.objects.event.mods.EventMods;
import com.rabbit13.events.objects.event.tools.RabCheckpointLocation;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.AbstractMap;
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
        for (Map.Entry<String, Event> entry : EventManager.getEvents().entrySet()) {
            Event event = entry.getValue();
            EventMods mods = event.getMods();
            //<editor-fold desc="Events">
            if (e.getWhoClicked().getOpenInventory().getTopInventory() == event.getInventory()) {
                if (e.getClickedInventory() == event.getInventory()) {
                    if (e.getSlot() == 0 || e.getSlot() == 1) {
                        debugMessage("editing string values");
                        PlayerManager.getModifyingEvent().put((Player) e.getWhoClicked(), new AbstractMap.SimpleEntry<>(e.getSlot(), event));
                        e.getWhoClicked().closeInventory();
                        sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-init"))
                                       .replace("%value%", (e.getSlot() == 0) ? "Name" : "Owner"),
                               true,
                               e.getWhoClicked());
                    }
                    else {
                        debugMessage("editing someting else than string values, no chat listener needed");
                        event.updateItems(e.getSlot(), (Player) e.getWhoClicked());
                    }
                    debugMessage("Clicked slot: " + e.getSlot());
                    debugMessage("Clicked type: " + e.getAction().toString());
                    e.setCancelled(true);
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="Mods">
            if (e.getWhoClicked().getOpenInventory().getTopInventory() == event.getMods().getInventory()) {
                if (e.getClickedInventory() == event.getMods().getInventory()) {
                    //Mods inventory
                    mods.updateItems(e.getSlot(), (Player) e.getWhoClicked(), e.getAction());
                    debugMessage("Clicked slot: " + e.getSlot());
                    debugMessage("Clicked type: " + e.getAction().toString());
                    e.setCancelled(true);
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="Starting Items">
            if (e.getWhoClicked().getOpenInventory().getTopInventory() == event.getMods().getStartingItems().getInventory()) {
                if (e.getClickedInventory() == event.getMods().getStartingItems().getInventory()) {
                    if (e.getSlot() == 35) {
                        e.setCancelled(true);
                        e.getWhoClicked().openInventory(event.getInventory());
                    }
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="Effect Settings">
            if (e.getWhoClicked().getOpenInventory().getTopInventory() == event.getMods().getEffectSettings().getInventory()) {
                if (e.getClickedInventory() == event.getMods().getEffectSettings().getInventory()) {
                    if (e.getSlot() == 35) {
                        e.setCancelled(true);
                        e.getWhoClicked().openInventory(event.getInventory());
                    }
                }
                if (e.getCurrentItem() != null) {
                    if (!e.getCurrentItem().getType().equals(Material.POTION)) {
                        e.setCancelled(true);
                    }
                }
                break;
            }
            //</editor-fold>
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
            val entry = PlayerManager.getModifyingEvent().remove(e.getPlayer());
            e.setCancelled(true);
            String name = e.getMessage().replace(" ", "_");
            if (entry.getKey() == 0) {
                EventManager.getEvents().remove(entry.getValue().getName());
                entry.getValue().updateItems(entry.getKey(), name, e.getPlayer());
                EventManager.getEvents().put(entry.getValue().getName(), entry.getValue());
            }
            else {
                entry.getValue().updateItems(entry.getKey(), name, e.getPlayer());
            }
        }
    }

    /**
     * Call death at contest event
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getEntity())) {
            RabPlayerDeathAtContestEvent event = new RabPlayerDeathAtContestEvent(e.getEntity().getName(), EventManager.getActiveEvent());
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
                e.setRespawnLocation(PlayerManager.getCheckpointed().get(e.getPlayer()).getSavedLocation());
            }
            else {
                e.setRespawnLocation(EventManager.getActiveEvent().getTeleport().getLocation());
            }
        }
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
            assert e.getTo() != null;
            if (EventManager.getActiveEvent().getCheckpoints().contains(e.getTo().getBlock().getLocation())) {
                if (EventManager.getActiveEvent().getMods().isActiveCheckpointsEnabled()) {
                    if (PlayerManager.getCheckpointed().containsKey(e.getPlayer())) {
                        if (!PlayerManager.getCheckpointed().get(e.getPlayer()).getCheckpointLocation().equals(e.getTo().getBlock().getLocation())) {
                            PlayerManager.getCheckpointed().get(e.getPlayer()).setCheckpointLocation(e.getTo().getBlock().getLocation());
                            PlayerManager.getCheckpointed().get(e.getPlayer()).setSavedLocation(e.getTo());
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-reached"), true, e.getPlayer());
                        }
                    }
                    else {
                        PlayerManager.getCheckpointed().put(e.getPlayer(), new RabCheckpointLocation(e.getTo().getBlock().getLocation(), e.getTo()));
                        sendLM(getPrefix() + " " + getFilMan().getWords().getString("checkpoint-reached"), true, e.getPlayer());
                    }
                }
            }
        }
    }
}
