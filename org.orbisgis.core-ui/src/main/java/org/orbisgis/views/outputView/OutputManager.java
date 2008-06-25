package org.orbisgis.views.outputView;

import java.awt.Color;

public interface OutputManager {

	/**
	 * Adds code to the output window
	 *
	 * @param out
	 */
	void append(String out);

	/**
	 * Adds text in the specified color
	 *
	 * @param text
	 * @param color
	 */
	void append(String text, Color color);

	/**
	 * Makes the output visible to the user
	 */
	void makeVisible();
}
