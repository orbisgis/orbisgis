/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.view.events;

/**
 * @brief Throw when a listener try to stop the propagation of an event.
 */
public class EventException extends Exception {

    /**
     * Creates a new instance of <code>EventException</code> without detail message.
     */
    public EventException() {
    }

    /**
     * Constructs an instance of <code>EventException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public EventException(String msg) {
        super(msg);
    }
}
