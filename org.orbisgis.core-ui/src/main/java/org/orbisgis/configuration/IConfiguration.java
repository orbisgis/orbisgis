package org.orbisgis.configuration;

import javax.swing.JComponent;

public interface IConfiguration {
	/**
	 * Loads the configuration
	 */
	void load();

	/**
	 * Saves the configuration
	 */
	void save();

	/**
	 * Gets the component shown by the configuration dialog
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
