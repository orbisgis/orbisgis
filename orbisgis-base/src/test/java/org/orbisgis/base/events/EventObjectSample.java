package org.orbisgis.base.events;

import java.util.EventObject;


public class EventObjectSample extends EventObject {
    String message;

    public EventObjectSample(String message, Object o) {
        super(o);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    
}
