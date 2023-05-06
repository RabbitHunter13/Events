package com.rabbit13.events.objects.event.tools;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public final class RabCheckpointLocation implements CheckpointLocation {
    @Getter @Setter
    private Location checkpointLocation;
    @Getter @Setter
    private Location savedLocation;

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

}
