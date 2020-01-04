package com.rabbit13.events.objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class eEventLocation extends Location implements EventLocation {
    private Location location;
    public eEventLocation(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.location = location;
    }

    public eEventLocation(@Nullable World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
        this.location = new Location(world,x,y,z,yaw,pitch);
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        assert getWorld() != null;
        return getWorld().getName() + "," + getX() + "," + getY() + "," + getZ() + "," + getYaw() + "," + getPitch();
    }
}
