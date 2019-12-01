package com.rabbit13.events.objects;

import com.rabbit13.events.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.logging.Level;

public class EventLocation extends Location {

    public EventLocation(double x, double y, double z, float yaw, float pitch) {
        super(Bukkit.getWorld(Objects.requireNonNull(Main.getInstance().getConfig().getString("event-world"))), x, y, z, yaw, pitch);
    }

    public EventLocation(Location l) {
        super(Bukkit.getWorld(Objects.requireNonNull(Main.getInstance().getConfig().getString("event-world"))), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
    }

    @Override
    public String toString() {
        return getX() + "," + getY() + "," + getZ() + "," + getYaw() + "," + getPitch();
    }

    public static Location parseBukkitLocation(double x, double y, double z) {
        World world = Bukkit.getWorld(Objects.requireNonNull(Main.getInstance().getConfig().getString("event-world")));
        if (world != null) {
            return new Location(world, x, y, z);
        }
        else {
            Bukkit.getLogger().log(Level.SEVERE, Main.getPrefix() + " " + Main.getFilMan().getWords().getString("world-not-found")
                    .replace("%world%", Objects.requireNonNull(Main.getInstance().getConfig().getString("event-world"))));
            return null;
        }
    }
}
