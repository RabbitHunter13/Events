package com.rabbit13.events.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class PlayerJoinContestEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String playerName;
    private String contestName;
    private boolean canceled;

    public PlayerJoinContestEvent(String playerName, String contestName) {
        this.playerName = playerName;
        this.contestName = contestName;
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

    public String getContestName() {
        return contestName;
    }
}
