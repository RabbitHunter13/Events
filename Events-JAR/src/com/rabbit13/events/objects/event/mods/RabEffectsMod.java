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

public final class RabEffectsMod extends RabMod implements Effects, Listener {
    @Getter
    private Inventory effectsInv;

    public RabEffectsMod(EventMods mods) {
        super(mods);
        effectsInv = Bukkit.createInventory(null, 36, "Effect Settings");
        initialize();
        setEnabled(true);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }


    public RabEffectsMod(EventMods mods, ItemStack[] items, boolean active) {
        super(mods);
        effectsInv = Bukkit.createInventory(null, 36, "Effect Settings");
        effectsInv.setContents(items);
        initialize();
        setEnabled(active);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    private void initialize() {
        getInventory().setItem(1, getSpecifiedItem(Material.CHEST, 1, "Effect Settings"));
        effectsInv.setItem(35, getSpecifiedItem(Material.ARROW, 1, "&cBack"));
    }

    @EventHandler
    public void modify(InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory() == getInventory()) {
            if (e.getClickedInventory() == getInventory()) {
                if (e.getSlot() == 1) {
                    e.getWhoClicked().openInventory(effectsInv);
                }
            }
        }
        else if (e.getWhoClicked().getOpenInventory().getTopInventory() == effectsInv) {
            if (e.getClickedInventory() == effectsInv) {
                if (e.getSlot() == 35) {
                    e.getWhoClicked().openInventory(super.getInventory());
                }
            }
            if (e.getCurrentItem() != null) {
                if(!e.getCurrentItem().getType().equals(Material.POTION)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
