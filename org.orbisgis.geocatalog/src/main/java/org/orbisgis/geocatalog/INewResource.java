package org.orbisgis.geocatalog;

import org.orbisgis.geocatalog.resources.IResource;
import org.sif.UIPanel;

/**
 * Interface to implement by extensions to ResourceWizard
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface INewResource {

	/**
	 * @return Returns a name that will be used by the user to identify the
	 *         wizard
	 */
	String getName();

	/**
	 * When the user selects this extension to add new resources, this method
	 * will be called. It returns an array with the pages of the wizard. This
	 * method should keep the instance that is returned because it is used in
	 * the <i>getResources</i> call
	 */
	UIPanel[] getWizardPanels();

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
