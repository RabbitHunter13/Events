package com.rabbit13.events.events;

import com.rabbit13.events.objects.Data;
import com.rabbit13.events.objects.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class PlayerJoinContestEvent extends org.bukkit.event.Event {
    private static final HandlerList handlers = new HandlerList();
    private String playerName;
    private Event event;
    private final Data data;
    private boolean canceled;

    public PlayerJoinContestEvent(String playerName, Event event, Data data) {
        this.playerName = playerName;
        this.event = event;
        this.data = data;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerName);
    }

    public Data getPlayerData() {
        return data;
    }

    public Event getEvent() {
        return event;
    }
}
