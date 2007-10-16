/*
 * Created on 17-oct-2004
 */
package org.gdms.driver;

/**
 * Exception thrown when the operation with the DataSource cannot be done. It
 * can be due to the backend failure (the file has been removed, the data base
 * doesn't allow the connection) or to an internal error like IOException when
 * managing the internal buffers for the different operations.
 *
 * @author Fernando Gonzalez Cortes
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
