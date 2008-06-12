/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.editorViews.toc;

import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceListener;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.action.IActionAdapter;
import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.ISelectableActionAdapter;
import org.orbisgis.action.MenuTree;
import org.orbisgis.editorViews.toc.action.EPTocLayerActionHelper;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.editorViews.toc.action.IMultipleLayerAction;
import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerCollectionEvent;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.LayerListener;
import org.orbisgis.layerModel.LayerListenerEvent;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.layerModel.MapContextListener;
import org.orbisgis.layerModel.SelectionEvent;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.resource.GdmsSource;
import org.orbisgis.resource.IResource;
import org.orbisgis.ui.resourceTree.MyTreeUI;
import org.orbisgis.ui.resourceTree.ResourceTree;
import org.orbisgis.views.geocatalog.TransferableResource;

public class Toc extends ResourceTree {
	private MyLayerListener ll;

	private TocRenderer tocRenderer;

	private TocTreeModel treeModel;

	private boolean ignoreSelection = false;

	private MyMapContextListener myMapContextListener;

	private MapContext mapContext;

	public Toc() {

		this.ll = new MyLayerListener();

		tocRenderer = new TocRenderer(this);
		DataManager dataManager = (DataManager) Services
				.getService("org.orbisgis.DataManager");
		treeModel = new TocTreeModel(dataManager.createLayerCollection("root"),
				getTree());
		this.setModel(treeModel);
		this.setTreeCellRenderer(tocRenderer);
		this.setTreeCellEditor(new TocEditor(tree));

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
						try {
							layer.setVisible(!layer.isVisible());
							tree.repaint();
						} catch (LayerException e1) {
						}
					}
				}
			}

		});

		myMapContextListener = new MyMapContextListener();

		this.getTree().getSelectionModel().addTreeSelectionListener(
				new TreeSelectionListener() {

					public void valueChanged(TreeSelectionEvent e) {
						if (!ignoreSelection) {
							TreePath[] selectedPaths = Toc.this.getSelection();
							ILayer[] selected = new ILayer[selectedPaths.length];
							for (int i = 0; i < selected.length; i++) {
								selected[i] = (ILayer) selectedPaths[i]
										.getLastPathComponent();
							}
							ignoreSelection = true;
							mapContext.setSelectedLayers(selected);
							ignoreSelection = false;
						}
					}

				});
	}

	@Override
	public JPopupMenu getPopup() {
		MenuTree menuTree = new MenuTree();
		LayerActionFactory factory = new LayerActionFactory();
		EPTocLayerActionHelper.createPopup(menuTree, factory, this,
				"org.orbisgis.editorViews.toc.Action");
		menuTree.removeEmptyMenus();
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

	@Override
	public boolean doDrop(Transferable trans, Object node) {

		ILayer dropNode = (ILayer) node;

		// By default drop on rootNode
		if (dropNode == null) {
			ILayer rootNode = (ILayer) treeModel.getRoot();
			dropNode = rootNode;
		}

		try {

			if (trans.isDataFlavorSupported(TransferableLayer.getLayerFlavor())) {
				ILayer[] draggedLayers = (ILayer[]) trans
						.getTransferData(TransferableLayer.getLayerFlavor());
				if (dropNode.acceptsChilds()) {
					for (ILayer layer : draggedLayers) {
						try {
							layer.moveTo(dropNode);
						} catch (LayerException e) {
							Services.getErrorManager().error(
									"Cannot move layer", e);
						}
					}
				} else {
					ILayer parent = dropNode.getParent();
					if (parent != null) {
						for (ILayer layer : draggedLayers) {
							if (layer.getParent() == dropNode.getParent()) {
								int index = parent.getIndex(dropNode);
								try {
									layer.moveTo(parent, index);
								} catch (LayerException e) {
									Services.getErrorManager().error(
											"Cannot move layer: "
													+ layer.getName());
								}
							}
						}
					}
				}
			} else if (trans.isDataFlavorSupported(TransferableResource
					.getResourceFlavor())) {
				final IResource[] draggedResources = (IResource[]) trans
						.getTransferData(TransferableResource
								.getResourceFlavor());
				BackgroundManager bm = (BackgroundManager) Services
						.getService("org.orbisgis.BackgroundManager");
				bm.backgroundOperation(new MoveProcess(draggedResources,
						dropNode));

			} else {
				return false;
			}
		} catch (UnsupportedFlavorException e1) {
			throw new RuntimeException("bug", e1);
		} catch (IOException e1) {
			throw new RuntimeException("bug", e1);
		}

		return true;
	}

	public Transferable getDragData(DragGestureEvent dge) {
		TreePath[] resources = getSelection();
		if (resources.length > 0) {
			return new TransferableLayer(toLayerArray(resources));
		} else {
			return null;
		}
	}

	private final class MyMapContextListener implements MapContextListener {
		public void layerSelectionChanged(MapContext mapContext) {
			if (!ignoreSelection) {
				setTocSelection(mapContext);
			}
		}

		public void activeLayerChanged(ILayer previousActiveLayer,
				MapContext mapContext) {
			treeModel.refresh();
		}
	}

	private final class LayerActionFactory implements IActionFactory {
		private final class LayerActionDecorator implements IActionAdapter {
			private Object action;

			public LayerActionDecorator(Object action) {
				this.action = action;
			}

			public boolean isVisible() {
				TreePath[] res = getSelection();
				if (action instanceof ILayerAction) {
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

					return acceptsAllResources;
				} else {
					IMultipleLayerAction layerAction = (IMultipleLayerAction) action;
					return layerAction.acceptsAll(toLayerArray(getSelection()));
				}

			}

			public boolean isEnabled() {
				return true;
			}

			public void actionPerformed() {
				if (action instanceof ILayerAction) {
					ILayerAction layerAction = (ILayerAction) action;
					EPTocLayerActionHelper.execute(mapContext, layerAction,
							toLayerArray(getSelection()));
				} else {
					IMultipleLayerAction layerAction = (IMultipleLayerAction) action;
					EPTocLayerActionHelper.execute(mapContext, layerAction,
							toLayerArray(getSelection()));
				}
			}
		}

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new LayerActionDecorator(action);
		}

		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			throw new RuntimeException(
					"Bug. Layer actions cannot be selectable");
		}
	}

	private class MyLayerListener implements LayerListener, EditionListener, DataSourceListener {

		public void layerAdded(final LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				addLayerListenerRecursively(layer, ll);
			}
			treeModel.refresh();
		}

		public void layerMoved(LayerCollectionEvent e) {
			treeModel.refresh();
		}

		public void layerRemoved(final LayerCollectionEvent e) {
			treeModel.refresh();
		}

		public void nameChanged(LayerListenerEvent e) {
		}

		public void styleChanged(LayerListenerEvent e) {

		}

		public void visibilityChanged(LayerListenerEvent e) {
		}

		public void selectionChanged(SelectionEvent e) {

		}

		public void multipleModification(MultipleEditionEvent e) {
			treeModel.refresh();
		}

		public void singleModification(EditionEvent e) {
			treeModel.refresh();
		}

		public void cancel(DataSource ds) {
		}

		public void commit(DataSource ds) {
		}

		public void open(DataSource ds) {
			treeModel.refresh();
		}

	}

	void delete() {
		if (mapContext != null) {
			mapContext.removeMapContextListener(myMapContextListener);
		}
	}

	private class MoveProcess implements BackgroundJob {

		private ILayer dropNode;
		private IResource[] draggedResources;

		public MoveProcess(IResource[] draggedResources, ILayer dropNode) {
			this.draggedResources = draggedResources;
			this.dropNode = dropNode;
		}

		public void run(IProgressMonitor pm) {
			int index;
			if (!dropNode.acceptsChilds()) {
				ILayer parent = dropNode.getParent();
				if (parent.acceptsChilds()) {
					index = parent.getIndex(dropNode);
					dropNode = parent;
				} else {
					Services.getErrorManager().error(
							"Cannot create layer on " + dropNode.getName());
					return;
				}
			} else {
				index = dropNode.getLayerCount();
			}
			DataManager dataManager = (DataManager) Services
					.getService("org.orbisgis.DataManager");
			for (int i = 0; i < draggedResources.length; i++) {
				IResource resource = draggedResources[i];
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo(100 * i / draggedResources.length);
					if (resource.getResourceType() instanceof GdmsSource) {
						try {
							dropNode.insertLayer(dataManager
									.createLayer(resource.getName()), index);
						} catch (LayerException e) {
							throw new RuntimeException("Cannot "
									+ "add the layer to the destination", e);
						}
					}
				}
			}
		}

		public String getTaskName() {
			return "Importing resources";
		}

	}

	private void addLayerListenerRecursively(ILayer rootLayer,
			MyLayerListener refreshLayerListener) {
		rootLayer.addLayerListener(refreshLayerListener);
		DataSource dataSource = rootLayer.getDataSource();
		if (dataSource != null) {
			dataSource.addEditionListener(refreshLayerListener);
			dataSource.addDataSourceListener(refreshLayerListener);
		}
		for (int i = 0; i < rootLayer.getLayerCount(); i++) {
			addLayerListenerRecursively(rootLayer.getLayer(i),
					refreshLayerListener);
		}
	}

	private void removeLayerListenerRecursively(ILayer rootLayer,
			MyLayerListener refreshLayerListener) {
		rootLayer.removeLayerListener(refreshLayerListener);
		DataSource dataSource = rootLayer.getDataSource();
		if (dataSource != null) {
			dataSource.removeEditionListener(refreshLayerListener);
			dataSource.removeDataSourceListener(refreshLayerListener);
		}
		for (int i = 0; i < rootLayer.getLayerCount(); i++) {
			removeLayerListenerRecursively(rootLayer.getLayer(i),
					refreshLayerListener);
		}
	}

	private void setTocSelection(MapContext mapContext) {
		ILayer[] selected = mapContext.getSelectedLayers();
		TreePath[] selectedPaths = new TreePath[selected.length];
		for (int i = 0; i < selectedPaths.length; i++) {
			selectedPaths[i] = new TreePath(selected[i].getLayerPath());
		}

		ignoreSelection = true;
		this.setSelection(selectedPaths);
		ignoreSelection = false;
	}

	public void setMapContext(MapContext mapContext) {
		// Remove the listeners
		if (this.mapContext != null) {
			removeLayerListenerRecursively(this.mapContext.getLayerModel(), ll);
			this.mapContext.removeMapContextListener(myMapContextListener);
		}

		if (mapContext != null) {
			this.mapContext = mapContext;
			// Add the listeners to the new MapContext
			this.mapContext.addMapContextListener(myMapContextListener);
			final ILayer root = this.mapContext.getLayerModel();
			addLayerListenerRecursively(root, ll);

			treeModel = new TocTreeModel(root, tree);

			// Set model clears selection
			ignoreSelection = true;
			Toc.this.setModel(treeModel);
			ignoreSelection = false;
			setTocSelection(Toc.this.mapContext);
			Toc.this.repaint();
		} else {
			// Remove the references to the mapContext
			DataManager dataManager = (DataManager) Services
					.getService("org.orbisgis.DataManager");
			treeModel = new TocTreeModel(dataManager
					.createLayerCollection("root"), getTree());
			this.setModel(treeModel);
			this.mapContext = new DefaultMapContext();

			// Patch to remove any reference to the previous model
			myTreeUI = new MyTreeUI();
			((MyTreeUI) tree.getUI()).dispose();
			tree.setUI(myTreeUI);
		}
	}

	boolean isActive(ILayer layer) {
		return layer == mapContext.getActiveLayer();
	}
}
