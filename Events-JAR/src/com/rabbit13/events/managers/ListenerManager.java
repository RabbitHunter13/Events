package com.rabbit13.events.managers;

import com.rabbit13.events.events.PlayerDeathAtContestEvent;
import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.rabbit13.events.main.Misc.debugMessage;
import static com.rabbit13.events.main.Misc.sendLM;

public final class ListenerManager implements Listener {
    private static final HashMap<Player, Event> modifyingEvent = new HashMap<>();

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        PlayerManager.getJoinedEvent().remove(e.getPlayer());
        modifyingEvent.remove(e.getPlayer());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getPlayer().hasPermission("events.staff") || !e.getPlayer().hasPermission("events.moderator")) {
            String[] args = e.getMessage().split(" ");
            debugMessage("Arguments: " + Arrays.toString(args));
            //commands here are permitted, or sub-permitted
            if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
                if (!args[0].equalsIgnoreCase("/e") && !args[0].equalsIgnoreCase("/event")) {
                    e.setCancelled(true);
                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission-commands"), true, e.getPlayer());
                }
                else {
                    if (args.length > 1) {
                        String argument = args[1];
                        debugMessage("Argument 1: " + argument);
                        if (!argument.equalsIgnoreCase("quit") && !argument.equalsIgnoreCase("checkpoint")) {
                            e.setCancelled(true);
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("no-permission-commands"), true, e.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void chooseValueChatEvent(AsyncPlayerChatEvent e) {
        if (modifyingEvent.containsKey(e.getPlayer())) {
            Event event = modifyingEvent.remove(e.getPlayer());
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

    @EventHandler
    public void onInventoryItemPickup(InventoryClickEvent e) {
        for (Map.Entry<String, Event> entry : EventManager.getEvents().entrySet()) {
            Event event = entry.getValue();
            event.modificator = e.getSlot();
            ItemStack item = event.getInventory().getItem(e.getSlot());
            if (event.getInventory().equals(e.getView().getTopInventory())) {
                if (item != null) {
                    if (e.getSlot() == 0 || e.getSlot() == 1) {
                        debugMessage("triggered if in itempickup listener");
                        modifyingEvent.put((Player) e.getWhoClicked(), event);
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-modification-init"), true, e.getWhoClicked());
                        e.getWhoClicked().closeInventory();
                    }
                    else {
                        debugMessage("triggered else in itempickup listener");
                        event.updateItems(event.modificator, (Player) e.getWhoClicked());
                        if (e.getSlot() == 2) {
                            e.getWhoClicked().closeInventory();
                        }
                    }
                }
                debugMessage("Clicked item: " + ((item != null) ? item.getType().toString() : "Null"));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void damageReducer(EntityDamageEvent e) {
        Player player = null;
        if (e.getEntity() instanceof Player) {
            player = (Player) e.getEntity();
        }
        if (player != null) {
            if (PlayerManager.getJoinedEvent().containsKey(player)) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    if (!EventManager.getActiveEvent().getFallDamage()) {
                        e.setCancelled(true);
                    }
                }
                else if (e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                    if (EventManager.getActiveEvent().getLavaEqualsFail()) {
                        e.setCancelled(true);
                        player.teleport(PlayerManager.getJoinedEvent().remove(player));
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> e.getEntity().setFireTicks(0));
                        ((Player) e.getEntity()).setHealth(((Player) e.getEntity()).getMaxHealth());
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-fail"), true, player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getEntity())) {
            PlayerDeathAtContestEvent event = new PlayerDeathAtContestEvent(e.getEntity().getName(), EventManager.getActiveEvent().getName());
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    /**
     * After player respawn, it would check if he is still at event, if it is, then te respawn point will be at event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
            e.setRespawnLocation(EventManager.getActiveEvent().getTeleport());
        }
    }

}
