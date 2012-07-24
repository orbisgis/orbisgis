package org.gdms.driver;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Image;
import org.gdms.data.stream.StreamSource;
import org.gvsig.remoteClient.wms.ICancellable;
import org.orbisgis.progress.ProgressMonitor;

/**
 *
 * @author doriangoepp
 */
public interface StreamDriver extends Driver {

    /**
     * Opens the stream.
     * <code>seturl</code> must have been called before calling
     * <code>open</code>.
     *
     * @throws DriverException
     */
    void open(StreamSource streamSource) throws DriverException;

    /**
     * Closes the stream being accessed
     *
     *
     * @throws DriverException
     */
    void close() throws DriverException;

    /**
     * Checks if the driver is currently open.
     *
     * @return true if the file is open, false otherwise.
     */
    boolean isOpen();
    
    /**
     * Gets an image from the stream.
     * 
     * @param width the width
     * @param height the height
     * @param extent the extend required
     * @param pm
     * @return the resulting image
     * @throws DriverException 
     */
    public Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws DriverException;
    

    /**
     * Gets the array of the stream types accepted by this driver.
     *
     * @return
     */
    String[] getStreamTypes();
}
