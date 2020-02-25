package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.objects.event.EventMods;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.rabbit13.events.main.Misc.getSpecifiedItem;

public abstract class RabMod implements Mod, Listener {
    @Getter
    private final EventMods mods;
    private boolean enabled;
    private Inventory gui;

    public RabMod(EventMods mods) {
        this.mods = mods;
        this.gui = Bukkit.createInventory(this, 18, "Options");
        initializeGui();
    }

    private void initializeGui() {
        gui.setItem(0, getSpecifiedItem(enabled ? Material.GREEN_WOOL : Material.RED_WOOL, 1, "Active",
                                        "Activated?: " + (enabled ? "&atrue" : "&cfalse")));
        gui.setItem(17, getSpecifiedItem(Material.ARROW, 1, "&cBack"));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        gui.setItem(0, getSpecifiedItem(enabled ? Material.GREEN_WOOL : Material.RED_WOOL, 1, "Active",
                                        "Activated?: " + (enabled ? "&atrue" : "&cfalse")));
    }

    @Override public @NotNull Inventory getInventory() {
        return gui;
    }

    @EventHandler
    public void chooseModifier(InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory() == gui) {
            if (e.getClickedInventory() == gui) {
                if (e.getSlot() == 0) {
                    setEnabled(!enabled);
                }
                else if (e.getSlot() == 17) {
                    e.getWhoClicked().openInventory(getMods().getInventory());
                }
            }
            e.setCancelled(true);
        }
    }

    /**
     * Called when theres incoming string from chatEvent
     */
    @ApiStatus.OverrideOnly
    public void chatUpdate(int slot, String text, CommandSender sender) {}
}

