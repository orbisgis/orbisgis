/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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
package org.orbisgis.geocatalog.resources;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.core.resourceTree.AbstractTreeModel;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.pluginManager.PluginManager;

public class ResourceTreeModel extends AbstractTreeModel implements TreeModel {

	private IResource rootNode;

	public ResourceTreeModel(JTree tree) {
		super(tree);
		init();
	}

	private void init() {
		rootNode = ResourceFactory.createResource("Root", new Folder(), this);
	}

	public ResourceTreeModel(ResourceTree rt) {
		super(rt.getTree());
		init();
	}

	public boolean existNode(IResource node) {
		boolean ok = false;
		for (IResource myNode : rootNode.getResourcesRecursively()) {
			if (myNode == node) {
				ok = true;
				break;
			}
		}
		return ok;
	}

	public void clearCatalog() {
		IResource rootNode = getRoot();
		IResource[] rootChilds = rootNode.getResources();
		for (IResource treeResource : rootChilds) {
			try {
				rootNode.removeResource(treeResource);
			} catch (ResourceTypeException e) {
				PluginManager.error("Cannot delete " + treeResource.getName(),
						e);
			}
		}
	}

	public Object getChild(Object parent, int index) {
		IResource p = (IResource) parent;
		return p.getResourceAt(index);
	}

	public int getChildCount(Object parent) {
		IResource p = (IResource) parent;
		return p.getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		IResource p = (IResource) parent;
		return p.getIndex((IResource) child);
	}

	public IResource getRoot() {
		return rootNode;
	}

	public boolean isLeaf(Object node) {
		IResource p = (IResource) node;
		return p.getChildCount() == 0;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public IResource[] getNodes(NodeFilter nodeFilter) {
		return getNodes(nodeFilter, rootNode).toArray(new IResource[0]);
	}

	private ArrayList<IResource> getNodes(NodeFilter nodeFilter, IResource node) {
		ArrayList<IResource> ret = new ArrayList<IResource>();
		if (nodeFilter.accept(node)) {
			ret.add(node);
		}
		IResource[] childs = node.getResources();
		for (int i = 0; i < childs.length; i++) {
			ret.addAll(getNodes(nodeFilter, childs[i]));
		}

		return ret;
	}

	public void setRootNode(IResource rootNode) {
		this.rootNode = rootNode;
		fireEvent(new TreePath(rootNode));
	}

	public void refresh(IResource resource) {
		fireEvent(new TreePath(getRoot()));
	}

	public void resourceTypeProcess(boolean b) {
	}

}
