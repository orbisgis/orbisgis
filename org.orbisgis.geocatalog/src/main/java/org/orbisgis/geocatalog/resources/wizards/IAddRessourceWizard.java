package org.orbisgis.geocatalog.resources.wizards;

import javax.swing.JPanel;

import org.orbisgis.geocatalog.resources.IResource;

public interface IAddRessourceWizard {

	public JPanel getWizardUI();

	public IResource[] getNewResources();

}
