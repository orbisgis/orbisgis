package org.orbisgis.pluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class ExtensionRegistry implements IExtensionRegistry {

	HashMap<String, ArrayList<Extension>> extensionsByPoint = new HashMap<String, ArrayList<Extension>>();
	HashMap<String, IExtensionRunner> extensionRunner = new HashMap<String, IExtensionRunner>();

	public ExtensionRegistry(ArrayList<Extension> extensions) {

		for (Extension extension : extensions) {
			String point = extension.getPoint();
			ArrayList<Extension> extensionsInPoint = extensionsByPoint
					.get(point);
			if (extensionsInPoint == null) {
				extensionsInPoint = new ArrayList<Extension>();
			}
			extensionsInPoint.add(extension);
			extensionsByPoint.put(point, extensionsInPoint);
		}
	}

	public Extension[] getExtensions(String extensionPoint) {
		Extension[] emptySet = new Extension[0];
		ArrayList<Extension> extensions = extensionsByPoint.get(extensionPoint);
		if (extensions == null) {
			return emptySet;
		}
		return extensions.toArray(emptySet);
	}

	public void registerExtensionRunnerInstance(IExtensionRunner runner) {
		Iterator<ArrayList<Extension>> exts = extensionsByPoint.values()
				.iterator();
		while (exts.hasNext()) {
			ArrayList<Extension> array = exts.next();
			for (Extension extension : array) {
				String className = runner.getClass().getCanonicalName();
				if (className.equals(extension.getRunnerClass())) {
					extensionRunner.put(extension.getId(), runner);
				}
			}
		}
	}

	public void executeExtension(String extensionId, String param) {
		IExtensionRunner er = extensionRunner.get(extensionId);
		if (er != null) {
			er.execute(param);
		} else {
			throw new IllegalStateException("No runner instance has been"
					+ " registered. The instance that manages "
					+ "the extensions should be registered with "
					+ "registerRunnerInstance() method");
		}
	}

}
