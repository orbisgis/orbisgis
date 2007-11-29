package org.orbisgis.pluginManager;

public interface PluginActivator {

	/**
	 * Called when the plug-in is started
	 *
	 * @throws Exception
	 */
	public void start() throws Exception;

	/**
	 * Called when the application is closing and this plug-in is stoped
	 *
	 * @throws Exception
	 */
	public void stop() throws Exception;

	/**
	 * Called before closing the application. If this method returns false the
	 * application won't exit. It must return true in some case because
	 * otherwise the application won't ever close
	 *
	 * @return true if the plug-in lets the application close, false otherwise
	 */
	public boolean allowStop();
}
