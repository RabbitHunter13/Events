package com.rabbit13.events.objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class eData implements Data {
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final ItemStack[] items;
    private final Collection<PotionEffect> effects;
    private final Location location;

    public eData(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack[] items, Collection<PotionEffect> effects, Location location) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.items = items;
        this.effects = effects;
        this.location = location;
    }

    @Override
    public String toString() {
        assert location.getWorld() != null;
        return "[H: " + (helmet != null)
                + " Ch: " + (chestplate != null)
                + " L: " + (leggings != null)
                + " B: " + (boots != null)
                + " Items: " + items.length
                + " Effects: " + effects.size()
                + " Loc: " + location.getWorld().getName()
                + "]";
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
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
