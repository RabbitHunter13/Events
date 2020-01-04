package com.rabbit13.events.objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public interface Data {

    @Override
    String toString();

    ItemStack[] getItems();

    Collection<PotionEffect> getEffects();

    Location getLocation();
}
