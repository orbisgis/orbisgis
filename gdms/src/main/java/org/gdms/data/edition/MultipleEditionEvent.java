package org.gdms.data.edition;

import java.util.ArrayList;

/**
 * Contains zero or more EditionEvents 
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class MultipleEditionEvent {
    private ArrayList<EditionEvent> events = new ArrayList<EditionEvent>();
    
    public void addEvent(EditionEvent event) {
        events.add(event);
    }
    
    public EditionEvent[] getEvents(){
        return events.toArray(new EditionEvent[0]);
    }

}
