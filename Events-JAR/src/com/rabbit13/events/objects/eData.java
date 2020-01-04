package com.rabbit13.events.objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class eData implements Data {
    private final ItemStack[] items;
    private final Collection<PotionEffect> effects;
    private final Location location;

    public eData(ItemStack[] items, Collection<PotionEffect> effects, Location location) {
        // TODO: 04.01.2020 test: only items
        this.items = items;
        this.effects = effects;
        this.location = location;
    }


    @Override
    public String toString() {
        assert location.getWorld() != null;
        return " [Items: " + items.length
                + " Effects: " + effects.size()
                + " Loc: " + location.getWorld().getName()
                + "]";
    }

    public ItemStack[] getItems() {
        return items;
    }

    public Collection<PotionEffect> getEffects() {
        return effects;
    }

    public Location getLocation() {
        return location;
    }
}
