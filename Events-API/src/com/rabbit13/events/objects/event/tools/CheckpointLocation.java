package com.rabbit13.events.objects.event.tools;

import org.bukkit.Location;

public interface CheckpointLocation {
    Location getCheckpointLocation();

    void setCheckpointLocation(Location checkpointLocation);

    Location getSavedLocation();

    void setSavedLocation(Location savedLocation);

    @Override
    String toString();
}
