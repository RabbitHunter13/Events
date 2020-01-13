package com.rabbit13.events.objects.event.mods;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.rabbit13.events.main.Misc.getSpecifiedItem;

public final class RabEffectSettings implements EffectSettings {
    Inventory effectSettings;
    EventMods mods;

    public RabEffectSettings(EventMods mods) {
        this.mods = mods;
        effectSettings = Bukkit.createInventory(this, 36, "Effect Settings");
        initialize();
    }

    private void initialize() {
        effectSettings.setItem(35, getSpecifiedItem(Material.ARROW, 1, "&cBack"));
    }

    @Override public @NotNull Inventory getInventory() {
        return effectSettings;
    }

    @Override public EventMods getMods() {
        return mods;
    }
}
