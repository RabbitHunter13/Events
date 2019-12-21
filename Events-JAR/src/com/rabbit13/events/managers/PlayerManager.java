package com.rabbit13.events.managers;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.eData;
import com.rabbit13.events.objects.eEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

import static com.rabbit13.events.main.Misc.debugMessage;

public class PlayerManager {
    private static final HashMap<Player, eData> joinedEvent = new HashMap<>();
    private static final HashMap<Player, eEvent> modifyingEvent = new HashMap<>();
    private static final Map<String, Integer> winCounter = new HashMap<>();

    /**
     * Called when player is about to enter event
     *
     * @param player player entering event
     * @return data of player (items, potions, and location)
     */
    public static eData playerEnteringEvent(Player player) {
        debugMessage("all items in inventory: " + player.getInventory().getContents().length);
        eData data = new eData(player.getInventory().getHelmet()
                , player.getInventory().getChestplate()
                , player.getInventory().getLeggings()
                , player.getInventory().getBoots()
                , player.getInventory().getContents()
                , player.getActivePotionEffects()
                , player.getLocation());
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        //health set to maximum
        double maxHealth = player.getMaxHealth();
        debugMessage("Max Health for player " + player.getName() + ": " + maxHealth);
        player.setHealth(maxHealth);
        BackupItemsManager.createBackup(player, data);
        return data;
    }

    /**
     * Retrieve items to player that had before joining and teleport him to his location before joining
     *
     * @param player player leaving event
     */
    public static void playerLeavingEvent(Player player) {
        debugMessage("Leaving player: " + player);
        eData data = joinedEvent.get(player);
        PlayerInventory inventory = player.getInventory();
        player.teleport(data.getLocation());
        inventory.setHelmet(data.getHelmet());
        inventory.setChestplate(data.getChestplate());
        inventory.setLeggings(data.getLeggings());
        inventory.setBoots(data.getBoots());
        inventory.setContents(data.getItems());
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            player.addPotionEffects(data.getEffects());
            player.setFireTicks(0);
        });

        joinedEvent.remove(player);
    }

    /**
     * @return players that joined active event
     */
    public static HashMap<Player, eData> getJoinedEvent() {
        return joinedEvent;
    }

    public static HashMap<Player, eEvent> getModifyingEvent() {
        return modifyingEvent;
    }

    public static Map<String, Integer> getWinCounter() {
        return winCounter;
    }
}