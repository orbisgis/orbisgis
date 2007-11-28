package org.orbisgis.geocatalog;

import org.orbisgis.core.wizards.IWizard;
import org.orbisgis.geocatalog.resources.IResource;

/**
 * Interface to implement by extensions to ResourceWizard
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface INewResource extends IWizard {

	/**
	 * When the wizard is executed and finished properly by the user, this
	 * method is called to obtain the new resources. Looks for the user input in
	 * the UIPanels returned by getWizardPanels and returns an implementation of
	 * IResource
	 *
	 * @return
	 */
	IResource[] getResources();

}
