package org.gdms.data;

public class ClosedDataSourceException extends RuntimeException {

    /**
     * 
     */
    public ClosedDataSourceException() {
        super();
        
    }

    /**
     * @param message
     * @param cause
     */
    public ClosedDataSourceException(String message, Throwable cause) {
        super(message, cause);
        
    }

    /**
     * @param message
     */
    public ClosedDataSourceException(String message) {
        super(message);
        
    }

    /**
     * @param cause
     */
    public ClosedDataSourceException(Throwable cause) {
        super(cause);
        
    }

}
