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
package org.orbisgis.base.events;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.WeakHashMap;
import org.orbisgis.utils.I18N;

/**
 * @brief Accessor to listeners
 * The listener container manager the Add,Remove and Call to listeners
 * corresponding to a single EventName
 * The listener container is fully compatible with Java Beans specifications.
 * A listener must have only one method
 * This is a refactorisation, leading to remove duplicate code.
 */

public class ListenerContainer<EventObjectType> {
    private ListenerContainer upLevelContainer = null;
    private static WeakHashMap<Object,ArrayList<Listener> > targetToListeners = new WeakHashMap<Object,ArrayList<Listener>>();
    public ListenerContainer() {
        
    }
    public ListenerContainer(ListenerContainer upLevelContainer) {
        this.upLevelContainer = upLevelContainer;
    }
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
    
    public void removeListeners(Object target) {
        if(targetToListeners.containsKey(target)) {
            targetToListeners.remove(target);
        }
    }
    public void callListeners(EventObjectType data) throws EventException {
        //EventObjectType must inherit from EventObject
        if(data!= null && !(data instanceof EventObject)) { //TODO find a nice way to set this constraint
            throw(new InvalidParameterException(I18N.getString("org.orbisgis.base.events.ListenerContainer.eventDataInvalid")));
        }
        Iterator<ArrayList<Listener>> itListener = targetToListeners.values().iterator();
        ArrayList<Listener> currentList;
        while(itListener.hasNext()) {
            currentList=itListener.next();
            for(Listener listener: currentList) {
                try {
                    if(data!=null) {
                        listener.onEvent((EventObject)data);
                    }else{
                        listener.onEvent(null);                        
                    }
                } catch (ListenerException ex) {
                    throw(new EventException(ex));
                }
            }
        }
        if(this.upLevelContainer!=null) {
            this.upLevelContainer.callListeners(data);
        }
    }
}
