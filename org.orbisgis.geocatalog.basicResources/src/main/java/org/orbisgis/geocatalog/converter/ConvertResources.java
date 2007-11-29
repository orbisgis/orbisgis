package org.orbisgis.geocatalog.converter;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.GeoCatalog;
import org.orbisgis.geocatalog.IGeocatalogAction;
import org.orbisgis.geocatalog.INewResource;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.pluginManager.ui.FileWizard;
import org.sif.SIFWizard;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class ConvertResources implements IGeocatalogAction{

	
	
	public void actionPerformed(Catalog catalog) {
		
		SIFWizard sifDialog = UIFactory.getWizard(new ConvertXYZDEMWizard().getWizardPanels());
		sifDialog.pack();
		sifDialog.setVisible(true);
	}

	public boolean isEnabled(GeoCatalog geoCatalog) {
		
		return true;
	}

	public boolean isVisible(GeoCatalog geoCatalog) {		
		return true;
	}



}
