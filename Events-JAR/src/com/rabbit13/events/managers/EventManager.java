package com.rabbit13.events.managers;

import com.rabbit13.events.objects.eEvent;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private static eEvent activeEvent;
    private static Map<String, eEvent> events = new HashMap<>();

    public static eEvent getActiveEvent() {
        return activeEvent;
    }

    public static void setActiveEvent(String activeEvent) {
        if (activeEvent != null) {
            EventManager.activeEvent = getEventByName(activeEvent);
        }
        else {
            EventManager.activeEvent = null;
        }
    }

    public static eEvent getEventByName(String name) {
        return events.get(name);
    }

    public static Map<String, eEvent> getEvents() {
        return events;
    }
}
