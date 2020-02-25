package com.rabbit13.events.objects.event;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.event.mods.*;
import lombok.Getter;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.rabbit13.events.main.Misc.*;

public final class RabEventMods implements InventoryHolder, EventMods, Listener {
    @Getter
    private String owner;
    @Getter
    private Inventory modsHolder;
    @Getter
    private Checkpoints checkpoints;
    @Getter
    private Effects effects;
    @Getter
    private FallDamage fallDamage;
    @Getter
    private LavaEqualFail lavaEqualFail;
    @Getter
    private MoreHP moreHP;
    @Getter
    private RewardItems rewards;
    @Getter
    private StartingItems startingItems;

    public RabEventMods(String owner) {
        this.owner = owner;
        checkpoints = new RabCheckpointsMod(this);
        effects = new RabEffectsMod(this);
        fallDamage = new RabNoFallDamageMod(this);
        lavaEqualFail = new RabLavaEqualsFailMod(this);
        moreHP = new RabMoreHPMod(this);
        rewards = new RabRewardsMod(this);
        startingItems = new RabStartingItemsMod(this);
        initializeItems();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    public RabEventMods(String owner, ConfigurationSection section) {
        this.owner = owner;
        //<editor-fold desc="Fall Damage">
        var modSection = section.getConfigurationSection("fall-damage");
        if (modSection != null) {
            fallDamage = new RabNoFallDamageMod(this, modSection.getBoolean("enabled", false));
        }
        else {
            fallDamage = new RabNoFallDamageMod(this);
            error("Error occured while loading fall-damage section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="Lava equals fail">
        modSection = section.getConfigurationSection("lava-equals-fail");
        if (modSection != null) {
            lavaEqualFail = new RabLavaEqualsFailMod(this, modSection.getBoolean("enabled", true));
        }
        else {
            lavaEqualFail = new RabLavaEqualsFailMod(this);
            error("Error occured while loading lava-equals-fail section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="Checkpoints">
        modSection = section.getConfigurationSection("checkpoints");
        if (modSection != null) {
            checkpoints = new RabCheckpointsMod(this, modSection.getBoolean("enabled", true));
        }
        else {
            checkpoints = new RabCheckpointsMod(this);
            error("Error occured while loading checkpoints section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="MoreHP">
        modSection = section.getConfigurationSection("more-hp");
        if (modSection != null) {
            moreHP = new RabMoreHPMod(this,
                                      modSection.getInt("value", 20),
                                      modSection.getBoolean("enabled", false));
        }
        else {
            moreHP = new RabMoreHPMod(this);
            error("Error occured while loading more-hp section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="Starting Items">
        modSection = section.getConfigurationSection("starting-items");
        if (modSection != null) {
            List<?> startingList = modSection.getList("items");
            ItemStack[] startingItemsArr = null;
            if (startingList != null) {
                startingItemsArr = startingList.toArray(new ItemStack[0]);
            }
            if (startingItemsArr != null) {
                startingItems = new RabStartingItemsMod(this,
                                                        startingItemsArr,
                                                        modSection.getBoolean("enabled"));
            }
            else {
                startingItems = new RabStartingItemsMod(this);
                error("Error occured while loading starting-items items in event: " + owner);
            }
        }
        else {
            startingItems = new RabStartingItemsMod(this);
            error("Error occured while loading starting-items section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="Effects">
        modSection = section.getConfigurationSection("effects");

        if (modSection != null) {
            List<?> potionList = modSection.getList("items");
            ItemStack[] effectSettingsArr = null;
            if (potionList != null) {
                effectSettingsArr = potionList.toArray(new ItemStack[0]);
            }
            if (effectSettingsArr != null) {
                effects = new RabEffectsMod(this,
                                            effectSettingsArr,
                                            modSection.getBoolean("enabled", true));
            }
            else {
                effects = new RabEffectsMod(this);
                error("Error occured while loading effects items in event: " + owner);
            }
        }
        else {
            effects = new RabEffectsMod(this);
            error("Error occured while loading effects section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="Rewards">
        modSection = section.getConfigurationSection("rewards");
        if (modSection != null) {
            List<?> rewardList = modSection.getList("items");
            ItemStack[] rewardItemsArr = null;
            if (rewardList != null) {
                rewardItemsArr = rewardList.toArray(new ItemStack[0]);
            }
            if (rewardItemsArr != null) {
                rewards = new RabRewardsMod(this,
                                            modSection.getInt("max-winners", 3),
                                            rewardItemsArr,
                                            modSection.getBoolean("enabled"));
            }
            else {
                rewards = new RabRewardsMod(this);
                error("Error occured while loading reward-items items in event: " + owner);
            }
        }
        else {
            rewards = new RabRewardsMod(this);
            error("Error occured while loading reward-items section in event: " + owner);
        }
        //</editor-fold>
        initializeItems();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    private void initializeItems() {
        modsHolder = Bukkit.createInventory(this, 18, "Mods");
        modsHolder.setItem(0, getSpecifiedItem(Material.FEATHER, 1, "&fNo-Fall Damage",
                                               "If Enabled, Fall Damage is not present at this event"));
        modsHolder.setItem(1, getSpecifiedItem(Material.LAVA_BUCKET, 1, "&eLava Equals Fail",
                                               "If Enabled, Lava automatically kick players out of event"));
        modsHolder.setItem(2, getSpecifiedItem(Material.EXPERIENCE_BOTTLE, 1, "&aCheckpoints Activated",
                                               "If activated, checkpoints will work"));
        modsHolder.setItem(3, getSpecifiedItem(Material.GOLDEN_APPLE, 1, "&6More HP Mod",
                                               "Set player hp to given value. &6Minimum is 1"));
        modsHolder.setItem(8, getSpecifiedItem(Material.CHEST_MINECART, 1, "&bStarting Items",
                                               "If enabled, player will get starting items",
                                               "that are in this event's \"Starting items\" inventory."));
        modsHolder.setItem(16, getSpecifiedItem(Material.DIAMOND_BLOCK, 1, "Rewards",
                                                "If enabled, players will get rewards and leave event",
                                                "automatically when they stand on reward position."));
        modsHolder.setItem(17, getSpecifiedItem(Material.POTION, 1, "Effect Settings",
                                                "If enabled, player will get infinite potion effects of type",
                                                "that are in this event's \"Effect Settings\" inventory."));
    }

    /**
     * Opens inventory of a mod
     *
     * @param slot   slot that being clicked and updated
     * @param player player who updated value
     */
    public void openMods(int slot, Player player) {
        switch (slot) {
            case 0: {
                player.openInventory(fallDamage.getInventory());
                break;
            }
            case 1: {
                player.openInventory(lavaEqualFail.getInventory());
                break;
            }
            case 2: {
                player.openInventory(checkpoints.getInventory());
                break;
            }
            case 3: {
                player.openInventory(moreHP.getInventory());
                break;
            }
            case 8: {
                player.openInventory(startingItems.getInventory());
                break;
            }
            case 16: {
                player.openInventory(rewards.getInventory());
                break;
            }
            case 17: {
                player.openInventory(effects.getInventory());
                break;
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory() == this.getInventory()) {
            if (e.getClickedInventory() == this.getInventory()) {
                this.openMods(e.getSlot(), (Player) e.getWhoClicked());
                debugMessage("Clicked slot: " + e.getSlot());
                e.setCancelled(true);
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return modsHolder;
    }
}
