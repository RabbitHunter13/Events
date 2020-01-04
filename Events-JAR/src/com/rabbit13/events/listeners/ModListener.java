package com.rabbit13.events.listeners;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static com.rabbit13.events.main.Misc.sendLM;

public class ModListener implements Listener {
    // TODO: 06.12.2019 rapidDMG a moreHP funkctions
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
                    if (!EventManager.getActiveEvent().getMods().isFallDamage()) {
                        e.setCancelled(true);
                    }
                }
                else if (e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                    if (EventManager.getActiveEvent().getMods().isLavaEqualsFail()) {
                        e.setCancelled(true);
                        PlayerManager.playerLeavingEvent(player);
                        sendLM(Main.getPrefix() + " " + Main.getFilMan().getWords().getString("event-fail"), true, player);
                    }
                }
            }
        }
    }
}
