package com.rabbit13.events.objects;

/**
 * Indicates a place within 2 Location points
 * For future sets, now unused
 */
public class Volume {
    EventLocation pos1;
    EventLocation pos2;

    public Volume(EventLocation pos1, EventLocation pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    /**
     * Checks if position is within this place.
     * @param position Location to check
     * @return true, if {@link EventLocation } is within place, otherwise false
     */
    public boolean checkLocation(EventLocation position) {
        return true;
    }
}
