/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.views.sqlSemanticRepository;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBException;

import org.orbisgis.core.resourceTree.AbstractTreeModel;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.Menu;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.MenuItem;

public class ToolsMenuPanelTreeModel extends AbstractTreeModel {
	private final Menu rootMenu;

	public ToolsMenuPanelTreeModel(final Menu rootMenu, final JTree tree)
			throws JAXBException {
		super(tree);
		this.rootMenu = rootMenu;
	}

	public Object getChild(Object parent, int index) {
		final Menu parentMenu = (Menu) parent;
		return parentMenu.getMenuOrMenuItem().get(index);
	}

	public int getChildCount(Object parent) {
		final Menu parentMenu = (Menu) parent;
		return parentMenu.getMenuOrMenuItem().size();
	}

	public int getIndexOfChild(Object parent, Object child) {
		final Menu parentMenu = (Menu) parent;
		return parentMenu.getMenuOrMenuItem().indexOf(child);
	}

	public Object getRoot() {
		return rootMenu;
	}

	public boolean isLeaf(Object node) {
		return node instanceof MenuItem;
	}

	public void refresh() {
		fireEvent(new TreePath(rootMenu));
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}
}