package org.gdms.data;

/**
 * Thrown if attempting to close an already closed DataSource
 * 
 * @author root
 *
 */
public class AlreadyClosedException extends RuntimeException {

    /**
     * @param message
     */
    public AlreadyClosedException(String message) {
        super(message);
        
    }

}
