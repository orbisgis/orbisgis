package org.orbisgis.core;

import org.sif.UIPanel;

/**
 * All wizards extend this interface.
 *
 * @author Fernando Gonzalez Cortes
 */
public interface IWizard {

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
}
