package com.rabbit13.events.events;

import com.rabbit13.events.objects.event.Event;

public final class RabPlayerDeathAtContestEvent extends PlayerDeathAtContestEvent {

    public RabPlayerDeathAtContestEvent(String playerName, Event event) {
        super(playerName, event);
    }
}
