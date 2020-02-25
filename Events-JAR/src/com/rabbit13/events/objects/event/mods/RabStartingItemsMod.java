package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.event.EventMods;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.rabbit13.events.main.Misc.getSpecifiedItem;

public final class RabStartingItemsMod extends RabMod implements StartingItems, Listener {
    @Getter
    private Inventory startingItems;

    public RabStartingItemsMod(EventMods mods) {
        super(mods);
        this.startingItems = Bukkit.createInventory(this, 36, "Starting Items");
        initialize();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }


    public RabStartingItemsMod(EventMods mods, ItemStack[] items, boolean enabled) {
        super(mods);
        this.startingItems = Bukkit.createInventory(this, 36, "Starting Items");
        startingItems.addItem(items);
        initialize();
        setEnabled(enabled);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    private void initialize() {
        getInventory().setItem(1, getSpecifiedItem(Material.CHEST_MINECART, 1, "Starting items"));
        startingItems.setItem(35, getSpecifiedItem(Material.ARROW, 1, "&cBack"));
    }

    @EventHandler
    public void modify(InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory() == getInventory()) {
            if (e.getClickedInventory() == getInventory()) {
                if (e.getSlot() == 1) {
                    e.getWhoClicked().openInventory(startingItems);
                }
            }
        }
        else if (e.getWhoClicked().getOpenInventory().getTopInventory() == startingItems) {
            if (e.getClickedInventory() == startingItems) {
                if (e.getSlot() == 35) {
                    e.setCancelled(true);
                    e.getWhoClicked().openInventory(super.getInventory());
                }
            }
        }
    }

}
