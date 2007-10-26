package org.orbisgis.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.gdms.data.DataSourceFactoryEvent;
import org.gdms.data.DataSourceFactoryListener;
import org.gdms.data.NoSuchTableException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.NodeFilter;
import org.orbisgis.geocatalog.resources.GdmsSource;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

public class Catalog extends ResourceTree {

	private boolean ignoreSourceOperations = false;

	// Handles all the actions performed in Catalog (and GeoCatalog)
	private ActionListener acl = null;

	public Catalog() {
		/** *** Register listeners **** */
		tree.addMouseListener(new MyMouseAdapter());
		this.acl = new GeocatalogActionListener();

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
						IResource res = getCatalogModel().getNodes(
								new NodeFilter() {

									public boolean accept(IResource resource) {
										return resource.getName().equals(
												e.getName());
									}

								})[0];

						((GdmsSource) res).updateNameTo(e.getNewName());
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

	public void setIgnoreSourceOperations(boolean b) {
		ignoreSourceOperations = b;
	}

	protected class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
		}

		private void showPopup(MouseEvent e) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			TreePath[] selectionPaths = tree.getSelectionPaths();
			if ((selectionPaths != null) && (path != null)) {
				if (!contains(selectionPaths, path)) {
					tree.setSelectionPath(path);
				}
			} else {
				tree.setSelectionPath(path);
			}
			if (e.isPopupTrigger()) {
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

}
