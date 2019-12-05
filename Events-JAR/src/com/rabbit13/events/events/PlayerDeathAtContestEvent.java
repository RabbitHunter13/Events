package com.rabbit13.events.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.rabbit13.events.objects.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class PlayerDeathAtContestEvent extends org.bukkit.event.Event {
    private static final HandlerList handlers = new HandlerList();
    private String playerName;
    private Event event;

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
