package com.rabbit13.events.objects;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class RabPlayerData implements PlayerData {
    @Getter
    private final ItemStack[] items;
    @Getter
    private final Collection<PotionEffect> effects;
    @Getter
    private final double maxHP;
    @Getter
    private final float exp;
    @Getter
    private final int level;
    @Getter
    private final Location location;

    public RabPlayerData(ItemStack[] items,
                         Collection<PotionEffect> effects,
                         double maxHP,
                         float exp,
                         int level,
                         Location location) {
        this.items = items;
        this.effects = effects;
        this.maxHP = maxHP;
        this.exp = exp;
        this.level = level;
        this.location = location;
    }


    @Override
    public String toString() {
        assert location.getWorld() != null;
        return "[Items: " + items.length
                + " Effects: " + effects.size()
                + " Loc: " + location.getWorld().getName()
                + "]";
    }

    @Override public ItemStack getHelmet() {
        return items[39];
    }

    @Override public ItemStack getChestplate() {
        return items[38];
    }

    @Override public ItemStack getLeggings() {
        return items[37];
    }

    @Override public ItemStack getBoots() {
        return items[36];
    }

    @Override public ItemStack getOffHand() {
        return items[40];
    }

}
