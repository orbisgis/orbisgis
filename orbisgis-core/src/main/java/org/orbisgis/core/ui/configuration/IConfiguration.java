package org.orbisgis.core.ui.configuration;

import javax.swing.JComponent;

public interface IConfiguration {
	/**
	 * Loads the configuration and applies it where necessary. This method is
	 * called at startup. If no previous configuration is stored this method
	 * does nothing
	 */
	void loadAndApply();

	/**
	 * Applies the user input configuration where necessary. This method is
	 * called when the user changes the configuration by the getComponent()
	 * control
	 */
	void applyUserInput();

	/**
	 * Retrieves the applied values and saves them
	 */
	void saveApplied();

	/**
	 * Gets the component shown by the configuration dialog. The component must
	 * show the applied values of the configuration, not the loaded ones
	 * 
	 * @return the component shown by the configuration dialog
	 */
	JComponent getComponent();

	/**
	 * A method invoked regularly to validate the contents of the interface
	 * 
	 * @return An error description if the validation fails or null if
	 *         everything is ok
	 */
	String validateInput();
}
