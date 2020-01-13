package com.rabbit13.events.objects.event;

import com.rabbit13.events.objects.event.mods.EventMods;
import com.rabbit13.events.objects.event.tools.EventLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Event extends InventoryHolder {

    @NotNull
    @Override
    Inventory getInventory();

    /**
     * Set and update values of event thru ChatListener
     *
     * @param slot   slot of {@link org.bukkit.inventory.ItemStack ItemStack} that is being updated - holds old modified info
     * @param data   data that is being loaded into value
     * @param player player who clicked
     */
    void updateItems(int slot, String data, Player player);

    /**
     * Update without ChatListener
     *
     * @param slot   index of clicked slot
     * @param player player who clicked
     */
    void updateItems(int slot, Player player);

    String getName();

    String getOwner();

    EventLocation getTeleport();

    boolean isLockedTeleport();

    void setLockedTeleport(boolean locked);

    EventMods getMods();

    List<Location> getCheckpoints();

    List<String> getBanned();

}
