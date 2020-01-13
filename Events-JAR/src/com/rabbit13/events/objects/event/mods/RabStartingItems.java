package com.rabbit13.events.objects.event.mods;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.rabbit13.events.main.Misc.getSpecifiedItem;

public final class RabStartingItems implements StartingItems {
    private EventMods mods;
    private Inventory startingItems;

    public RabStartingItems(EventMods mods) {
        this.mods = mods;
        this.startingItems = Bukkit.createInventory(this, 36, "Starting Items");
        initialize();
    }

    private void initialize() {
        startingItems.setItem(35, getSpecifiedItem(Material.ARROW, 1, "&cBack"));
    }

    @Override public @NotNull Inventory getInventory() {
        return startingItems;
    }

    public EventMods getMods() {
        return mods;
    }
}
