package org.gdms.data.persistence;

/**
 * Thrown when an error happens obtaining the memento or restoring the
 * DataSource state from a memento
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class MementoException extends Exception {
    /**
     *  
     */
    public MementoException() {
        super();

        // TODO Auto-generated constructor stub
    }

    /**
     * DOCUMENT ME!
     * 
     * @param message
     */
    public MementoException(String message) {
        super(message);

        // TODO Auto-generated constructor stub
    }

    /**
     * DOCUMENT ME!
     * 
     * @param cause
     */
    public MementoException(Throwable cause) {
        super(cause);

        // TODO Auto-generated constructor stub
    }

    /**
     * DOCUMENT ME!
     * 
     * @param message
     * @param cause
     */
    public MementoException(String message, Throwable cause) {
        super(message, cause);

        // TODO Auto-generated constructor stub
    }
}
