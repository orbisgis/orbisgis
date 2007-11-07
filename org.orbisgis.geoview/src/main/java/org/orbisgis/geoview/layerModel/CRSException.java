package org.orbisgis.geoview.layerModel;

public class CRSException extends Exception {
	public CRSException() {
	}

	/**
	 * @param message
	 */
	public CRSException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CRSException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CRSException(String message, Throwable cause) {
		super(message, cause);
	}
}