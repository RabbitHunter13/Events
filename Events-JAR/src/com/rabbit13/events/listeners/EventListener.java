package com.rabbit13.events.listeners;

import com.rabbit13.events.events.RabPlayerDeathAtContestEvent;
import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.EventManager;
import com.rabbit13.events.managers.PlayerManager;
import com.rabbit13.events.objects.event.tools.RabCheckpointLocation;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import static com.rabbit13.events.main.Main.getFilMan;
import static com.rabbit13.events.main.Main.getPrefix;
import static com.rabbit13.events.main.Misc.sendLM;

public final class EventListener implements Listener {

    /**
     * remove player from event if disconnected
     */
    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer()))
            PlayerManager.playerLeavingEvent(e.getPlayer(), null, false);
        PlayerManager.getModifyingEvent().remove(e.getPlayer());
    }

    /**
     * all commands are prohibited, excluding /e quit (for players)
     */
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getPlayer().hasPermission("events.staff") || !e.getPlayer().hasPermission("events.moderator")) {
            String[] args = e.getMessage().split(" ");
            //commands here are permitted, or sub-permitted
            if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
                if (!args[0].equalsIgnoreCase("/e") && !args[0].equalsIgnoreCase("/event")) {
                    e.setCancelled(true);
                    sendLM(getPrefix() + " " + getFilMan().getWords().getString("no-permission-commands"), true, e.getPlayer());
                }
                else {
                    if (args.length > 1) {
                        String argument = args[1];
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
                if (EventManager.getActiveEvent() != null) {
                    e.setRespawnLocation(EventManager.getActiveEvent().getTeleport());
                }
            }
        }
    }

    /**
     * Checkpoint and reward place controller
     */
    @EventHandler
    public void onMovement(PlayerMoveEvent e) {
        if (PlayerManager.getJoinedEvent().containsKey(e.getPlayer())) {
            if (e.getTo() != null) {
                assert EventManager.getActiveEvent() != null;
                if (EventManager.getActiveEvent().getCheckpoints().contains(e.getTo().getBlock().getLocation())) {
                    if (EventManager.getActiveEvent().getMods().getCheckpointsMod().isEnabled()) {
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
                if (EventManager.getActiveEvent().getFinish() != null) {
                    if (EventManager.getActiveEvent().getMods().getRewardItemsMod().isEnabled()) {
                        if (EventManager.getActiveEvent().getFinish().getBlock().getLocation().equals(e.getTo().getBlock().getLocation())) {
                            Bukkit.dispatchCommand(Main.getSender(), "e b &3&l" + (EventManager.getActiveEvent().getMods().getRewardItemsMod().getWinnerIndex() + 1) + ". Misto &f&l" + e.getPlayer().getName());
                            Bukkit.dispatchCommand(Main.getSender(), "e win add " + e.getPlayer().getName());
                            PlayerManager.playerLeavingEvent(e.getPlayer(), EventManager.getActiveEvent().getMods().getRewardItemsMod().giveRewardToPlayer(), false);
                            if (EventManager.getActiveEvent().getMods().getRewardItemsMod().getWinnerIndex() == 0) {
                                Bukkit.dispatchCommand(Main.getSender(), "e end");
                            }
                        }
                    }
                }
            }
        }
    }

}
