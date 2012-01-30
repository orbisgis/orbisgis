/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.view.events;

/**
 * Event target sample, the target is an object that catch an Event and process it.
 * The target doesn't have to include the Source class.
 * The target can be the model, not dependencies with source or controler.
 */
public class EventTargetSample {
    private boolean firedEvent = false;
    private boolean firedRootEvent = false;
    private boolean firedSubEvent = false;
    public boolean isFiredEventWitness() {
        return firedEvent;
    }

    public boolean isFiredRootEvent() {
        return firedRootEvent;
    }
    /**
     * This method is linked with the listener
     */
    public void setFiredRootEvent() {
        this.firedRootEvent = true;
    }

    public boolean isFiredSubEvent() {
        return firedSubEvent;
    }

    /**
     * This method is linked with the listener
     */
    public void setFiredSubEvent() {
        this.firedSubEvent = true;
    }
    
    public void onFire(EventName evtName) {
        firedEvent = true;
    }
}
