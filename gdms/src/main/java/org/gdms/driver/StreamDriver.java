package org.gdms.driver;

import org.gdms.data.stream.StreamSource;

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
     * Gets the array of the prefixes accepted by this driver
     *
     * @return
     */
    String[] getPrefixes();
}
