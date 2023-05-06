package com.rabbit13.events.managers;

import com.rabbit13.events.objects.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private static Event activeEvent;
    private static final Map<String, Event> events = new HashMap<>();

    @Nullable
    public static Event getActiveEvent() {
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

    public static Event getEventByName(String name) {
        return events.get(name);
    }

    public static Map<String, Event> getEvents() {
        return events;
    }
}
