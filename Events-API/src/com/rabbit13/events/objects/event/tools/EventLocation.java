package com.rabbit13.events.objects.event.tools;


import org.bukkit.Location;

public interface EventLocation {

    Location getLocation();

    @Override
    String toString();
}
