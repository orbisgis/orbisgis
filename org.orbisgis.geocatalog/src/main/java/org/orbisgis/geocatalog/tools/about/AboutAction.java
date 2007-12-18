package org.orbisgis.geocatalog.tools.about;

import javax.swing.JFrame;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.GeoCatalog;
import org.orbisgis.geocatalog.IGeocatalogAction;

public class AboutAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		final JFrame viewerFrame = new HtmlViewer(AboutAction.class
				.getResource("about.html"));
		viewerFrame.setSize(650, 600);
		viewerFrame.setVisible(true);
	}

	public boolean isEnabled(GeoCatalog geoCatalog) {
		return true;
	}

	public boolean isVisible(GeoCatalog geoCatalog) {
		return true;
	}
}