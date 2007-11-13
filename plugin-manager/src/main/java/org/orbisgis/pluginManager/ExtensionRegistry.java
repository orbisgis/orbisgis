package org.orbisgis.pluginManager;

import java.util.ArrayList;
import java.util.HashMap;

class ExtensionRegistry implements IExtensionRegistry {

	private HashMap<String, ArrayList<Extension>> extensionsByPoint = new HashMap<String, ArrayList<Extension>>();

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
}
