package org.orbisgis.view.events;

import java.util.EventListener;
/**
 * @brief Interface of all listeners
 * All class that implements this interface can be listened for events through EventDispatcher
 */
public interface Listener extends EventListener {
    /**
     * @brief The event has been fired
     * Use java.beans.EventHandler.create to make a listener that will directly
     * link from the source to the target method.
     * You can also overload this method to call your related class method,
     * try to not write too much code in your functor.
     * @param evtName The event name, it can be the same as the event linked with this event but it can be also a more specific event.
     * @param evtData The event information, like the instance that fired the event.
     */
    public void onEvent(EventData evtData) throws ListenerException;
    
    
}
