package org.orbisgis.geocatalog;

import org.orbisgis.geocatalog.resources.IResource;
import org.sif.UIPanel;

public interface INewResource {

	String getName();

	UIPanel[] getWizardPanels();

	IResource[] getResources();

}
