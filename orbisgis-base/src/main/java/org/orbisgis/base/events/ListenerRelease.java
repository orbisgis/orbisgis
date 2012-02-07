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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.orbisgis.base.events.internals.ListenerContainers;
/**
 * @brief Release all listeners attached to a specific target in one call.
 * 
 * When a target will no longer receive listener calls, 
 * ListenerRelease.releaseListeners(target) must be called to avoid 
 * java memory leaks and let the garbage collector to collect the target.
 */
public class ListenerRelease {
    private final static Map<Object,ListenerContainers> targetToListenerContainer = Collections.synchronizedMap(new HashMap<Object,ListenerContainers>());

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
    /**
     * Add a container to a target
     * @warning Must be called by ListenerContainer only
     * @param target The object target
     * @param container The container instance
     */
    public static synchronized void addListenerTarget(Object target,ListenerContainer container) {
        ListenerContainers containersList;
        if(!targetToListenerContainer.containsKey(target)) {
            containersList = new ListenerContainers();
            targetToListenerContainer.put(target, containersList);            
        } else {
            containersList = targetToListenerContainer.get(target);
        }
        containersList.add(container);
    }
    /**
     * When a target is no longer used, the listeners created by it must be removed.
     */
    public static synchronized void releaseListeners(Object target) {
        ListenerContainers containersList;
        if(targetToListenerContainer.containsKey(target)) {
            containersList = targetToListenerContainer.get(target);
            //Retrieve all UUID linked with this target
            for(ListenerContainer container : containersList) {
                    container.removeListeners(target);
            }
        }
        targetToListenerContainer.remove(target);
    }
}
