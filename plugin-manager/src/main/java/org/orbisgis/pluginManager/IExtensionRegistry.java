package org.orbisgis.pluginManager;


public interface IExtensionRegistry {

	/**
	 * Gets the extensions the extends the specified extension point
	 *
	 * @param extensionPointId
	 * @return
	 */
	Extension[] getExtensions(String extensionPointId);

	/**
	 * Executes the extension with the specified id using the specified
	 * parameter. The actual meaning of the execution is extension point
	 * dependent. To successfully execute an extension, the instance that
	 * manages it have to be registered before with registerRunnerInstance()
	 *
	 * @param extensionId
	 * @param param
	 */
	void executeExtension(String extensionId, String param);

	/**
	 * Registers the instance that manages the extension executions
	 *
	 * @param runner
	 */
	public void registerExtensionRunnerInstance(IExtensionRunner runner);

}
