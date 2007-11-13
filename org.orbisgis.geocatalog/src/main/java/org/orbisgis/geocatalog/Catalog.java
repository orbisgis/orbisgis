package org.orbisgis.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.gdms.data.NoSuchTableException;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.orbisgis.core.MenuTree;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.NodeFilter;
import org.orbisgis.core.resourceTree.ResourceActionValidator;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.GdmsSource;

public class Catalog extends ResourceTree {

	private boolean ignoreSourceOperations = false;

	// Handles all the actions performed in Catalog (and GeoCatalog)
	private ActionListener acl = null;

	public Catalog() {
		this.acl = new GeocatalogActionListener();

		OrbisgisCore.getDSF().getSourceManager().addSourceListener(
				new SourceListener() {

					public void sourceRemoved(final SourceEvent e) {
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

					public void sourceNameChanged(final SourceEvent e) {
						IResource res = getTreeModel().getNodes(
								new NodeFilter() {

									public boolean accept(IResource resource) {
										return resource.getName().equals(
												e.getName());
									}

								})[0];

						((GdmsSource) res).updateNameTo(e.getNewName());
					}

					public void sourceAdded(SourceEvent e) {
						if (ignoreSourceOperations) {
							return;
						}
						String name = e.getName();
						String driver = null;

						if (e.isWellKnownName()) {
							try {
								driver = OrbisgisCore.getDSF()
										.getSourceManager().getDriverName(name);
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
		MenuTree menuTree = new MenuTree();
		EPGeocatalogResourceActionHelper.createPopup(menuTree, acl, this,
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

		EPResourceWizardHelper.addWizardMenus(menuTree, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				IResource[] resource = getSelectedResources();
				if (resource.length == 0) {
					EPResourceWizardHelper.runWizard(Catalog.this, e
							.getActionCommand(), null);
				} else {
					EPResourceWizardHelper.runWizard(Catalog.this, e
							.getActionCommand(), resource[0]);
				}
			}

		});

		JPopupMenu popup = new JPopupMenu();
		JComponent[] menus = menuTree.getJMenus();
		for (JComponent menu : menus) {
			popup.add(menu);
		}

		return popup;
	}

	private class GeocatalogActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			EPGeocatalogResourceActionHelper.executeAction(Catalog.this, e
					.getActionCommand(), getSelectedResources());
		}

	}

	@Override
	protected String getDnDExtensionPointId() {
		return "org.orbisgis.geocatalog.DND";
	}

}
