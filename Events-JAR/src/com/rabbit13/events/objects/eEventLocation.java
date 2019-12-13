package com.rabbit13.events.objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class eEventLocation extends Location implements EventLocation {

    public eEventLocation(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public eEventLocation(@Nullable World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        assert getWorld() != null;
        return getWorld().getName() + "," + getX() + "," + getY() + "," + getZ() + "," + getYaw() + "," + getPitch();
    }
}
