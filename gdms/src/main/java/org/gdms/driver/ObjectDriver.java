package org.gdms.driver;

import org.gdms.data.DataSource;



/**
 * @author Fernando Gonz�lez Cort�s
 */
public interface ObjectDriver extends GDBMSDriver, ReadAccess {
    /**
     * M�todo invocado al comienzo para abrir el objeto.
     *
     * @throws DriverException Si se produce algun error
     */
    void start() throws DriverException;

    /**
     * Cierra el objeto sobre el que se estaba accediendo
     *
     * @throws DriverException Si se produce alg�n error
     */
    void stop() throws DriverException;

    /**
     * Returns true if the specified field is read only
     *
     * @param i
     * @return
     */
    public boolean isReadOnly(int i);

    /**
     * Returns a string array with the names of all the fields that are
     * primary key
     *
     * @return
     */
    public String[] getPrimaryKeys();

	/**
	 * Writes the content in the DataWare to the specified file
	 *
	 * @param dataWare DataWare with the contents
	 */
	void write(DataSource dataWare) throws DriverException;
}
