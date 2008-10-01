package org.orbisgis.config;

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
}
