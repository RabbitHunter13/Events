package com.rabbit13.events.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {
    private static final HashMap<Player, Location> joinedEvent = new HashMap<>();

    public static HashMap<Player, Location> getJoinedEvent() {
        return joinedEvent;
    }
}
