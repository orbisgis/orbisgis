package org.orbisgis.pluginManager;

public interface IExtensionRunner {

	/**
	 * Executes the extension with the specified parameter. The execution
	 * behavior is arbitrarily determined by the extension point.
	 *
	 * @param extensionId
	 * @param param
	 */
	void execute(String param);

}
