package com.rabbit13.events.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class PlayerDeathAtContestEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String playerName;
    private String contestName;

    public PlayerDeathAtContestEvent(String playerName, String contestName) {
        this.playerName = playerName;
        this.contestName = contestName;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getContestName() {
        return contestName;
    }
}
