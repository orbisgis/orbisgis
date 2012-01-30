package org.orbisgis.view.events;

/**
 * @brief Exception raised by the OnEvent method of listeners
 * This exception let other listener to manage the event if continueProcessing is True
 */
public class ListenerException extends Exception {
    boolean continueProcessing;
    /**
     * Creates a new instance of <code>ListenerException</code> without detail message.
     */
    public ListenerException(boolean continueProcessing) {
        this.continueProcessing = continueProcessing;
    }

    /**
     * Constructs an instance of <code>ListenerException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ListenerException(boolean continueProcessing,String msg) {
        super(msg);
        this.continueProcessing = continueProcessing;
    }
    
    boolean letContinueProcessing() {
        return continueProcessing;
    }
}
