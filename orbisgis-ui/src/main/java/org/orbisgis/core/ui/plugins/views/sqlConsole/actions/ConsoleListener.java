/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.views.sqlConsole.actions;

import java.awt.datatransfer.Transferable;
import java.io.IOException;

public interface ConsoleListener {

	/**
	 * Executes the text in the console.
	 * 
	 * @param text
	 *            Content of the console
	 */
	void execute(String text);

	/**
	 * Opens a script and returns the contents, that will be placed in the
	 * console
	 * 
	 * @throws IOException
	 */
	String open() throws IOException;

	/**
	 * Saves the content of the console
	 * 
	 * @param text
         * @return true if the file has been saved, false otherwise
         * @throws IOException
	 */
	boolean save(String text) throws IOException;

	/**
	 * Invoked when the text of the console is changed
	 */
	void change();

	/**
	 * Return true if the buttons to execute, open, save, etc. have to be shown
	 * 
	 * @return
	 */
	boolean showControlButtons();

	/**
	 * Manages the drop event in the console and returns the string that will be
	 * added to the console. Return null to have a default management
	 * 
	 * @param t
	 * @return
	 */
	String doDrop(Transferable t);
}
