package org.orbisgis.renderer.legend;

/**
 * The layer cannot be drawn
 *
 * @author Fernando Gonzalez Cortes
 */
public class RenderException extends Exception {

	public RenderException() {
		super();
	}

	public RenderException(String message, Throwable cause) {
		super(message, cause);
	}

	public RenderException(String message) {
		super(message);
	}

	public RenderException(Throwable cause) {
		super(cause);
	}

}
