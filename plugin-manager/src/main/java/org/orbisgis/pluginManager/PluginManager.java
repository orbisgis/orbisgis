package org.orbisgis.pluginManager;

import java.io.File;

public interface PluginManager {

	/**
	 * Exits the application
	 */
	void stop();

	/**
	 * Gets the file where all the logging take place
	 *
	 * @return
	 */
	String getLogFile();

	/**
	 * Gets the folder where the basic configuration of OrbisGIS is stored
	 *
	 * @return
	 */
	File getHomeFolder();

	/**
	 * Adds a listener of system events
	 *
	 * @param listener
	 */
	void addSystemListener(SystemListener listener);

	/**
	 * Removes a listener of system events
	 *
	 * @param listener
	 */
	void removeSystemListener(SystemListener listener);

}
