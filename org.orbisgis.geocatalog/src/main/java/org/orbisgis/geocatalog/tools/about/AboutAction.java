package org.orbisgis.geocatalog.tools.about;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.GeoCatalog;
import org.orbisgis.geocatalog.IGeocatalogAction;
import org.sif.SIFDialog;
import org.sif.UIFactory;

public class AboutAction implements IGeocatalogAction {

	public void actionPerformed(Catalog catalog) {
		final SIFDialog sifDialog = UIFactory.getSimpleDialog(new HtmlViewer(
				getClass().getResource("about.html")));
		sifDialog.setSize(650, 600);
		sifDialog.setVisible(true);
	}

	public boolean isEnabled(GeoCatalog geoCatalog) {
		return true;
	}

	public boolean isVisible(GeoCatalog geoCatalog) {
		return true;
	}
}