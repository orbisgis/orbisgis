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

public class EventSourceSample {
    //Secret message
    public final static String secretMessage = "Hello world";
    // ListenerRelease help Source to clear all listeners
    private ListenerRelease listenerRelease=new ListenerRelease();
    //This is a single level event
    private ListenerContainer<EventObjectSample> somethingEventHandler = new ListenerContainer<EventObjectSample>().addReleaseTool(listenerRelease);
    //This events use multi levels calls
    //Listeners ofpublic final root will be called when listeners of subEvent where called.
    private ListenerContainer rootEventHandler = new ListenerContainer().addReleaseTool(listenerRelease);
    private ListenerContainer subEventHandler = new ListenerContainer(rootEventHandler).addReleaseTool(listenerRelease);
    
    public void fireSomething() {
        try {
            somethingEventHandler.callListeners(new EventObjectSample(secretMessage,this));
        } catch (EventException ex) {
            //Do nothing in this case if the event raise a fatal error (a listener throw an error with stoping procedure instruction)
        }
    }    
    public void fireSubEvent() {
        try {
            subEventHandler.callListeners(null);
        } catch (EventException ex) {
            //Do nothing in this case if the event raise a fatal error (a listener throw an error with stoping procedure instruction)
        }
    }

    public ListenerRelease getListenerRelease() {
        return listenerRelease;
    }

    public ListenerContainer getRootEventHandler() {
        return rootEventHandler;
    }

    public ListenerContainer<EventObjectSample> getSomethingEventHandler() {
        return somethingEventHandler;
    }

    public ListenerContainer getSubEventHandler() {
        return subEventHandler;
    }
    
}
