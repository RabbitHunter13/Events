package com.rabbit13.events.objects;


import org.bukkit.Location;

public interface EventLocation {

    Location getLocation();

    @Override
    String toString();
}
