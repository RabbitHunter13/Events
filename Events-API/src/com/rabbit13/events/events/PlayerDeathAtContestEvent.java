package com.rabbit13.events.events;

import com.rabbit13.events.objects.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


public abstract class PlayerDeathAtContestEvent extends org.bukkit.event.Event {
    private static final HandlerList handlers = new HandlerList();
    private final String playerName;
    private final Event event;

    public PlayerDeathAtContestEvent(String playerName, Event event) {
        this.playerName = playerName;
        this.event = event;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerName);
    }

    public Event getEvent() {
        return event;
    }
}
