package org.orbisgis.geoview.toc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;

import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.NodeFilter;
import org.orbisgis.core.resourceTree.ResourceActionValidator;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.core.resourceTree.ResourceTreeActionExtensionPointHelper;
import org.orbisgis.geoview.ILayerResource;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.ILayerAction;
import org.orbisgis.geoview.layerModel.LayerCollection;
import org.orbisgis.geoview.layerModel.LayerCollectionEvent;
import org.orbisgis.geoview.layerModel.LayerCollectionListener;
import org.orbisgis.geoview.layerModel.LayerListener;
import org.orbisgis.geoview.layerModel.LayerListenerEvent;
import org.orbisgis.pluginManager.ExtensionPointManager;

public class Toc extends ResourceTree {

	private MyLayerListener ll;

	private TocActionListener al = new TocActionListener();

	public Toc(LayerCollection layers) {

		this.ll = new MyLayerListener();

		LayerCollection.processLayersNodes(layers, new ILayerAction() {

			public void action(ILayer layer) {
				layer.addLayerListener(ll);
				if (layer instanceof LayerCollection) {
					((LayerCollection) layer).addCollectionListener(ll);
				}

			}

		});
	}

	private class MyLayerListener implements LayerCollectionListener,
			LayerListener {

		public void layerAdded(LayerCollectionEvent e) {
			for (ILayer layer : e.getAffected()) {
				getTreeModel().insertNode(new LayerResource(layer));
				layer.addLayerListener(ll);
				if (layer instanceof LayerCollection) {
					((LayerCollection) layer).addCollectionListener(ll);
				}
			}
		}

		public void layerMoved(LayerCollectionEvent e) {

		}

		public void layerRemoved(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				layer.removeLayerListener(ll);
				if (layer instanceof LayerCollection) {
					((LayerCollection) layer).removeCollectionListener(ll);
				}

				IResource[] toDelete = getTreeModel().getNodes(
						new NodeFilter() {
							public boolean accept(IResource resource) {
								if (resource.getName().equals(layer.getName())) {
									return true;
								} else {
									return false;
								}
							}
						});
				for (IResource resource : toDelete) {
					getTreeModel().removeNode(resource);
				}
			}
		}

		public void nameChanged(LayerListenerEvent e) {

		}

		public void styleChanged(LayerListenerEvent e) {

		}

		public void visibilityChanged(LayerListenerEvent e) {

		}

	}

	@Override
	public JPopupMenu getPopup() {
		return ResourceTreeActionExtensionPointHelper.getPopup(al, this,
				"org.orbisgis.geoview.toc.LayerAction",
				new ResourceActionValidator() {

					public boolean acceptsSelection(Object action,
							IResource[] res) {
						ITocAction tocAction = (ITocAction) action;
						boolean acceptsAllResources = true;
						if (tocAction.acceptsSelectionCount(res.length)) {
							for (IResource resource : res) {
								ILayer layer = ((ILayerResource) resource)
										.getLayer();
								if (!tocAction.accepts(layer)) {
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

	private class TocActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			ExtensionPointManager<ITocAction> epm = new ExtensionPointManager<ITocAction>(
					"org.orbisgis.geocatalog.ResourceAction");
			ITocAction action = epm.instantiateFrom("/extension/action[@id='"
					+ e.getActionCommand() + "']", "class");
			IResource[] selectedResources = getSelectedResources();
			if (selectedResources.length == 0) {
				action.execute(Toc.this, null);
			} else {
				for (IResource resource : selectedResources) {
					action.execute(Toc.this, ((ILayerResource) resource)
							.getLayer());
				}
			}
		}

	}

}
