package org.orbisgis.pluginManager;

import java.util.ArrayList;

public class RegistryFactory {

	private static ExtensionRegistry extensionRegistry;

	static void createExtensionRegistry(ArrayList<Extension> extensions) {
		extensionRegistry = new ExtensionRegistry(extensions);
	}

	public static IExtensionRegistry getRegistry() {
		return extensionRegistry;
	}

	public static void shutdown() {
		// TODO All all the Activators.close() methods
		System.exit(0);
	}

}
