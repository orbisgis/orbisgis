package org.orbisgis.geoview.fromXmlToSQLTree;

import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class EPFromXmlToSqlTreeHelper {
	public static void install() {
		IExtensionRegistry er = RegistryFactory.getRegistry();
		Extension[] exts = er
				.getExtensions("org.orbisgis.geoview.FromXmlToSqlTree");
		for (Extension extension : exts) {
			Configuration configuration = extension.getConfiguration();
			String path = configuration.getAttribute("sql", "resource-path");
			
			// active part : populate here the TreeModel...
			System.out.println(path);
		}
	}
}
