package org.orbisgis.geocatalog;

import javax.swing.JOptionPane;

public class AboutAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		JOptionPane.showMessageDialog(catalog,
				"GeoCatalog\nVersion 0.0", "About GeoCatalog",
				JOptionPane.INFORMATION_MESSAGE);

	}

}
