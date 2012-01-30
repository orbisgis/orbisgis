package org.orbisgis.view.events;

import java.util.EventObject;
/**
 * @brief Base class for event data.
 * @warning Use java beans particular convention.
 * @link http://en.wikipedia.org/wiki/JavaBean
 */
public class EventData extends EventObject {
    EventName eventName;

    /**
     * Unique constructor, with minimal parameters
     * @param eventName The event description, statically stored by the inherited class of EventSource
     * @param o The instance of EventSource that fire this event
     */
    public EventData(EventName eventName, EventSource o) {
        super(o);
        this.eventName = eventName;
    }
    /**
     * Use "eventName" in the EventHandler create method if you want to pass it in your target object
     * @return The event name.
     */
    public EventName getEventName() {
        return eventName;
    }

    
}
