package com.rabbit13.events.events;

import com.rabbit13.events.objects.eEvent;

public final class ePlayerDeathAtContestEvent extends PlayerDeathAtContestEvent {

    public ePlayerDeathAtContestEvent(String playerName, eEvent event) {
        super(playerName, event);
    }
}
