package com.rabbit13.events.objects.event.mods;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface EventMods extends InventoryHolder {


    /**
     * Updates values in mods inventory. All of them are boolean values.
     *
     * @param slot   slot that being clicked and updated
     * @param player player who updated value
     */
    void updateItems(int slot, Player player, InventoryAction action);

    void updateItemsWithAction(int slot, Player player, String text);

    @Override
    @NotNull Inventory getInventory();

    //Fall Damage Mod
    boolean isFallDamageEnabled();

    void setFallDamageEnabled(boolean fallDamageEnabled);

    //Lava Equals Fail Mod
    boolean isLavaEqualsFailEnabled();

    void setLavaEqualsFailEnabled(boolean lavaEqualsFailEnabled);

    //checkpoints mod
    boolean isActiveCheckpointsEnabled();

    void setActiveCheckpointsEnabled(boolean activeCheckpointsEnabled);

    //starting items
    boolean isStartingItemsEnabled();

    void setStartingItemsEnabled(boolean startingItemsEnabled);

    StartingItems getStartingItems();

    //effect settings
    boolean isEffectSettingsEnabled();

    void setEffectSettingsEnabled(boolean effectSettingsEnabled);

    EffectSettings getEffectSettings();

    //More HP Mod
    boolean isMoreHPEnabled();

    void setMoreHPEnabled(boolean moreHPEnabled);

    int getMoreHP();

}
