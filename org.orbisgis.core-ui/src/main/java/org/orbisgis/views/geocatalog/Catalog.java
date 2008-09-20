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
package org.orbisgis.views.geocatalog;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.action.IActionAdapter;
import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.ISelectableActionAdapter;
import org.orbisgis.action.MenuTree;
import org.orbisgis.resource.GdmsSource;
import org.orbisgis.resource.INode;
import org.orbisgis.resource.IResource;
import org.orbisgis.resource.NodeFilter;
import org.orbisgis.resource.ResourceDecorator;
import org.orbisgis.resource.ResourceFactory;
import org.orbisgis.resource.ResourceTypeException;
import org.orbisgis.ui.resourceTree.ResourceTree;
import org.orbisgis.views.geocatalog.action.EPGeocatalogResourceActionHelper;
import org.orbisgis.views.geocatalog.action.IResourceAction;
import org.orbisgis.views.geocatalog.newResourceWizard.EPResourceWizardHelper;

public class Catalog extends ResourceTree {

	private static final Logger logger = Logger.getLogger(Catalog.class);

	private boolean ignoreSourceOperations = false;

	private ResourceTreeModel treeModel;

	private SyncSourceListener sourceListener;

	public Catalog() {
		super();
		treeModel = new ResourceTreeModel(tree) {

			@Override
			public void resourceTypeProcess(boolean b) {
				setIgnoreSourceOperations(b);
			}

		};

		setModel(treeModel);
		tree.setCellRenderer(new ResourceTreeRenderer());
		ResourceTreeEditor resourceTreeEditor = new ResourceTreeEditor(tree);
		tree.setCellEditor(resourceTreeEditor);

		sourceListener = new SyncSourceListener();
		((DataManager) Services.getService(DataManager.class)).getDSF().getSourceManager().addSourceListener(
				sourceListener);

	}

	public void setIgnoreSourceOperations(boolean b) {
		ignoreSourceOperations = b;
	}

	@Override
	public JPopupMenu getPopup() {
		MenuTree menuTree = new MenuTree();
		IActionFactory factory = new ResourceActionFactory();
		EPGeocatalogResourceActionHelper.createPopup(menuTree, factory, this,
				"org.orbisgis.views.geocatalog.Action");

		EPResourceWizardHelper wh = new EPResourceWizardHelper(this);
		wh.addWizardMenus(menuTree, new ResourceWizardActionFactory(this),
				"org.orbisgis.views.geocatalog.file.Add");

		JPopupMenu popup = new JPopupMenu();
		JComponent[] menus = menuTree.getJMenus();
		for (JComponent menu : menus) {
			popup.add(menu);
		}

		return popup;
	}

	private final class SyncSourceListener implements SourceListener {
		public synchronized void sourceRemoved(final SourceRemovalEvent e) {
			if (ignoreSourceOperations) {
				return;
			}
			IResource[] res = treeModel
					.getNodes(new GdmsNodeFilter(e.getName()));
			if (res.length > 0) {
				((ResourceDecorator) res[0]).getParent().removeNode(
						(INode) res[0]);
			}
		}

		public synchronized void sourceNameChanged(final SourceEvent e) {
			IResource[] res = treeModel
					.getNodes(new GdmsNodeFilter(e.getName()));

			((ResourceDecorator) res[0]).setName(e.getNewName());
		}

		public synchronized void sourceAdded(SourceEvent e) {
			if (ignoreSourceOperations) {
				return;
			}
			String name = e.getName();

			if (e.isWellKnownName()) {
				GdmsSource nodeType = new GdmsSource();
				IResource res = ResourceFactory.createResource(name, nodeType);
				try {
					treeModel.getRoot().addResource(res);
				} catch (ResourceTypeException e1) {
					Services.getErrorManager().error(
							"Cannot add resource '" + res.getName()
									+ "' in catalog", e1);
				}
			}

		}
	}

	private final class ResourceActionFactory implements IActionFactory {

		private final class ResourceAction implements IActionAdapter {
			private IResourceAction action;

			public ResourceAction(Object action) {
				this.action = (IResourceAction) action;
			}

