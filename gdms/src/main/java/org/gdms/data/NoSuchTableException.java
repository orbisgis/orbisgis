package org.gdms.data;

/**
 * This class represents the errors that happens when the DataSourceFactory
 * is asked for a table that doesn't exists. The common mistakes are
 * typing errors, forgetting to register the source, handling of two different
 * instances of a DataSourceFactory
 *
 * @author Fernando Gonzalez Cortes
 */
public class NoSuchTableException extends Exception {
	/**
	 * Creates a new NoSuchTableException object.
	 */
	public NoSuchTableException() {
		super();
	}

	/**
	 * Creates a new NoSuchTableException object.
	 *
	 * @param arg0
	 */
	public NoSuchTableException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new NoSuchTableException object.
	 *
	 * @param arg0
	 */
	public NoSuchTableException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Creates a new NoSuchTableException object.
	 *
	 * @param arg0
	 * @param arg1
	 */
	public NoSuchTableException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
