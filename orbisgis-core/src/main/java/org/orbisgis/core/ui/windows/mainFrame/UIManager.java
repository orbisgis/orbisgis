/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.windows.mainFrame;

import javax.swing.JFrame;

import net.infonode.docking.RootWindow;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.action.IActionAdapter;

public interface UIManager {

	/**
	 * Gets the main frame of the application. Useful to set the parent of the
	 * dialogs. There is no functionality in this frame that cannot be used
	 * through the preferred way, the {@link Services} class
	 *
	 * @return
	 */
	JFrame getMainFrame();

	/**
	 * Refreshes the status of the actions. Usually this method is invoked
	 * automatically but there are certain cases where it is necessary to call
	 * it manually
	 */
	void refreshUI();

	/**
	 * Get the groups of all the actions installed as menus
	 *
	 * @return
	 */
	String[] getInstalledMenuGroups();

	/**
	 * Get the ids of the specified menu children
	 *
	 * @param parentMenuId
	 *            The menu id. Null if the top level menus are required
	 *
	 * @return
	 */
	String[] getMenuChildren(String parentMenuId);

	/**
	 * Gets the name of the specified menu in the current locale
	 *
	 * @param menuId
	 * @return
	 */
	String getMenuName(String menuId);

	/**
	 * Install a menu for an action
	 *
	 * @param id
	 *            id of the new menu
	 * @param text
	 *            Text to show in the menu
	 * @param menuId
	 *            Id of the parent menu. It must be different from null and a
	 *            menu with that id must exist
	 * @param group
	 *            Group to install the action to
	 * @param actionAdapter
	 *            Adapter to manage visibility and execution
	 * @throws IllegalArgumentException
	 *             If the menuId is null or doesn't point to a valid menu
	 */
	void installMenu(String id, String text, String menuId, String group,
			IActionAdapter actionAdapter) throws IllegalArgumentException;

	/**
	 * Uninstalls a menu with the specified id
	 *
	 * @param idPath
	 */
	void uninstallMenu(String id);

	/**
	 *
	 * @return the rootwindow
	 */
	RootWindow getRoot();

}
