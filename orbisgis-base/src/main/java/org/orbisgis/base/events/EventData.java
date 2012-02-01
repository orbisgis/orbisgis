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
