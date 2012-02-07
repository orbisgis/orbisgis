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
 * A PARTICULAR PURPArrayList<ListenerContainer>OSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.base.events;

import java.security.InvalidParameterException;
import java.util.*;
import org.orbisgis.utils.I18N;

/**
 * @brief Accessor to listeners
 * The listener container manager the Add,Remove and Call to listeners
 * corresponding to a single event type
 * The listener container is fully compatible with Java Beans specifications for event managments.
 * This is a refactorisation, leading to remove duplicate code.
 * @warning A listener must have only one method
 */

public class ListenerContainer<EventObjectType> {
    private ListenerContainer upLevelContainer = null; /*!< This container will call the upLevelContainer on a new event */
    private static Map<Object,ArrayList<Listener> > targetToListeners = Collections.synchronizedMap(new HashMap<Object,ArrayList<Listener>>()); /*!< Contain the list of listeners */
    /**
     * Declare the root or single event listener collection.
     */
    public ListenerContainer() {
        
    }
    /**
     * A leaf listener collection
     * This container will call the upLevelContainer on a new event
     * @param upLevelContainer The container to call on a callListeners event.
     */
    public ListenerContainer(ListenerContainer upLevelContainer) {
        this.upLevelContainer = upLevelContainer;
    }
    /**
     * Attach a listener between the event source and the event target
     * @param target The target called by the listener.
     * @param listener The listener object. Created by EventHandler
     */
    public void addListener(Object target,Listener listener) {
        ArrayList<Listener> listenerList;
        if(!targetToListeners.containsKey(target)) {
            listenerList = new ArrayList<Listener>();
            targetToListeners.put(target, listenerList);
        } else {
            listenerList = targetToListeners.get(target);
        }
        listenerList.add(listener);
        ListenerRelease.addListenerTarget(target, this);
    }
    /**
     * Remove the listeners linked with a specific target
     * @warning Called by ListenerRelease. This is more conveniant to simply call ListenerRelease.release
     * @param target The instance of the target object
     */
    public void removeListeners(Object target) {
        if(targetToListeners.containsKey(target)) {
            targetToListeners.remove(target);
        }
    }
    /**
     * Remove all listeners of this container
     */
    public void clearListeners() {
        targetToListeners.clear();
    }
    /**
     * Call all listeners of this collection.
     * @param data Null or the event data specified in the declaration.
     * @throws EventException Throwed by a Listener
     * @throws InvalidParameterException if data is not null and is not an instance of EventObject.
     */
    public void callListeners(EventObjectType data) throws EventException {
        //EventObjectType must inherit from EventObject
        if(data!= null && !(data instanceof EventObject)) { //TODO find a nice way to set this constraint
            throw(new InvalidParameterException(I18N.getString("org.orbisgis.base.events.ListenerContainer.eventDataInvalid")));
        }
        Iterator<ArrayList<Listener>> itListener = targetToListeners.values().iterator();
        ArrayList<Listener> currentList;
        //For all targets
        while(itListener.hasNext()) {
            currentList=itListener.next();
            //For all listeners of the target
            for(Listener listener: currentList) {
                try {
                    if(data!=null) {
                        listener.onEvent((EventObject)data);
                    }else{
                        listener.onEvent(null);                        
                    }
                } catch (ListenerException ex) {
                    //This listener stop the propagation of the event
                    if(!ex.letContinueProcessing()) {
                        throw(new EventException(ex));
                    }
                }
            }
        }
        if(this.upLevelContainer!=null) {
            this.upLevelContainer.callListeners(data);
        }
    }
}
