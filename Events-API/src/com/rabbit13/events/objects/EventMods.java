package com.rabbit13.events.objects;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
interface EventMods extends InventoryHolder {

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
