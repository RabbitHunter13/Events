package com.rabbit13.events.events;

import com.rabbit13.events.objects.event.Event;

public final class RabPlayerLeaveContestEvent extends PlayerLeaveContestEvent {

    public RabPlayerLeaveContestEvent(String playerName, Event event) {
        super(playerName, event);
    }
}