			public boolean isVisible() {
				IResource[] res = getSelectedResources();
				IResourceAction resourceAction = (IResourceAction) action;
				boolean acceptsAllResources = true;
				if (resourceAction.acceptsSelectionCount(res.length)) {
					for (IResource resource : res) {
						try {
							if (!resourceAction.accepts(resource)) {
								acceptsAllResources = false;
								break;
							}
						} catch (Throwable t) {
							acceptsAllResources = false;
							logger.error("Error getting pop up", t);
						}
					}
				} else {
					acceptsAllResources = false;
				}

				return acceptsAllResources;
			}

			public boolean isEnabled() {
				return true;
			}

			public void actionPerformed() {
				EPGeocatalogResourceActionHelper.executeAction(Catalog.this,
						action, getSelectedResources());
			}
		}

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new ResourceAction(action);
		}

		public ISelectableActionAdapter getSelectableAction(Object actionObject,
				HashMap<String, String> attributes) {
			throw new RuntimeException(
					"Bug. Resource actions should not be selectable");
		}
	}

	private final class GdmsNodeFilter implements NodeFilter {
		private final String name;

		private GdmsNodeFilter(String name) {
			this.name = name;
		}

		public boolean accept(IResource resource) {
			if (resource.getResourceType() instanceof GdmsSource) {
				if (resource.getName().equals(name)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	@Override
	protected boolean doDrop(Transferable transferable, Object node) {

		// Get the node where we drop
		IResource dropNode = (IResource) node;

		// By default drop on rootNode
		if (dropNode == null) {
			IResource rootNode = treeModel.getRoot();
			dropNode = rootNode;
		}

		/** *** DRAG STUFF **** */
		if (transferable.isDataFlavorSupported(TransferableResource
				.getResourceFlavor())) {
			try {
				IResource[] resources = (IResource[]) transferable
						.getTransferData(TransferableResource
								.getResourceFlavor());

				if (isValidDragAndDrop(resources, dropNode)) {
					for (IResource resource : resources) {
						try {
							resource.moveTo(dropNode);
						} catch (ResourceTypeException e) {
							Services.getErrorManager().warning(
									"Cannot move the resource", e);
						}
					}
				} else {
					return false;
				}

			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		return true;
	}

	private boolean isValidDragAndDrop(IResource[] nodes, IResource dropNode) {
		for (IResource node : nodes) {
			if (contains(dropNode.getResourcePath(), node)) {
				return false;
			} else if (node.getParentResource() == dropNode) {
				return false;
			} else {
				IResource[] children = node.getResourcesRecursively();
				// We must check we wont put a parent in one of its children
				if (contains(children, dropNode)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean contains(IResource[] path, IResource exNode) {
		for (IResource resource : path) {
			if (resource == exNode) {
				return true;
			}
		}

		return false;
	}

	public ResourceTreeModel getTreeModel() {
		return treeModel;
	}

	protected IResource[] getSelectedResources() {
		TreePath[] paths = tree.getSelectionPaths();
		if (paths == null) {
			return new IResource[0];
		} else {
			IResource[] ret = new IResource[paths.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = (IResource) paths[i].getLastPathComponent();
			}

			return ret;
		}
	}

	@Override
	public Transferable getDragData(DragGestureEvent dge) {
		IResource[] resources = getSelectedResources();
		if (resources.length > 0) {
			return new TransferableResource(resources);
		} else {
			return null;
		}
	}

	void delete() {
		((DataManager) Services.getService(DataManager.class)).getDSF().getSourceManager().removeSourceListener(
				sourceListener);
	}

	/**
	 * Adds the resources to the catalog
	 *
	 * @param resources
	 */
	public void addResources(IResource[] resources) {
		setIgnoreSourceOperations(true);
		for (IResource resource : resources) {
			try {
				getTreeModel().getRoot().addResource(resource);
			} catch (ResourceTypeException e) {
				Services.getErrorManager().error("Cannot add the layer", e);
			}
		}
		setIgnoreSourceOperations(false);
	}

	/**
	 * Adds the resources to the catalog under the specified parent
	 *
	 * @param resources
	 *            Resources to add
	 * @param parent
	 *            Parent of the resources
	 */
	public void addResources(IResource[] resources, IResource parent) {
		setIgnoreSourceOperations(true);
		for (IResource resource : resources) {
			try {
				parent.addResource(resource);
			} catch (ResourceTypeException e) {
				Services.getErrorManager().error("Cannot add the layer", e);
			}
		}
		setIgnoreSourceOperations(false);
	}

}
