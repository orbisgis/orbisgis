package org.orbisgis.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;

import org.gdms.data.DataSourceFactoryEvent;
import org.gdms.data.DataSourceFactoryListener;
import org.gdms.data.NoSuchTableException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.NodeFilter;
import org.orbisgis.core.resourceTree.ResourceActionValidator;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.core.resourceTree.ResourceTreeActionExtensionPointHelper;
import org.orbisgis.geocatalog.resources.GdmsSource;
import org.orbisgis.pluginManager.ExtensionPointManager;

public class Catalog extends ResourceTree {

	private boolean ignoreSourceOperations = false;

	// Handles all the actions performed in Catalog (and GeoCatalog)
	private ActionListener acl = null;

	public Catalog() {
		this.acl = new GeocatalogActionListener();

		OrbisgisCore.getDSF().addDataSourceFactoryListener(
				new DataSourceFactoryListener() {

					public void sqlExecuted(DataSourceFactoryEvent event) {
					}

					public void sourceRemoved(final DataSourceFactoryEvent e) {
						if (ignoreSourceOperations) {
							return;
						}
						IResource[] res = getTreeModel().getNodes(
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
						getTreeModel().removeNode(res[0]);
					}

					public void sourceNameChanged(final DataSourceFactoryEvent e) {
						IResource res = getTreeModel().getNodes(
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
								getTreeModel().insertNode(node);
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
		return ResourceTreeActionExtensionPointHelper.getPopup(acl, this,
				"org.orbisgis.geocatalog.ResourceAction",
				new ResourceActionValidator() {

					public boolean acceptsSelection(Object action,
							IResource[] res) {
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

				});
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
