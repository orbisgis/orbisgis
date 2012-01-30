/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.view.events;

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

    
    private WeakReference<Listener> garbageCollectingCheck() {
       //We are the Controller
        //Instanciate the object to watch, the event raiser, it can be the View
        EventSourceSample sourceSample = new EventSourceSample();
        //Instanciate the object that will do the job, event catcher, the Model
        EventTargetSample targetSample = new EventTargetSample();
        
        
        //Attach a listener to the watch target
        //local onFire method request the event name, then we extract
        //the event name from EventData by specify the string "getEventName"
        //The listener will no longer exist when testSample instance will be garbage collected
        Listener evtListener=EventHandler.create(Listener.class, targetSample, "onFire","eventName");
        EventDispatcher.addListener(targetSample,evtListener, EventSourceSample.SOMETHING_EVENT, sourceSample);
        
        //Only for this unit test purpose :
        //Add a local reference, to test if the garbage collector has free the listener
        WeakReference<Listener> refTest=new WeakReference<Listener>(evtListener); 
        
        //Ask the Event source to fire an event
        sourceSample.fireSomething();

        //Remove the listener from the target
        EventDispatcher.removeListeners(targetSample);
        
        return refTest;
    }
    public void testGarbageCollectingEvent() {
        WeakReference<Listener> refTest = garbageCollectingCheck();
        //Now we check if the listener has been succesfully destroyed
        System.gc();
        assertTrue(refTest.get()==null);
    }
    public void testSingleLevelEvent() {
        //Instanciate the object to watch, the event raiser, it can be the View
        EventSourceSample sourceSample = new EventSourceSample();
        //Instanciate the object that will do the job, event catcher, the Model
        EventTargetSample targetSample = new EventTargetSample();
        
        //Attach a listener to the watch target
        //local onFire method request the event name, then we extract
        //the event name from EventData by specify the string "getEventName"
        //The listener will no longer exist when testSample instance will be garbage collected
        EventDispatcher.addListener(targetSample,
                EventHandler.create(Listener.class, targetSample, "onFire","eventName"),
                EventSourceSample.SOMETHING_EVENT, sourceSample);
                
        
        //OnFire must not be already called
        assertFalse(targetSample.isFiredEventWitness());
        
        //Ask the Event source to fire an event
        sourceSample.fireSomething();
        
        
        //Test if the event has been succefully propagate to the local method onFire
        assertTrue(targetSample.isFiredEventWitness());
        
        //Remove the listener from the target
        EventDispatcher.removeListeners(targetSample);
    }
    
    public void testMultiLevelEvent() {

        //We are the Controller
        //Instanciate the object to watch, the event raiser, it can be the View
        EventSourceSample sourceSample = new EventSourceSample();
        //Instanciate the object that will do the job, event catcher, the Model
        EventTargetSample targetSample = new EventTargetSample();
        
        // Attach a listener to the root event, when the sub event will be fired,
        // the root event will be fired too
        EventDispatcher.addListener(targetSample,
                EventHandler.create(Listener.class, targetSample, "setFiredRootEvent")
                , EventSourceSample.ROOT_EVENT, sourceSample);
        //Attach a listener to the sub event
       EventDispatcher.addListener(targetSample,
                EventHandler.create(Listener.class, targetSample, "setFiredSubEvent")
                , EventSourceSample.SUB_EVENT, sourceSample);
        
        //Test if the event has not be already propagatated to the two listeners
        assertFalse(targetSample.isFiredRootEvent());
        assertFalse(targetSample.isFiredSubEvent());
 
        //Ask the Event source to fire the sub event
        sourceSample.fireSubEvent();
        
        //Test if the event has been succefully propagate to the two listeners
        assertTrue(targetSample.isFiredRootEvent());
        assertTrue(targetSample.isFiredSubEvent());
        
        //Remove the listener from the target
        EventDispatcher.removeListeners(targetSample);        
    }
}