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
package org.orbisgis.views.documentCatalog;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.orbisgis.ui.resourceTree.AbstractTreeModel;

public class DocumentTreeModel extends AbstractTreeModel {

	private IDocument root;

	public DocumentTreeModel(JTree tree, IDocument root) {
		super(tree);
		this.root = root;
	}

	public Object getChild(Object parent, int index) {
		return ((IDocument) parent).getDocument(index);
	}

	public int getChildCount(Object parent) {
		return ((IDocument) parent).getDocumentCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		IDocument parentDoc = (IDocument) parent;
		for (int i = 0; i < parentDoc.getDocumentCount(); i++) {
			if (parentDoc.getDocument(i) == child) {
				return i;
			}
		}

		return -1;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {

	}

	public void refresh() {
		fireEvent(new TreePath(getRoot()));
	}
}
