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
package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.components.resourceTree.AbstractTreeModel;
import org.orbisgis.core.ui.pluginSystem.menu.IMenu;
import org.orbisgis.utils.I18N;

class ConfigurationTreeModel extends AbstractTreeModel {
	private List<TreeModelListener> listeners;
	private IMenu root;
	private List<ConfigurationDecorator> configs;

	/**
	 * Creates a new configuration tree model
	 * 
	 * @param root
	 *            the root menu of the configuration tree
	 * @param configs
	 *            the installed configurations
	 */
	ConfigurationTreeModel(IMenu root, List<ConfigurationDecorator> configs,
			JTree tree) {
		super(tree);
		this.root = root;
		this.configs = configs;
		listeners = new ArrayList<TreeModelListener>();
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof IMenu) {
			IMenu menuParent = (IMenu) parent;
			IMenu[] children = menuParent.getChildren();
			if (index < children.length) {
				return children[index];
			} else {
				int rest = index - children.length;
				for (ConfigurationDecorator config : configs) {
					if (config.getParentId().equals(menuParent.getId())) {
						if (rest == 0) {
							return config;
						} else {
							rest--;
						}
					}
				}

				return null;
			}
		} else {
			Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.configurationTreeModel.bug"), //$NON-NLS-1$
					new RuntimeException(I18N.getString("orbisgis.org.orbisgis.configuration.menuChildren"))); //$NON-NLS-1$
			return null;
		}
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof IMenu) {
			IMenu menuParent = (IMenu) parent;
			int childCount = menuParent.getChildren().length;
			for (ConfigurationDecorator config : configs) {
				if (config.getParentId().equals(menuParent.getId())) {
					childCount++;
				}
			}

			return childCount;
		} else {
			Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.configurationTreeModel.bug"), //$NON-NLS-1$
					new RuntimeException(I18N.getString("orbisgis.org.orbisgis.configuration.menuChildren"))); //$NON-NLS-1$
			return 0;
		}
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof IMenu) {
			IMenu menuParent = (IMenu) parent;
			IMenu[] children = menuParent.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i] == child) {
					return i;
				}
			}

			int index = children.length;

			for (ConfigurationDecorator config : configs) {
				if (config.getParentId().equals(menuParent.getId())) {
					if (config == child) {
						return index;
					} else {
						index++;
					}
				}
			}

			return -1;
		} else {
			Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.configurationTreeModel.bug"), //$NON-NLS-1$
					new RuntimeException(I18N.getString("orbisgis.org.orbisgis.configuration.menuChildren"))); //$NON-NLS-1$
			return -1;
		}
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		return (node instanceof ConfigurationDecorator);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// do nothing
	}
}
