/*
 * Created on 17-oct-2004
 */
package org.gdms.driver;

/**
 * Excepci�n lanzada cuando un driver no pudo resolver la petici�n que se le
 * realiz�. En un driver de fichero tendr� como causa una IOException, en un
 * driver de DB tendr� una SQLException, ...
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class DriverException extends Exception {
	/**
	 * Creates a new StartException object.
	 */
	public DriverException() {
		super();
	}

	/**
	 * Creates a new DriverException object.
	 *
	 * @param arg0
	 */
	public DriverException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new DriverException object.
	 *
	 * @param arg0
	 * @param arg1
	 */
	public DriverException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Creates a new DriverException object.
	 *
	 * @param arg0
	 */
	public DriverException(Throwable arg0) {
		super(arg0);
	}
}
