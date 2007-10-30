package org.orbisgis.geoview.toc;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.NodeFilter;
import org.orbisgis.core.resourceTree.ResourceActionValidator;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.core.resourceTree.ResourceTreeActionExtensionPointHelper;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerCollectionEvent;
import org.orbisgis.geoview.layerModel.LayerListener;
import org.orbisgis.geoview.layerModel.LayerListenerEvent;
import org.orbisgis.pluginManager.ExtensionPointManager;

public class Toc extends ResourceTree {

	private MyLayerListener ll;

	private TocActionListener al = new TocActionListener();

	private GeoView2D geoview;

	private TocRenderer tocRenderer;

	public Toc(GeoView2D geoview) {
		this.geoview = geoview;

		this.ll = new MyLayerListener();

		tocRenderer = new TocRenderer();
		this.setTreeCellRenderer(tocRenderer);
		this.setTreeCellEditor(new TocEditor(tree));

		ILayer root = geoview.getMapModel().getLayers();
		ILayerResource rootResource = LayerResourceFactory
				.getLayerResource(root);
		this.setRootNode(rootResource);

		root.addLayerListenerRecursively(ll);

		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				final int x = e.getX();
				final int y = e.getY();
				final int mouseButton = e.getButton();
				TreePath path = tree.getPathForLocation(x, y);
				Rectangle layerNodeLocation = Toc.this.tree.getPathBounds(path);

				if (path != null) {
					ILayer layer = ((ILayerResource) path
							.getLastPathComponent()).getLayer();
					Rectangle checkBoxBounds = tocRenderer.getCheckBoxBounds();
					checkBoxBounds.translate((int) layerNodeLocation.getX(),
							(int) layerNodeLocation.getY());
					if ((checkBoxBounds.contains(e.getPoint()))
							&& (MouseEvent.BUTTON1 == mouseButton)
							&& (1 == e.getClickCount())) {
						// mouse click inside checkbox
						layer.setVisible(!layer.isVisible());
						tree.repaint();
					}
				}
			}

		});
	}

	private class MyLayerListener implements LayerListener {

		public void layerAdded(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				IResource[] parent = getTreeModel().getNodes(new NodeFilter() {

					public boolean accept(IResource resource) {
						ILayer nodeLayer = ((ILayerResource) resource)
								.getLayer();
						return nodeLayer == layer.getParent();
					}

				});
				((ILayerResource) parent[0]).syncWithLayerModel();
				getTreeModel().refresh(parent[0]);
				layer.addLayerListenerRecursively(ll);
			}
		}

		public void layerMoved(LayerCollectionEvent e) {

		}

		public void layerRemoved(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				layer.removeLayerListenerRecursively(ll);

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
						ILayerAction tocAction = (ILayerAction) action;
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
						if (acceptsAllResources) {
							acceptsAllResources = tocAction
									.acceptsAll(toLayerArray(res));
						}

						return acceptsAllResources;
					}

				});
	}

	private class TocActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			ExtensionPointManager<ILayerAction> epm = new ExtensionPointManager<ILayerAction>(
					"org.orbisgis.geoview.toc.LayerAction");
			ILayerAction action = epm.instantiateFrom("/extension/action[@id='"
					+ e.getActionCommand() + "']", "class");
			IResource[] selectedResources = getSelectedResources();
			if (selectedResources.length == 0) {
				action.execute(geoview, null);
			} else {
				for (IResource resource : selectedResources) {
					action.execute(geoview, ((ILayerResource) resource)
							.getLayer());
				}
			}
			ILayer[] layers = toLayerArray(selectedResources);
			action.executeAll(geoview, layers);
		}

	}

	@Override
	protected String getDnDExtensionPointId() {
		return "org.orbisgis.geoview.toc.DND";
	}

	private ILayer[] toLayerArray(IResource[] selectedResources) {
		ILayer[] layers = new ILayer[selectedResources.length];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = ((ILayerResource) selectedResources[i]).getLayer();
		}
		return layers;
	}

}
