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
package org.orbisgis.core.events;

import java.beans.EventHandler;
import java.lang.ref.WeakReference;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class EventDispatcherTest extends TestCase {
    public EventDispatcherTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EventDispatcherTest.class );
    }

    
    private WeakReference<Listener> garbageCollectingCheck(EventTargetSample targetSample) {
       //We are the Controller
        //Instanciate the object to watch, the event raiser, it can be the View
        EventSourceSample sourceSample = new EventSourceSample();
        
        
        //Attach a listener to the watch target
        Listener evtListener=EventHandler.create(Listener.class, targetSample, "setPrivateMessage","message");
        sourceSample.getSomethingEventHandler().addListener(targetSample, evtListener);

        //Only for this unit test purpose :
        //Add a local reference, to test if the garbage collector has free the listener
        WeakReference<Listener> refTest=new WeakReference<Listener>(evtListener); 
        
        //Ask the Event source to fire an event
        sourceSample.fireSomething();

        //Remove the listener(s) from the target
        //sourceSample.getListenerRelease().releaseListeners(targetSample);
        
        return refTest;
    }
    public void testGarbageCollectingEvent() {
        
        //Instanciate the object that will do the job, event catcher, the Model
        EventTargetSample targetSample = new EventTargetSample();
        WeakReference<Listener> refTest = garbageCollectingCheck(targetSample);
        //Now we check if the listener has been succesfully destroyed
        System.gc();
        assertTrue(refTest.get()==null);
    }
    public void testSingleLevelEvent() {
        //Instanciate the object to watch, the event raiser, it can be the View
        EventSourceSample sourceSample = new EventSourceSample();
        //Instanciate the object that will do the job, event catcher, the Model, it does't have to add dependency to events package
        EventTargetSample targetSample = new EventTargetSample();
        
        //Attach a listener to the watch target
        sourceSample.getSomethingEventHandler().addListener(targetSample,
                EventHandler.create(Listener.class, targetSample, "setPrivateMessage","message"));
        //This is equivalent to this inner class :
        // new Listener() {
        //    public void onEvent(EventObject obj) {
        //        targetSample.setPrivateMessage((String)obj.getMessage()); 
        //    }
        // }
        //
        //
        
        //OnFire must not be already called
        assertFalse(targetSample.getPrivateMessage().equals(EventSourceSample.secretMessage));
        
        //Ask the Event sourcsourceGarbageCollectingCheck()e to fire an event
        sourceSample.fireSomething();
        
        
        //Test if the event has been succefully propagate to the local method setMessage
        assertTrue(targetSample.getPrivateMessage().equals(EventSourceSample.secretMessage));
        
        //Remove the listener from the target
        sourceSample.getListenerRelease().releaseListeners(targetSample);
        
        //Check fire without listeners
        sourceSample.fireSomething();
    }
    public void testRemoveListener() {
        EventTargetSample targetSample = new EventTargetSample();
        ListenerContainer listOfListener = new ListenerContainer();
        Listener listenerToRemove = EventHandler.create(Listener.class, targetSample, "setFiredRootEvent");
        listOfListener.addListener(this, listenerToRemove);
        assertTrue(listOfListener.removeListener(listenerToRemove));
    }
    public void testMultiLevelEvent() {

        //We are the Controller
        //Instanciate the object to watch, the event raiser, it can be the View
        EventSourceSample sourceSample = new EventSourceSample();
        //Instanciate the object that will do the job, event catcher, the Model
        EventTargetSample targetSample = new EventTargetSample();
        
        // Attach a listener to the root event, when the sub event will be fired,
        // the root event will be fired too
        sourceSample.getRootEventHandler().addListener(targetSample,
                EventHandler.create(Listener.class, targetSample, "setFiredRootEvent"));
        //Attach a listener to the sub event
        sourceSample.getSubEventHandler().addListener(targetSample,
                EventHandler.create(Listener.class, targetSample, "setFiredSubEvent"));

        
        //Test if the event has not be already propagatated to the two listeners
        assertFalse(targetSample.isFiredRootEvent());
        assertFalse(targetSample.isFiredSubEvent());
 
        //Ask the Event source to fire the sub event
        sourceSample.fireSubEvent();
        
        //Test if the event has been succefully propagate to the two listeners
        assertTrue(targetSample.isFiredRootEvent());
        assertTrue(targetSample.isFiredSubEvent());
        
        //Remove the listener from the target
        sourceSample.getListenerRelease().releaseListeners(targetSample);
    }
}