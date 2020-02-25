package com.rabbit13.events.listeners;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.PlayerManager;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.rabbit13.events.main.Misc.sendLM;

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
     * handles lava/fall damage mods
     */
    @EventHandler
    public void damageReducer(EntityDamageEvent e) {
        Player player;
        if (e.getEntity() instanceof Player) {
            player = (Player) e.getEntity();
            if (PlayerManager.getJoinedEvent().containsKey(player)) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    if (EventManager.getActiveEvent().getMods().getFallDamage().isEnabled()) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
            assert e.getTo() != null;
            if (e.getTo().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.LAVA)) {
                if (EventManager.getActiveEvent().getMods().getLavaEqualFail().isEnabled()) {
                    e.setCancelled(true);
                    PlayerManager.playerLeavingEvent(e.getPlayer(),null,false);
                    sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-fail"), true, e.getPlayer());
                }
            }
        }
    }
}
