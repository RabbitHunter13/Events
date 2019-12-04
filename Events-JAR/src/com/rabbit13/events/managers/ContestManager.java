package com.rabbit13.events.managers;

import com.rabbit13.events.objects.Event;

import java.util.HashMap;
import java.util.Map;

public class ContestManager {
    private static Event activeEvent;
    private static Map<String, Event> events = new HashMap<>();

    public static Event getActiveEvent() {
        return activeEvent;
    }

    public static void setActiveEvent(String activeEvent) {
        if (activeEvent != null) {
            ContestManager.activeEvent = getEventByName(activeEvent);
        }
        else {
            ContestManager.activeEvent = null;
        }
    }

    public static Event getEventByName(String name) {
        return events.get(name);
    }

    public static Map<String, Event> getEvents() {
        return events;
    }
}
