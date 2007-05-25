package org.gdms.data;

/**
 * Used to keep a InternalDataSource from being closed to many times
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class OpenCloseCounter {

    private int counter = 0;
    private InternalDataSource ds;

    public OpenCloseCounter(InternalDataSource ds) {
        this.ds = ds;
    }

    /**
     * Returns true if the InternalDataSource has to open the driver or it's already opened
     *
     * @return
     */
    public boolean start() {
        counter++;

        if (counter == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the InternalDataSource has to close the driver or must be kept open until, at
     * least, next call to close.
     *
     * @return
     */
    public boolean stop() {
        counter--;

        if (counter == 0) {
            return true;
        } else if (counter < 0){
            counter = 0;
            throw new AlreadyClosedException("InternalDataSource closed too many times: " + ds.getName());
        } else {
            return false;
        }
    }

    /**
     * Returns true if the last call to stop will
     * return true
     *
     * @return
     */
    public boolean nextStopCloses() {
    	return counter == 1;
    }

    /**
     * Returns true if the datasource is opened and false if it's closed
     *
     * @return
     */
    public boolean isOpen() {
    	return counter > 0;
    }
}
