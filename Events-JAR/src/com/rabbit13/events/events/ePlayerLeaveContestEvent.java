package com.rabbit13.events.events;

import com.rabbit13.events.objects.Event;

public final class ePlayerLeaveContestEvent extends PlayerLeaveContestEvent {

    public ePlayerLeaveContestEvent(String playerName, Event event) {
        super(playerName, event);
    }
}
