package com.rabbit13.events.managers;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.PlayerData;
import com.rabbit13.events.objects.RabPlayerData;
import com.rabbit13.events.objects.event.Event;
import com.rabbit13.events.objects.event.mods.EventMods;
import com.rabbit13.events.objects.event.tools.CheckpointLocation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.rabbit13.events.main.Main.getInstance;
import static com.rabbit13.events.main.Misc.debugMessage;

public class PlayerManager {
    @Getter private static final Map<Player, PlayerData> joinedEvent = new HashMap<>();
    @Getter private static final Map<Player, CheckpointLocation> checkpointed = new HashMap<>();
    @Getter private static final Map<Player, AbstractMap.SimpleEntry<Integer, Event>> modifyingEvent = new HashMap<>();
    @Getter private static final Map<Player, AbstractMap.SimpleEntry<Integer, EventMods>> modifyingMods = new HashMap<>();
    @Getter private static final Map<String, Integer> winCounter = new HashMap<>();


    /**
     * Called when player is about to enter event
     *
     * @param player player entering event
     */
    @SuppressWarnings("ConstantConditions")
    public static void playerEnteringEvent(Player player) {
        debugMessage("Entering player: " + player.getName());
        PlayerData data = new RabPlayerData(player.getInventory().getContents().clone(),
                                            player.getActivePotionEffects(),
                                            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                                            player.getExp(),
                                            player.getLevel(),
                                            player.getLocation());
        BackupItemsManager.createBackup(player, data);
        EventMods mods = EventManager.getActiveEvent().getMods();
        clearPlayer(player,false);
        if (mods.isStartingItemsEnabled()) {
            for (int i = 0; i < mods.getStartingItems().getInventory().getSize() - 1; i++) {
                ItemStack item = mods.getStartingItems().getInventory().getItem(i);
                if (item != null) {
                    player.getInventory().addItem(item);
                }
            }
        }
        if (mods.isEffectSettingsEnabled()) {
            for (ItemStack potion : EventManager.getActiveEvent().getMods().getEffectSettings().getInventory().getContents()) {
                if (potion != null) {
                    if (potion.getType().equals(Material.POTION)) {
                        PotionMeta meta = (PotionMeta) potion.getItemMeta();

                        debugMessage("PotionbaseData: " + meta.getBasePotionData().getType().getEffectType().toString());
                        if (meta != null) {
                            player.addPotionEffect(meta.getBasePotionData().getType().getEffectType().createEffect(Integer.MAX_VALUE, meta.getBasePotionData().isUpgraded() ? 1 : 0));
                        }
                    }
                }
            }
        }
        if (mods.isMoreHPEnabled()) {
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue((mods.getMoreHP() > 0) ? mods.getMoreHP() : 1);
        }
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
    }

    /**
     * Retrieve items to player that had before joining and teleport him to his location before joining
     *
     * @param player player leaving event
     */
    public static void playerLeavingEvent(Player player) {
        debugMessage("Leaving player: " + player);
        clearPlayer(player,false);
        PlayerData data = joinedEvent.get(player);
        returnItems(player, data);
        Bukkit.getScheduler().runTask(getInstance(), () -> {
            player.addPotionEffects(data.getEffects());
            player.setFireTicks(0);
        });
        debugMessage("Removed from checkpointed?: " + (checkpointed.remove(player) != null));
        debugMessage("Removed from joined event?: " + (joinedEvent.remove(player) != null));
    }

    /**
     * playerLeavingEvent method without Scheduler for {@link Main#onDisable()} usage
     *
     * @param player player leaving event
     * @param data players data
     */
    public static void playerLeavingEvent(Player player, PlayerData data) {
        debugMessage("Leaving player: " + player);
        clearPlayer(player,true);
        returnItems(player, data);
        player.addPotionEffects(data.getEffects());
        player.setFireTicks(0);
    }

    //<editor-fold desc="Other Methods">
    private static void returnItems(Player player, PlayerData data) {
        PlayerInventory inventory = player.getInventory();
        player.teleport(data.getLocation());
        inventory.setHelmet(data.getHelmet());
        inventory.setChestplate(data.getChestplate());
        inventory.setLeggings(data.getLeggings());
        inventory.setBoots(data.getBoots());
        inventory.setItemInOffHand(data.getOffHand());
        inventory.setContents(data.getItems());
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(data.getMaxHP());
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
        player.setLevel(data.getLevel());
        player.setExp(data.getExp());
    }

    private static void clearPlayer(Player player, boolean onDisable) {
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().setItemInOffHand(null);
        player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFoodLevel(20);
        if (!onDisable) {
            Bukkit.getScheduler().runTask(getInstance(), () -> player.setFireTicks(0));
        }
    }
    //</editor-fold>

}