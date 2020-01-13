package com.rabbit13.events.objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public interface PlayerData {

    @Override
    String toString();

    ItemStack getHelmet();

    ItemStack getChestplate();

    ItemStack getLeggings();

    ItemStack getBoots();

    ItemStack getOffHand();

    ItemStack[] getItems();

    Collection<PotionEffect> getEffects();

    double getMaxHP();

    float getExp();

    int getLevel();

    Location getLocation();
}
