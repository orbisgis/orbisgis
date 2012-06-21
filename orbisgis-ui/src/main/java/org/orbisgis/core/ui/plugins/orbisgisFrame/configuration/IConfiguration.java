/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

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
