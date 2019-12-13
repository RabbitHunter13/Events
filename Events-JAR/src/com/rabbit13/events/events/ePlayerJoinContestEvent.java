package com.rabbit13.events.events;

import com.rabbit13.events.objects.Data;
import com.rabbit13.events.objects.Event;

public final class ePlayerJoinContestEvent extends PlayerJoinContestEvent {

    public ePlayerJoinContestEvent(String playerName, Event event, Data data) {
        super(playerName,event, data);
    }
}
