package org.orbisgis.view.events;

public class EventSourceSample implements EventSource {
    //This is a single level event
    public final static EventName SOMETHING_EVENT = new EventName("eventofsource");
    //This events use multi level event
    public final static EventName ROOT_EVENT = new EventName("theRootEvent");
    public final static EventName SUB_EVENT = new EventName(ROOT_EVENT,"SpecificSubEvent");
    void fireSomething() {
        try {
            EventDispatcher.onEvent(new EventData(SOMETHING_EVENT,this));
        } catch (EventException ex) {
            //Do nothing in this case if the event raise a fatal error (a listener throw an error with stoping procedure instruction)
        }
    }    
    void fireSubEvent() {
        try {
            EventDispatcher.onEvent(new EventData(SUB_EVENT,this));
        } catch (EventException ex) {
            //Do nothing in this case if the event raise a fatal error (a listener throw an error with stoping procedure instruction)
        }
    }
}
