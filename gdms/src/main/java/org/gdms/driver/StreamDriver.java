package org.gdms.driver;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Image;
import org.gdms.data.stream.StreamSource;
import org.gvsig.remoteClient.wms.ICancellable;

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
     * Sets the file associated with this driver. Faut-il créer un objet url qui
     * s'assure de la validité et de l'accessibilité d'une url ?
     *
     * @param url a valid url.
     * @throws DriverException
     */
    void setURL(String url) throws DriverException;

    /**
     * Checks if the driver is currently open.
     *
     * @return true if the file is open, false otherwise.
     */
    boolean isOpen();
    
    /**
     * Get map from server
     * 
     * @param width
     * @param height
     * @param extent
     * @param cancel
     * @return
     * @throws DriverException 
     */
    public Image getMap(int width, int height, Envelope extent, ICancellable cancel) throws DriverException;
    

    /**
     * Gets the array of the prefixes accepted by this driver
     *
     * @return
     */
    String[] getPrefixes();
}
