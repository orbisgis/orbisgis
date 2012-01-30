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
package org.orbisgis.view.events;

/**
 * @brief Event name describe the event by a hierarchical way
 * The final level (rightmost) is the most precise description
 * Ex: mainclass/view/button/hide
 * New eventName must be specified as a static variable inside the event source class. In that case, the modification of the event name will not break other class that reference to this event name.
 */
public class EventName {
    private String chain="";
    private final static String sep="/";
    /**
     * An empty event name
     */
    public EventName() {
        
    }
    /**
     * A single level name
     * @param item Event description
     */
    public EventName(String item) {
        push(item);
    }
    public EventName(EventName parent,String eventItem) {
        chain=parent.toString();
        this.push(eventItem);
    }
    public EventName(EventName parent,EventName eventItem) {
        chain=parent.toString();
        for(String item : eventItem.getEventItems()) {
            this.push(item);            
        }
    }
    /**
     * Add a sub-level to this event
     * @param eventItem A string corresponding to a more precise description that the last one
     * @return Instance of this EventName.
     */
    final public EventName push(String eventItem) {
        //eventItem.replaceAll(sep, "\\"+sep); //Escape special char
        String lowerEvent=eventItem.toLowerCase();
        if(!chain.isEmpty()) {
            chain+=sep;
        }
        chain+=lowerEvent;
        return this;
    }
    private String[] getEventItems() {
        return chain.split(sep);
    }
    /**
     * @return Return all sub compounds of this particular event. For a.b.c.d return [a.b.c,a.b,a]
     */
    public EventName[] getLeftEvents() {
        EventName[] allitems = getDecomposedEvent();
        if(allitems.length>1) {
            EventName[] parents = new EventName[allitems.length-1];
            System.arraycopy(allitems, 1, parents, 0, allitems.length-1);
            return parents;
        } else {
            return new EventName[] {};
        }
    }
    /**
     * @return Return all compounds of this particular event. For a.b.c.d return [a.b.c.d,a.b.c,a.b,a]
     */
    public EventName[] getDecomposedEvent() {
        String[] allItems=getEventItems();
        EventName[] parents = new EventName[allItems.length];
        for(int parentId=0;parentId < parents.length ; parentId++) {
            EventName parent=new EventName();
            //Build a new EventName with a shorter name
            for(int itemIndex=0;itemIndex <= parentId ; itemIndex++) {
                parent.push(allItems[itemIndex]);
            }
            parents[parents.length-parentId-1]=parent;
        }
        return parents;        
    }
    /**
     * @return The string form of this event description 
     */
    @Override
    public String toString() {
        return chain;
    }

    @Override
    public int hashCode() {
        return chain.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventName other = (EventName) obj;
        if (!this.chain.equals(other.toString())) {
            return false;
        }
        return true;
    }
}
