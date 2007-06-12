package org.gdms.driver;

/**
 * @author Fernando Gonz�lez Cort�s
 */
public interface ObjectDriver extends ReadOnlyDriver {
	/**
	 * M�todo invocado al comienzo para abrir el objeto.
	 * 
	 * @throws DriverException
	 *             Si se produce algun error
	 */
	void start() throws DriverException;

	/**
	 * Cierra el objeto sobre el que se estaba accediendo
	 * 
	 * @throws DriverException
	 *             Si se produce alg�n error
	 */
	void stop() throws DriverException;
}