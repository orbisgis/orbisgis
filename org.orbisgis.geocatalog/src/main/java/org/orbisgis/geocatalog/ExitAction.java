package org.orbisgis.geocatalog;

import javax.swing.JOptionPane;

import org.orbisgis.pluginManager.PluginManager;

public class ExitAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		int answer = JOptionPane.showConfirmDialog(catalog,
				"Really quit?", "OrbisGIS", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			PluginManager.stop();
		}
	}

	public boolean isEnabled(GeoCatalog geoCatalog) {
		return true;
	}

	public boolean isVisible(GeoCatalog geoCatalog) {
		return true;
	}

}
