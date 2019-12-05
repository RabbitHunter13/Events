package com.rabbit13.events.managers;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.Data;
import com.rabbit13.events.objects.Event;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Objects;

import static com.rabbit13.events.main.Misc.debugMessage;

public class PlayerManager {
    private static final HashMap<Player, Data> joinedEvent = new HashMap<>();
    private static final HashMap<Player, Event> modifyingEvent = new HashMap<>();

    /**
     * Called when player is about to enter event
     *
     * @param player player entering event
     * @return data of player (items, potions, and location)
     */
    public static Data playerEnteringEvent(Player player) {
        debugMessage("all items in inventory: " + player.getInventory().getContents().length);
        Data data = new Data(player.getInventory().getHelmet()
                , player.getInventory().getChestplate()
                , player.getInventory().getLeggings()
                , player.getInventory().getBoots()
                , player.getInventory().getItemInOffHand()
                , player.getInventory().getContents().clone()
                , player.getActivePotionEffects()
                , player.getLocation());

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        //health set to maximum
        double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        debugMessage("Max Health for player " + player.getName() + ": " + maxHealth);
        player.setHealth(maxHealth);

        return data;
    }

    /**
     * Retrieve items to player that had before joining and teleport him to his location before joining
     *
     * @param player player leaving event
     */
    public static void playerLeavingEvent(Player player) {
        debugMessage("Leaving player: " + player);
        Data data = joinedEvent.get(player);
        PlayerInventory inventory = player.getInventory();
        player.teleport(data.getLocation());
        inventory.setHelmet(data.getHelmet());
        inventory.setChestplate(data.getChestplate());
        inventory.setLeggings(data.getLeggings());
        inventory.setBoots(data.getBoots());
        inventory.setItemInOffHand(data.getOffHand());
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
    public static HashMap<Player, Data> getJoinedEvent() {
        return joinedEvent;
    }

    public static HashMap<Player, Event> getModifyingEvent() {
        return modifyingEvent;
    }
}