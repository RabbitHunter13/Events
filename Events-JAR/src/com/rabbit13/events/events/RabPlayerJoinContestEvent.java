package com.rabbit13.events.events;

import com.rabbit13.events.objects.PlayerData;
import com.rabbit13.events.objects.event.Event;

public final class RabPlayerJoinContestEvent extends PlayerJoinContestEvent {

    public RabPlayerJoinContestEvent(String playerName, Event event, PlayerData data) {
        super(playerName, event, data);
    }
}
