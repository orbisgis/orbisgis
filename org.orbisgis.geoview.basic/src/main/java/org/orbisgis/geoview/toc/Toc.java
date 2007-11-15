package org.orbisgis.geoview.toc;

import java.awt.Rectangle;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.orbisgis.core.MenuTree;
import org.orbisgis.core.resourceTree.ResourceActionValidator;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerCollectionEvent;
import org.orbisgis.geoview.layerModel.LayerListener;
import org.orbisgis.geoview.layerModel.LayerListenerEvent;
import org.orbisgis.geoview.layerModel.VectorLayer;

public class Toc extends ResourceTree {

	private MyLayerListener ll;

	private TocActionListener al = new TocActionListener();

	private GeoView2D geoview;

	private TocRenderer tocRenderer;

	private TocTreeModel treeModel;

	public Toc(GeoView2D geoview) {
		this.geoview = geoview;

		this.ll = new MyLayerListener();

		ILayer root = geoview.getMapModel().getLayers();
		treeModel = new TocTreeModel(root, tree);
		this.setModel(treeModel);
		tocRenderer = new TocRenderer();
		this.setTreeCellRenderer(tocRenderer);
		this.setTreeCellEditor(new TocEditor(tree));

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
					ILayer layer = (ILayer) path.getLastPathComponent();
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
				layer.addLayerListenerRecursively(ll);
			}
			treeModel.refresh();
		}

		public void layerMoved(LayerCollectionEvent e) {

		}

		public void layerRemoved(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				if (layer instanceof VectorLayer) {
					((VectorLayer)layer).processRemove();
				}
				layer.removeLayerListenerRecursively(ll);
			}
			treeModel.refresh();
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
		MenuTree menuTree = new MenuTree();
		EPTocLayerActionHelper.createPopup(menuTree, al, this,
				"org.orbisgis.geoview.toc.LayerAction",
				new ResourceActionValidator() {

					public boolean acceptsSelection(Object action,
							TreePath[] res) {
						ILayerAction tocAction = (ILayerAction) action;
						boolean acceptsAllResources = true;
						if (tocAction.acceptsSelectionCount(res.length)) {
							for (TreePath resource : res) {
								ILayer layer = (ILayer) resource
										.getLastPathComponent();
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

		JPopupMenu popup = new JPopupMenu();
		JComponent[] menus = menuTree.getJMenus();
		for (JComponent menu : menus) {
			popup.add(menu);
		}

		return popup;
	}

	public ILayer[] toLayerArray(TreePath[] selectedResources) {
		ILayer[] layers = new ILayer[selectedResources.length];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = (ILayer) selectedResources[i].getLastPathComponent();
		}
		return layers;
	}

	private class TocActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			EPTocLayerActionHelper.execute(geoview, e.getActionCommand(),
					toLayerArray(getSelection()));
		}

	}

	@Override
	protected String getDnDExtensionPointId() {
		return "org.orbisgis.geoview.toc.DND";
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		// TODO Auto-generated method stub

	}

	public void dragGestureRecognized(DragGestureEvent dge) {
		// TODO Auto-generated method stub

	}

}
