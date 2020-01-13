package com.rabbit13.events.objects.event.tools;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class RabEventLocation extends Location implements EventLocation {
    @Getter
    private Location location;

    public RabEventLocation(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.location = location;
    }

    public RabEventLocation(@Nullable World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
        this.location = new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        assert getWorld() != null;
        return getWorld().getName() + "," + getX() + "," + getY() + "," + getZ() + "," + getYaw() + "," + getPitch();
    }
}
