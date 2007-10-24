package org.orbisgis.geocatalog;

import javax.swing.JOptionPane;

import org.orbisgis.pluginManager.RegistryFactory;

public class ExitAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		exit(catalog);
	}

	public static void exit(Catalog catalog) {
		int answer = JOptionPane.showConfirmDialog(catalog,
				"Really quit?", "OrbisGIS", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			RegistryFactory.shutdown();
		}
	}

}
