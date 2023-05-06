package com.rabbit13.events.managers;

import com.rabbit13.events.objects.PlayerData;
import com.rabbit13.events.objects.RabPlayerData;
import com.rabbit13.events.objects.event.Event;
import com.rabbit13.events.objects.event.EventMods;
import com.rabbit13.events.objects.event.mods.Mod;
import com.rabbit13.events.objects.event.tools.CheckpointLocation;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.*;

import static com.rabbit13.events.main.Main.getInstance;
import static com.rabbit13.events.main.Misc.debugMessage;

public class PlayerManager {
    @Getter private static final Map<Player, PlayerData> joinedEvent = new HashMap<>();
    @Getter private static final Map<Player, CheckpointLocation> checkpointed = new HashMap<>();
    @Getter private static final Map<Player, AbstractMap.SimpleEntry<Integer, Event>> modifyingEvent = new HashMap<>();
    @Getter private static final Map<Player, AbstractMap.SimpleEntry<Integer, Mod>> modifyingMods = new HashMap<>();
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
                                            player.getEnderChest().getContents().clone(),
                                            player.getActivePotionEffects(),
                                            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                                            player.getExp(),
                                            player.getLevel(),
                                            player.getLocation());
        BackupManager.createBackup(player, data);
        EventMods mods = EventManager.getActiveEvent().getMods();
        clearPlayer(player, false);
        if (mods.getStartingItemsMod().isEnabled()) {
            for (int i = 0; i < mods.getStartingItemsMod().getStartingItems().getSize() - 1; i++) {
                ItemStack item = mods.getStartingItemsMod().getStartingItems().getItem(i);
                if (item != null) {
                    player.getInventory().addItem(item);
                }
            }
        }
        if (mods.getEffectsMod().isEnabled()) {
            for (ItemStack potion : EventManager.getActiveEvent().getMods().getEffectsMod().getEffectsInv().getContents()) {
                if (potion != null) {
                    if (potion.getType().equals(Material.POTION)) {
                        PotionMeta meta = (PotionMeta) potion.getItemMeta();
                        if (meta != null) {
                            player.addPotionEffect(meta.getBasePotionData().getType().getEffectType().createEffect(Integer.MAX_VALUE, meta.getBasePotionData().isUpgraded() ? 1 : 0));
                        }
                    }
                }
            }
        }
        if (mods.getMoreHPMod().isEnabled()) {
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue((mods.getMoreHPMod().getHealth() > 0) ? mods.getMoreHPMod().getHealth() : 1);
        }
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
    }

    /**
     * Retrieve items to player that had before joining and teleport him to his location before joining
     *
     * @param player player leaving event
     */
    public static void playerLeavingEvent(Player player, ItemStack[] reward, boolean onDisable) {
        debugMessage("Leaving player: " + player);
        clearPlayer(player, onDisable);
        PlayerData data = joinedEvent.get(player);
        returnItems(player, data);
        if (!onDisable) {
            Bukkit.getScheduler().runTaskLater(getInstance(), () -> player.addPotionEffects(data.getEffects()), 2);
        }
        else {
            player.addPotionEffects(data.getEffects());
            player.setFireTicks(0);
        }
        assert EventManager.getActiveEvent() != null;
        if (EventManager.getActiveEvent().getMods().getRewardItemsMod().isEnabled()) {
            debugMessage("PlayerManager#playerLeavingEvent reward: " + Arrays.toString(reward));
            if (reward != null && reward.length > 0) {
                player.getInventory().addItem(reward);
            }
        }
        debugMessage("Removed from checkpointed?: " + (checkpointed.remove(player) != null));
        debugMessage("Removed from joined event?: " + (joinedEvent.remove(player) != null));
    }

    public static Map<String, Integer> getTopWinners() {
        Map<String, Integer> result = new HashMap<>();
        Map<String, Integer> tempWinCounter = new HashMap<>(winCounter);
        String tempName = "";
        int tempInt = 0;
        for (int i = 0; i < 5; i++) {
            for (val entry : tempWinCounter.entrySet()) {
                if (entry.getValue() > tempInt) {
                    tempName = entry.getKey();
                    tempInt = entry.getValue();
                }
            }
            if (tempInt > 0) {
                result.put(tempName, tempInt);
                tempWinCounter.remove(tempName);
            }
            tempName = "";
            tempInt = 0;
        }
        return result;
    }

    //<editor-fold desc="Other Methods">
    private static void returnItems(Player player, PlayerData data) {
        player.teleport(data.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        PlayerInventory inventory = player.getInventory();
        inventory.setHelmet(data.getHelmet());
        inventory.setChestplate(data.getChestplate());
        inventory.setLeggings(data.getLeggings());
        inventory.setBoots(data.getBoots());
        inventory.setItemInOffHand(data.getOffHand());
        inventory.setContents(data.getItems());
        player.getEnderChest().setContents(data.getEnderChest());
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
        player.getEnderChest().clear();
        player.setExp(0);
        player.setLevel(0);
        player.setCollidable(true);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFoodLevel(20);
        if (!onDisable) {
            Bukkit.getScheduler().runTask(getInstance(), () -> player.setFireTicks(0));
        }
        else {
            player.setFireTicks(0);
        }
    }
    //</editor-fold>

}