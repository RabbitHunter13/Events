package com.rabbit13.events.events;


import com.rabbit13.events.objects.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class PlayerLeaveContestEvent extends org.bukkit.event.Event {
    private static final HandlerList handlers = new HandlerList();
    private final String playerName;
    private final Event event;
    private boolean canceled;

    public PlayerLeaveContestEvent(String playerName, Event event) {
        this.playerName = playerName;
        this.event = event;
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

    public String getPlayerName() {
        return playerName;
    }

    public Event getEvent() {
        return event;
    }
}
