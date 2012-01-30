package org.orbisgis.view.events;
import java.util.UUID;

/**
 * @brief Internal class of EventDispatcher.
 * 
 * Able to remove a listener linked with a source event that has been freed
 * by the garbage collector.
 */
public class EventSourceInstanceFinalizeWitness {
    UUID uniqueId;
    public EventSourceInstanceFinalizeWitness() {
        uniqueId=UUID.randomUUID();
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        EventDispatcher.removeListener(uniqueId);
    }
    
}
