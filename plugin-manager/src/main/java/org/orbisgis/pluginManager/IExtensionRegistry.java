package org.orbisgis.pluginManager;


public interface IExtensionRegistry {

	/**
	 * Gets the extensions the extends the specified extension point
	 *
	 * @param extensionPointId
	 * @return
	 */
	Extension[] getExtensions(String extensionPointId);

}
