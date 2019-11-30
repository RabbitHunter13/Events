package com.rabbit13.events.managers;

import com.rabbit13.events.objects.Event;

import java.util.HashMap;
import java.util.Map;

public final class EventManager {
    private static Map<String, Event> events = new HashMap<>();

    public static Event getEventByName(String name) {
        return events.get(name);
    }

    public static Map<String, Event> getEvents() {
        return events;
    }
}
