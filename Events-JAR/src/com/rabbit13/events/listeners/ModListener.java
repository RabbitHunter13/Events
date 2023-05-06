package com.rabbit13.events.listeners;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.PlayerManager;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.rabbit13.events.main.Misc.sendLM;

/**
 * Listeners here listens just for active event
 */
public class ModListener implements Listener {


    /**
     * Monitoring Chat if someone modifying string values
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void chooseValueChatEvent(AsyncPlayerChatEvent e) {
        if (PlayerManager.getModifyingMods().containsKey(e.getPlayer())) {
            e.setCancelled(true);
            val entry = PlayerManager.getModifyingMods().remove(e.getPlayer());
            entry.getValue().chatUpdate(entry.getKey(), e.getMessage(), e.getPlayer());
        }
    }

    /**
     * handles fall damage mods
     */
    @EventHandler
    public void damageReducer(EntityDamageEvent e) {
        Player player;
        if (e.getEntity() instanceof Player) {
            player = (Player) e.getEntity();
            if (PlayerManager.getJoinedEvent().containsKey(player)) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    if (EventManager.getActiveEvent() != null) {
                        if (EventManager.getActiveEvent().getMods().getFallDamageMod().isEnabled()) {
                            e.setCancelled(true);
                        }
                    }
                }
                else if (e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                    if (EventManager.getActiveEvent() != null) {
                        if (EventManager.getActiveEvent().getMods().getLavaEqualFailMod().isEnabled()) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void disableSwim(EntityToggleSwimEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (EventManager.getActiveEvent() != null) {
                if (PlayerManager.getJoinedEvent().containsKey(player)) {
                    if (EventManager.getActiveEvent().getMods().getNoSwimMod().isEnabled()) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
            if (e.getTo() != null) {
                if (e.getTo().getBlock().getType().equals(Material.LAVA)) {
                    if (EventManager.getActiveEvent() != null) {
                        if (EventManager.getActiveEvent().getMods().getLavaEqualFailMod().isEnabled()) {
                            PlayerManager.playerLeavingEvent(e.getPlayer(), null, false);
                            sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-fail"), true, e.getPlayer());
                        }
                    }
                }
            }
        }
    }
}
