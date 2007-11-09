package org.orbisgis.pluginManager;

import java.util.ArrayList;

public class RegistryFactory {

	private static IExtensionRegistry extensionRegistry;

	static void createExtensionRegistry(ArrayList<Extension> extensions) {
		extensionRegistry = new ExtensionRegistry(extensions);
	}

	public static IExtensionRegistry getRegistry() {
		return extensionRegistry;
	}

}
