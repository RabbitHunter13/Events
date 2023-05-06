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
    private final String owner;
    @Getter
    private Inventory modsHolder;
    @Getter
    private final CheckpointsMod checkpointsMod;
    @Getter
    private final EffectsMod effectsMod;
    @Getter
    private final FallDamageMod fallDamageMod;
    @Getter
    private final LavaEqualFailMod lavaEqualFailMod;
    @Getter
    private final MoreHPMod moreHPMod;
    @Getter
    private final RewardItemsMod rewardItemsMod;
    @Getter
    private final StartingItemsMod startingItemsMod;
    @Getter
    private final NoSwimMod noSwimMod;

    // TODO: 3/20/2020 disable collision mod
    public RabEventMods(String owner) {
        this.owner = owner;
        checkpointsMod = new RabCheckpointsMod(this);
        effectsMod = new RabEffectsMod(this);
        fallDamageMod = new RabNoFallDamageMod(this);
        lavaEqualFailMod = new RabLavaEqualsFailMod(this);
        moreHPMod = new RabMoreHPMod(this);
        rewardItemsMod = new RabRewardsMod(this);
        startingItemsMod = new RabStartingItemsMod(this);
        noSwimMod = new RabNoSwimMod(this);
        initializeItems();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    public RabEventMods(String owner, ConfigurationSection section) {
        this.owner = owner;
        //<editor-fold desc="Fall Damage">
        var modSection = section.getConfigurationSection("fall-damage");
        if (modSection != null) {
            fallDamageMod = new RabNoFallDamageMod(this, modSection.getBoolean("enabled", false));
        }
        else {
            fallDamageMod = new RabNoFallDamageMod(this);
            error("Error occured while loading fall-damage section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="Lava equals fail">
        modSection = section.getConfigurationSection("lava-equals-fail");
        if (modSection != null) {
            lavaEqualFailMod = new RabLavaEqualsFailMod(this, modSection.getBoolean("enabled", true));
        }
        else {
            lavaEqualFailMod = new RabLavaEqualsFailMod(this);
            error("Error occured while loading lava-equals-fail section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="Checkpoints">
        modSection = section.getConfigurationSection("checkpoints");
        if (modSection != null) {
            checkpointsMod = new RabCheckpointsMod(this, modSection.getBoolean("enabled", true));
        }
        else {
            checkpointsMod = new RabCheckpointsMod(this);
            error("Error occured while loading checkpoints section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="MoreHP">
        modSection = section.getConfigurationSection("more-hp");
        if (modSection != null) {
            moreHPMod = new RabMoreHPMod(this,
                                         modSection.getInt("value", 20),
                                         modSection.getBoolean("enabled", false));
        }
        else {
            moreHPMod = new RabMoreHPMod(this);
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
                startingItemsMod = new RabStartingItemsMod(this,
                                                           startingItemsArr,
                                                           modSection.getBoolean("enabled"));
            }
            else {
                startingItemsMod = new RabStartingItemsMod(this);
                error("Error occured while loading starting-items items in event: " + owner);
            }
        }
        else {
            startingItemsMod = new RabStartingItemsMod(this);
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
                effectsMod = new RabEffectsMod(this,
                                               effectSettingsArr,
                                               modSection.getBoolean("enabled", true));
            }
            else {
                effectsMod = new RabEffectsMod(this);
                error("Error occured while loading effects items in event: " + owner);
            }
        }
        else {
            effectsMod = new RabEffectsMod(this);
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
                rewardItemsMod = new RabRewardsMod(this,
                                                   modSection.getInt("max-winners", 3),
                                                   rewardItemsArr,
                                                   modSection.getBoolean("enabled"));
            }
            else {
                rewardItemsMod = new RabRewardsMod(this);
                error("Error occured while loading reward-items items in event: " + owner);
            }
        }
        else {
            rewardItemsMod = new RabRewardsMod(this);
            error("Error occured while loading reward-items section in event: " + owner);
        }
        //</editor-fold>
        //<editor-fold desc="No-Swim">
        modSection = section.getConfigurationSection("no-swim");
        if (modSection != null) {
            noSwimMod = new RabNoSwimMod(this, modSection.getBoolean("enabled", false));
        }
        else {
            noSwimMod = new RabNoSwimMod(this);
            error("Error occured while loading lava-equals-fail section in event: " + owner);
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
        modsHolder.setItem(4, getSpecifiedItem(Material.WATER_BUCKET, 1, "&3No Swim Mod",
                                               "If enabled, players cannot 1.13 swim on event"));
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
                player.openInventory(fallDamageMod.getInventory());
                break;
            }
            case 1: {
                player.openInventory(lavaEqualFailMod.getInventory());
                break;
            }
            case 2: {
                player.openInventory(checkpointsMod.getInventory());
                break;
            }
            case 3: {
                player.openInventory(moreHPMod.getInventory());
                break;
            }
            case 4: {
                player.openInventory(noSwimMod.getInventory());
                break;
            }
            case 8: {
                player.openInventory(startingItemsMod.getInventory());
                break;
            }
            case 16: {
                player.openInventory(rewardItemsMod.getInventory());
                break;
            }
            case 17: {
                player.openInventory(effectsMod.getInventory());
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
            else {
                e.setCancelled(true);
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return modsHolder;
    }
}
