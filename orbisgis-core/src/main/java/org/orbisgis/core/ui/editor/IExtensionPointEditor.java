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
package org.orbisgis.core.ui.editor;

import org.orbisgis.core.ui.action.MenuTree;
import org.orbisgis.core.ui.action.ToolBarArray;

public interface IExtensionPointEditor extends IEditor {

	/**
	 * Adds the parent menus and tool bars to the main window. This will be the
	 * parents of the entries installed in the installExtensionPoint method
	 * 
	 * @param menuTree
	 * @param toolBarArray
	 */
	void prepareMenus(MenuTree menuTree, ToolBarArray toolBarArray);

	/**
	 * Adds Menus and tool bars to the main window. There are easier ways to
	 * install actions that deal with a concrete editor. it should be used only
	 * if the editor needs to keep track of all the actions for some reason
	 * 
	 * @param menuTree
	 * @param toolBarArray
	 */
	void installExtensionPoint(MenuTree menuTree, ToolBarArray toolBarArray);

}
