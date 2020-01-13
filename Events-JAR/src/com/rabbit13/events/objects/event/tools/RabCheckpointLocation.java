package com.rabbit13.events.objects.event.tools;

import org.bukkit.Location;

public class RabCheckpointLocation implements CheckpointLocation {
    Location checkpointLocation;
    Location savedLocation;

    public RabCheckpointLocation(Location checkpointLocation, Location savedLocation) {
        this.checkpointLocation = checkpointLocation;
        this.savedLocation = savedLocation;
    }

    @Override
    public String toString() {
        return "&5["
                + this.getCheckpointLocation().getBlockX() + ", "
                + this.getCheckpointLocation().getBlockY() + ", "
                + this.getCheckpointLocation().getBlockZ() + "]";
    }

    public Location getCheckpointLocation() {
        return checkpointLocation;
    }

    public void setCheckpointLocation(Location checkpointLocation) {
        this.checkpointLocation = checkpointLocation;
    }

    public Location getSavedLocation() {
        return savedLocation;
    }

    public void setSavedLocation(Location savedLocation) {
        this.savedLocation = savedLocation;
    }
}
