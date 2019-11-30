package com.rabbit13.events.objects;

import com.rabbit13.events.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.logging.Level;

public class EventLocation {
    private int x;
    private int y;
    private int z;

    public EventLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EventLocation(Location l) {
        this.x = l.getBlockX();
        this.y = l.getBlockY();
        this.z = l.getBlockZ();

    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public static Location parseBukkitLocation(EventLocation el) {
        World world = Bukkit.getWorld(Objects.requireNonNull(Main.getInstance().getConfig().getString("event-world")));
        if (world != null) {
            return new Location(world, el.x, el.y, el.z);
        }
        else {
            Bukkit.getLogger().log(Level.SEVERE, Main.getPrefix() + " " + Main.getFilMan().getWords().getString("world-not-found")
                    .replace("%world%", Objects.requireNonNull(Main.getInstance().getConfig().getString("event-world"))));
            return null;
        }
    }
}
