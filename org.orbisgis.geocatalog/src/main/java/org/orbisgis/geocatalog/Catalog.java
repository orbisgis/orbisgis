package org.orbisgis.geocatalog;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.actions.IAction;
import org.orbisgis.core.actions.IActionFactory;
import org.orbisgis.core.actions.ISelectableAction;
import org.orbisgis.core.actions.MenuTree;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.geocatalog.resources.AbstractGdmsSource;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.NodeFilter;
import org.orbisgis.geocatalog.resources.RegisteredGdmsSource;
import org.orbisgis.geocatalog.resources.ResourceFactory;
import org.orbisgis.geocatalog.resources.ResourceTreeEditor;
import org.orbisgis.geocatalog.resources.ResourceTreeModel;
import org.orbisgis.geocatalog.resources.ResourceTreeRenderer;
import org.orbisgis.geocatalog.resources.ResourceTypeException;
import org.orbisgis.geocatalog.resources.TransferableResource;
import org.orbisgis.pluginManager.PluginManager;

public class Catalog extends ResourceTree {

	private boolean ignoreSourceOperations = false;

	private ResourceTreeModel treeModel;

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

		OrbisgisCore.getDSF().getSourceManager().addSourceListener(
				new SourceListener() {

					public void sourceRemoved(final SourceEvent e) {
						if (ignoreSourceOperations) {
							return;
						}
						IResource[] res = treeModel
								.getNodes(new GdmsNodeFilter(e.getName()));
						try {
							res[0].getParentResource().removeResource(res[0]);
						} catch (ResourceTypeException e1) {
							PluginManager.error("Cannot remove '"
									+ res[0].getName() + "' from catalog", e1);
						}
					}

					public void sourceNameChanged(final SourceEvent e) {
						IResource[] res = treeModel
								.getNodes(new GdmsNodeFilter(e.getName()));

						try {
							res[0].setResourceName(e.getNewName());
						} catch (ResourceTypeException e1) {
							PluginManager.error(
									"Cannot change resource name in catalog: "
											+ res[0].getName(), e1);
						}
					}

					public void sourceAdded(SourceEvent e) {
						if (ignoreSourceOperations) {
							return;
						}
						String name = e.getName();

						if (e.isWellKnownName()) {
							RegisteredGdmsSource nodeType = new RegisteredGdmsSource(
									name);
							IResource res = ResourceFactory.createResource(
									name, nodeType);
							try {
								treeModel.getRoot().addResource(res);
							} catch (ResourceTypeException e1) {
								PluginManager.error("Cannot add resource '"
										+ res.getName() + "' in catalog", e1);
							}
						}

					}

				});

	}

	public void setIgnoreSourceOperations(boolean b) {
		ignoreSourceOperations = b;
	}

	@Override
	public JPopupMenu getPopup() {
		MenuTree menuTree = new MenuTree();
		IActionFactory factory = new ResourceActionFactory();
		EPGeocatalogResourceActionHelper.createPopup(menuTree, factory, this,
				"org.orbisgis.geocatalog.ResourceAction");

		EPResourceWizardHelper.addWizardMenus(menuTree,
				new ResourceWizardActionFactory(this));

		JPopupMenu popup = new JPopupMenu();
		JComponent[] menus = menuTree.getJMenus();
		for (JComponent menu : menus) {
			popup.add(menu);
		}

		return popup;
	}

	private final class ResourceActionFactory implements IActionFactory {

		private final class ResourceAction implements IAction {
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
						if (!resourceAction.accepts(resource)) {
							acceptsAllResources = false;
							break;
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

		public IAction getAction(Object action) {
			return new ResourceAction(action);
		}

		public ISelectableAction getSelectableAction(Object actionObject) {
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
			if (resource.getResourceType() instanceof AbstractGdmsSource) {
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
							PluginManager
									.warning("Cannot move the resource", e);
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

}
