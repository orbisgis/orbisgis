package org.orbisgis.geocatalog;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gdms.data.DataSourceFactoryEvent;
import org.gdms.data.DataSourceFactoryListener;
import org.gdms.data.NoSuchTableException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.resources.Folder;
import org.orbisgis.geocatalog.resources.GdmsSource;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.TransferableResource;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class Catalog extends JPanel implements DropTargetListener,
		DragGestureListener, DragSourceListener {

	private Folder rootNode = new Folder("Root");

	private CatalogModel catalogModel = null;

	private CatalogRenderer catalogRenderer = null;

	private CatalogEditor catalogEditor = null;

	JTree tree = null;

	// Used to create a transfer when dragging
	private DragSource source = null;

	// Handles all the actions performed in Catalog (and GeoCatalog)
	private ActionListener acl = null;

	private boolean ignoreSourceOperations = false;

	/** *** Catalog constructor **** */
	public Catalog() {
		super(new GridLayout(1, 0));

		tree = new JTree();
		catalogModel = new CatalogModel(tree, rootNode);
		tree.setModel(catalogModel);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		catalogRenderer = new CatalogRenderer();
		tree.setCellRenderer(catalogRenderer);
		catalogEditor = new CatalogEditor(tree);
		tree.setCellEditor(catalogEditor);

		/***********************************************************************
		 * DO NOT UNCOMMENT *
		 *
		 * setDragEnabled(true);
		 *
		 * This method is a swing method while our DnD is using awt. Using both
		 * swing and awt creates horrible exceptions... Please use DragSource
		 * instead
		 *
		 */

		/** *** Drag and Drop stuff **** */
		tree.setDropTarget(new DropTarget(this, this));
		source = DragSource.getDefaultDragSource();
		source.createDefaultDragGestureRecognizer(tree,
				DnDConstants.ACTION_COPY_OR_MOVE, this);

		/** *** Register listeners **** */
		tree.addMouseListener(new MyMouseAdapter());
		this.acl = new GeocatalogActionListener();

		/** *** UI stuff **** */
		add(new JScrollPane(tree));
		tree.setRootVisible(false);

		OrbisgisCore.getDSF().addDataSourceFactoryListener(
				new DataSourceFactoryListener() {

					public void sqlExecuted(DataSourceFactoryEvent event) {
					}

					public void sourceRemoved(final DataSourceFactoryEvent e) {
						if (ignoreSourceOperations) {
							return;
						}
						IResource[] res = getCatalogModel().getNodes(
								new NodeFilter() {

									public boolean accept(IResource resource) {
										if (resource instanceof GdmsSource) {
											if (resource.getName().equals(
													e.getName())) {
												return true;
											} else {
												return false;
											}
										} else {
											return false;
										}
									}

								});
						getCatalogModel().removeNode(res[0]);
					}

					public void sourceNameChanged(final DataSourceFactoryEvent e) {
						IResource res = catalogModel.getNodes(new NodeFilter() {

							public boolean accept(IResource resource) {
								return resource.getName().equals(e.getName());
							}

						})[0];

						((GdmsSource)res).updateNameTo(e.getNewName());
					}

					public void sourceAdded(DataSourceFactoryEvent e) {
						if (ignoreSourceOperations) {
							return;
						}
						String name = e.getName();
						String driver = null;

						if (e.isWellKnownName()) {
							try {
								driver = OrbisgisCore.getDSF().getDriver(name);
							} catch (NoSuchTableException e2) {
								e2.printStackTrace();
							}
							if (driver != null) {
								GdmsSource node = new GdmsSource(name);
								getCatalogModel().insertNode(node);
							}
						}

					}

				});

	}

	public void clearCatalog() {
		catalogModel.removeAllNodes();
	}

	/**
	 * Move exMyNode and put it as child of newMyNode
	 *
	 * @param exMyNode
	 * @param newMyNode
	 */
	private void moveNode(IResource exNode, IResource newNode) {
		// Ensure we are not moving within the same directory or
		// on itself
		if (exNode != newNode && exNode.getParent() != newNode) {
			ArrayList<IResource> children = exNode.depthChildList();
			// We must check we wont put a parent in one of its children
			if (!children.contains(newNode)) {
				catalogModel.removeNode(exNode, true);
				catalogModel.insertNodeInto(exNode, newNode);
			}
		}
	}

	private IResource[] getSelectedResources() {
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

	/**
	 * Retrieves myNode at the location point and select the node at this point
	 * Use it like this : currentNode = getMyNodeAtPoint(anypoint); so the
	 * selected node and currentNode remains coherent
	 *
	 * @param point
	 * @return
	 */
	private IResource getMyNodeAtPoint(Point point) {
		TreePath treePath = tree.getPathForLocation(point.x, point.y);
		IResource myNode = null;
		if (treePath != null) {
			myNode = (IResource) treePath.getLastPathComponent();
		}
		return myNode;
	}

	/**
	 * Once the user begins a drag, this is executed. It creates an instance of
	 * TransferableResource, which can be retrieved during the drop.
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {
		IResource[] resources = getSelectedResources();
		if (resources.length > 0) {
			TransferableResource data = new TransferableResource(resources);
			if (data != null) {
				source.startDrag(dge, DragSource.DefaultMoveDrop, data, this);
			}
		}
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	public void dragEnter(DragSourceDragEvent dsde) {
	}

	public void dragExit(DragSourceEvent dse) {
	}

	public void dragOver(DragSourceDragEvent dsde) {
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent dtde) {

		/** *** DROP STUFF **** */
		// Get the node where we drop
		IResource dropNode = getMyNodeAtPoint(dtde.getLocation());

		// By default drop on rootNode
		if (dropNode == null) {
			dropNode = rootNode;
		}

		/** *** DRAG STUFF **** */
		Transferable transferable = dtde.getTransferable();
		if (transferable
				.isDataFlavorSupported(TransferableResource.myNodeFlavor)) {
			try {
				IResource[] myNode = (IResource[]) transferable
						.getTransferData(TransferableResource.myNodeFlavor);

				// If we dropped on a folder, move the resource
				if (dropNode instanceof Folder) {
					for (IResource resource : myNode) {
						moveNode(resource, dropNode);
					}
				} else {
					for (IResource resource : myNode) {
						dropNode.addChild(resource);
						tree.scrollPathToVisible(new TreePath(resource
								.getPath()));
					}
				}

			} catch (UnsupportedFlavorException e) {
			} catch (IOException e) {
			}
		}
		dtde.rejectDrop();
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	private class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
		}

		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				TreePath[] selectionPaths = tree.getSelectionPaths();
				if ((selectionPaths != null) && (path != null)) {
					if (!contains(selectionPaths, path)) {
						tree.setSelectionPath(path);
					}
				} else {
					tree.setSelectionPath(path);
				}
				getPopup().show(e.getComponent(), e.getX(), e.getY());
			}
		}

		private boolean contains(TreePath[] selectionPaths, TreePath path) {
			for (TreePath treePath : selectionPaths) {
				boolean equals = true;
				Object[] objectPath = treePath.getPath();
				Object[] testPath = path.getPath();
				if (objectPath.length != testPath.length) {
					equals = false;
				}
				for (int i = 0; i < testPath.length; i++) {
					if (testPath[i] != objectPath[i]) {
						equals = false;
					}
				}
				if (equals) {
					return true;
				}
			}

			return false;
		}

		private JPopupMenu getPopup() {
			JPopupMenu popup = new JPopupMenu();

			IExtensionRegistry reg = RegistryFactory.getRegistry();
			Extension[] exts = reg
					.getExtensions("org.orbisgis.geocatalog.ResourceAction");
			HashMap<String, ArrayList<JMenuItem>> groups = new HashMap<String, ArrayList<JMenuItem>>();
			ArrayList<String> orderedGroups = new ArrayList<String>();
			for (int i = 0; i < exts.length; i++) {
				Configuration c = exts[i].getConfiguration();

				int n = c.evalInt("count(/extension/action)");
				for (int j = 0; j < n; j++) {
					String base = "/extension/action[" + (j + 1) + "]";
					IResourceAction action = (IResourceAction) c
							.instantiateFromAttribute(base, "class");

					boolean acceptsAllResources = true;
					IResource[] res = getSelectedResources();
					for (IResource resource : res) {
						if (!action.accepts(resource)) {
							acceptsAllResources = false;
							break;
						}
					}
					if (acceptsAllResources) {
						if ((res.length > 0)
								|| (action.acceptsEmptySelection())) {
							JMenuItem actionMenu = getMenuFrom(c, base, groups,
									orderedGroups);
							popup.add(actionMenu);
						}
					}
				}
			}

			for (int i = 0; i < orderedGroups.size(); i++) {
				ArrayList<JMenuItem> pops = groups.get(orderedGroups.get(i));
				for (int j = 0; j < pops.size(); j++) {
					popup.add(pops.get(j));
				}
				if (i != orderedGroups.size() - 1) {
					popup.addSeparator();
				}
			}

			return popup;
		}

		private JMenuItem getMenuFrom(Configuration c, String baseXPath,
				HashMap<String, ArrayList<JMenuItem>> groups,
				ArrayList<String> orderedGroups) {
			String text = c.getAttribute(baseXPath, "text");
			String id = c.getAttribute(baseXPath, "id");
			String icon = c.getAttribute(baseXPath, "icon");
			JMenuItem menu = getMenu(text, id, icon);

			String group = c.getAttribute(baseXPath, "group");
			ArrayList<JMenuItem> pops = groups.get(group);
			if (pops == null) {
				pops = new ArrayList<JMenuItem>();
			}
			pops.add(menu);
			groups.put(group, pops);
			if (!orderedGroups.contains(group)) {
				orderedGroups.add(group);
			}

			return menu;
		}

		private JMenuItem getMenu(String text, String actionCommand,
				String iconURL) {
			JMenuItem menuItem = new JMenuItem(text);
			menuItem.addActionListener(acl);
			menuItem.setActionCommand(actionCommand);

			if (iconURL != null) {
				Icon icon = new ImageIcon(this.getClass().getResource(iconURL));
				menuItem.setIcon(icon);
			}

			return menuItem;
		}

	}

	private class GeocatalogActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			ExtensionPointManager<IResourceAction> epm = new ExtensionPointManager<IResourceAction>(
					"org.orbisgis.geocatalog.ResourceAction");
			IResourceAction action = epm.instantiateFrom(
					"/extension/action[@id='" + e.getActionCommand() + "']",
					"class");
			IResource[] selectedResources = getSelectedResources();
			if (selectedResources.length == 0) {
				action.execute(Catalog.this, null);
			} else {
				for (IResource resource : selectedResources) {
					action.execute(Catalog.this, resource);
				}
			}
		}

	}

	public CatalogModel getCatalogModel() {
		return catalogModel;
	}

	public void setIgnoreSourceOperations(boolean b) {
		ignoreSourceOperations = b;
	}

}