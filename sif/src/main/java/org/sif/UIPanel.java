package org.sif;

import java.awt.Component;
import java.net.URL;

/**
 * Interface that provides the necessary information to the SIF framework to
 * show dialogs and perform some validation
 *
 * @author Fernando Gonzalez Cortes
 */
public interface UIPanel {

	/**
	 * Gets the icon of the UIPanel. If this method returns null, the default
	 * icon in {@link UIFactory} is used
	 *
	 * @return
	 */
	URL getIconURL();

	/**
	 * Gets the title to show in the dialog
	 *
	 * @return
	 */
	String getTitle();

	/**
	 * Performs any initialization.
	 *
	 * @return An error description if something goes wrong or null if
	 *         everything is ok
	 */
	String initialize();

	/**
	 * When the user accepts the dialog or wizard this method is called to make
	 * a last validation before closing the dialog or going to next step in the
	 * wizard. If the user cancels this method is not called
	 *
	 * @return An error description if the validation fails or null if
	 *         everything is ok
	 */
	String postProcess();

	/**
	 * A method invoked regularly to validate the contents of the interface
	 *
	 * @return An error description if the validation fails or null if
	 *         everything is ok
	 */
	String validateInput();

	/**
	 * Gets the swing component to show in the dialog
	 *
	 * @return
	 */
	Component getComponent();

	/**
	 * Gets a text to be shown as tool tip for the dialog. Typically this will
	 * be the purpose of the dialog or some similar information
	 *
	 * @return
	 */
	String getInfoText();

}
