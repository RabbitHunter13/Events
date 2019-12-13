package com.rabbit13.events.objects;

import org.bukkit.entity.Player;
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
    void updateItems(int slot, Player player);

    @Override
    @NotNull Inventory getInventory();

    //Fall Damage Mod
    boolean getFallDamage();

    void setFallDamage(boolean fallDamage);

    //Lava Equals Fail Mod
    boolean getLavaEqualsFail();

    void setLavaEqualsFail(boolean lavaEqualsFail);

    //More HP Mod
    boolean getMoreHP();

    void setMoreHP(boolean moreHP);

    //Rapid Damage Mod
    boolean getRapidDamage();

    void setRapidDamage(boolean rapidDamage);

}
