package org.orbisgis.geoview.views.sqlConsole.actions;

import java.io.IOException;

public interface ConsoleListener {

	/**
	 * Executes the text in the console.
	 *
	 * @param text
	 *            Content of the console
	 */
	void execute(String text);

	/**
	 * Opens a script and returns the contents, that will be placed in the
	 * console
	 *
	 * @throws IOException
	 */
	String open() throws IOException;

	/**
	 * Saves the content of the console
	 *
	 * @param text
	 * @throws IOException
	 */
	void save(String text) throws IOException;

}
