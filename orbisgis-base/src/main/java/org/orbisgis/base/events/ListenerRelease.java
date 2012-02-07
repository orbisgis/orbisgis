/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
/**
 * @package org.orbisgis.view.events
 * @brief Helper to quickly release Listeners from their target
 */
package org.orbisgis.base.events;

import java.util.*;
/**
 * @brief manage the processing of releasing all listeners attached to a specific target
 * 
 * Ex :
 *
 * An eventListener is linked with at least 2 objects,
 * an EventSource and an EventTarget. The listener will be removed automatically
 * if the associated EventSource no longer exist. 
 * However the event target is hard linked with a listener, then an event target
 * would remove listener.
 * 
 * To declare an new event :
 *   The class that own the event must store in final static the EventName related to it and an ListenerContainer for each EventName.
 *   The class that will create listeners and be called by them should create the listener by this way :
 *   code : EventHandler.create(Listener.class, targetInstance, "methodOfTarget","the property name of EventData to pass into the methodOfTarget as parameter");
 *   EventHandler will return a new listener, this listener must be pushed into the ListenerContainer :
 *   eventSource.anEvent.addListener(target,thelistener)
 *   code :EventDispatcher.addListener(targetInstance,theNewListener, EventSourceClass.SOMETHING_EVENT, sourceInstance);
 *   The target object must free the listeners by using this method :
 *   code: ListenerRelease.removeListeners(targetInstance);
 */
public class ListenerRelease {
    //Link between the event name, event source and listener ID
    //Used by OnEvent
    //Link between target and listeners
    //Used by target to no longer be called by listeners, and guarentee 
    //that the target will be freed by the garbage collector
    private static WeakHashMap<Object,ListenerContainer> targetToListenerContainer = new WeakHashMap<Object,ListenerContainer>();

    /**
     * Add a listener to an Object/Event
     * @param target The target associated with the listener !=watchedInstance
     * @param listener Object that inherit from Listener class
     * @param evtName The event description
     * @param watchedInstance Null or a specific instance, this object must not be strong referenced by the listener, otherwise listener could never be automatically removed.
     * @warning Do not forget to call the method  
     * EventDispatcher.removeListeners(target); 
     * to remove listeners linked with your action class, 
     * to let the garbage collector free your target instance.
     * @return The unique ID of the new event listener
     */
    
    public static synchronized void addListenerTarget(Object target,ListenerContainer container) {
        targetToListenerContainer.put(target, container);
    }
    /**
     * When a target is no longer used, the listeners created by it must be removed.
     */
    public static synchronized void releaseListeners(Object target) {
        //Retrieve all UUID linked with this target
        ListenerContainer theContainer = targetToListenerContainer.get(target);
        if(theContainer!=null) {
            theContainer.removeListeners(target);
            targetToListenerContainer.remove(target);
        }
    }
}
