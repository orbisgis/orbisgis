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
package org.orbisgis.core.ui.editorViews.toc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceListener;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerCollectionEvent;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.LayerListener;
import org.orbisgis.core.layerModel.LayerListenerEvent;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.MapContextListener;
import org.orbisgis.core.layerModel.SelectionEvent;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.ui.action.IActionAdapter;
import org.orbisgis.core.ui.action.IActionFactory;
import org.orbisgis.core.ui.action.ISelectableActionAdapter;
import org.orbisgis.core.ui.action.MenuTree;
import org.orbisgis.core.ui.components.resourceTree.MyTreeUI;
import org.orbisgis.core.ui.components.resourceTree.ResourceTree;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editorViews.toc.TocTreeModel.LegendNode;
import org.orbisgis.core.ui.editorViews.toc.action.EPTocLayerActionHelper;
import org.orbisgis.core.ui.editorViews.toc.action.ILayerAction;
import org.orbisgis.core.ui.editorViews.toc.action.IMultipleLayerAction;
import org.orbisgis.core.ui.editors.map.MapEditor;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.core.ui.views.geocatalog.TransferableSource;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class Toc extends ResourceTree {
	public static final String SCALE_1000 = "1000";
	public static final String SCALE_5000 = "5000";
	public static final String SCALE_10000 = "10000";
	public static final String SCALE_25000 = "25000";
	public static final String SCALE_50000 = "50000";
	public static final String SCALE_100000 = "100000";
	public static final String SCALE_500000 = "500000";
	public static final String SCALE_1000000 = "1000000";
	public static final String SCALE_5000000 = "5000000";
	public static final String SCALE_10000000 = "10000000";;

	private MyLayerListener ll;

	private TocRenderer tocRenderer;

	private TocTreeModel treeModel;

	private boolean ignoreSelection = false;

	private MyMapContextListener myMapContextListener;

	private MapContext mapContext = null;

	private EditableElement element = null;

	private JComboBox combobox;
	private MapEditor mapEditor;

	public Toc() {

		this.ll = new MyLayerListener();

		tocRenderer = new TocRenderer(this);
		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);
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
					if (path.getLastPathComponent() instanceof ILayer) {
						ILayer layer = (ILayer) path.getLastPathComponent();
						Rectangle checkBoxBounds = tocRenderer
								.getCheckBoxBounds();
						checkBoxBounds.translate(
								(int) layerNodeLocation.getX(),
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
					} else if (path.getLastPathComponent() instanceof LegendNode) {

						LegendNode legendNode = (LegendNode) path
								.getLastPathComponent();
						Rectangle checkBoxBounds = tocRenderer
								.getCheckBoxBounds();
						checkBoxBounds.translate(
								(int) layerNodeLocation.getX(),
								(int) layerNodeLocation.getY());
						if ((checkBoxBounds.contains(e.getPoint()))
								&& (MouseEvent.BUTTON1 == mouseButton)
								&& (1 == e.getClickCount())) {
							try {
								Legend legend = legendNode.getLayer()
										.getRenderingLegend()[legendNode
										.getLegendIndex()];
								if (!legend.isVisible()) {
									legend.setVisible(true);
								} else {
									legend.setVisible(false);
								}
								ILayer layer = legendNode.getLayer();
								if (layer.isVisible()) {
									layer.setVisible(true);
								}
								tree.repaint();
							} catch (DriverException e1) {
								e1.printStackTrace();
							} catch (LayerException e1) {
								e1.printStackTrace();
							}

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
							ArrayList<ILayer> layers = getSelectedLayers(selectedPaths);

							ignoreSelection = true;
							mapContext.setSelectedLayers(layers
									.toArray(new ILayer[0]));
							ignoreSelection = false;
						}
					}

				});

		JPanel p = new JPanel(new BorderLayout());

		JLabel label = new JLabel("Scale 1 : ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setForeground(Color.BLUE);

		combobox = new JComboBox(new String[] { SCALE_1000, SCALE_5000,
				SCALE_10000, SCALE_25000, SCALE_50000, SCALE_100000,
				SCALE_500000, SCALE_1000000, SCALE_5000000, SCALE_10000000 });
		combobox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		combobox.addActionListener(new ScaleListener());
		combobox.setMaximumSize(new Dimension(100,20));
		p.add(label);
		p.add(combobox);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(p);
	}

	private ArrayList<ILayer> getSelectedLayers(TreePath[] selectedPaths) {
		ArrayList<ILayer> layers = new ArrayList<ILayer>();
		for (int i = 0; i < selectedPaths.length; i++) {
			Object lastPathComponent = selectedPaths[i].getLastPathComponent();
			if (lastPathComponent instanceof ILayer) {
				layers.add((ILayer) lastPathComponent);
			}
		}
		return layers;
	}

	@Override
	public JPopupMenu getPopup() {
		MenuTree menuTree = new MenuTree();
		LayerActionFactory factory = new LayerActionFactory();
		EPTocLayerActionHelper.createPopup(menuTree, factory,
				"org.orbisgis.core.ui.editorViews.toc.Action");
		menuTree.removeEmptyMenus();
		JPopupMenu popup = new JPopupMenu();
		JComponent[] menus = menuTree.getJMenus();
		for (JComponent menu : menus) {
			popup.add(menu);
		}

		return popup;
	}

	@Override
	protected boolean isDroppable(TreePath path) {
		return path.getLastPathComponent() instanceof ILayer;
	}

	@Override
	public boolean doDrop(Transferable trans, Object node) {

		ILayer dropNode;

		if (node instanceof TocTreeModel.LegendNode) {
			dropNode = ((TocTreeModel.LegendNode) node).getLayer();
		} else {
			dropNode = (ILayer) node;
		}
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
			} else if (trans.isDataFlavorSupported(TransferableSource
					.getResourceFlavor())) {
				final String[] draggedResources = (String[]) trans
						.getTransferData(TransferableSource.getResourceFlavor());
				BackgroundManager bm = (BackgroundManager) Services
						.getService(BackgroundManager.class);
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
		ArrayList<ILayer> layers = getSelectedLayers(resources);
		if (layers.size() == 0) {
			return null;
		} else {
			return new TransferableLayer(element, layers.toArray(new ILayer[0]));
		}
	}

	private final class MyMapContextListener implements MapContextListener {
		public void layerSelectionChanged(MapContext mapContext) {
			setTocSelection(mapContext);
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
				if (mapContext != null) {
					ILayer[] selectedLayers = mapContext.getSelectedLayers();
					if (action instanceof ILayerAction) {
						ILayerAction tocAction = (ILayerAction) action;
						boolean acceptsAllResources = true;
						if (tocAction
								.acceptsSelectionCount(selectedLayers.length)) {
							for (ILayer layer : selectedLayers) {
								if (!tocAction.accepts(mapContext, layer)) {
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
						return layerAction.acceptsAll(selectedLayers);
					}
				} else {
					return false;
				}
			}

			public boolean isEnabled() {
				return true;
			}

			public void actionPerformed() {
				if (action instanceof ILayerAction) {
					ILayerAction layerAction = (ILayerAction) action;
					EPTocLayerActionHelper.execute(mapContext, layerAction,
							mapContext.getSelectedLayers());
				} else {
					IMultipleLayerAction layerAction = (IMultipleLayerAction) action;
					EPTocLayerActionHelper.execute(mapContext, layerAction,
							mapContext.getSelectedLayers());
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

	private class MyLayerListener implements LayerListener, EditionListener,
			DataSourceListener {

		public void layerAdded(final LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				addLayerListenerRecursively(layer, ll);
			}
			treeModel.refresh();
		}

		public void layerMoved(LayerCollectionEvent e) {
			treeModel.refresh();
		}

		@Override
		public boolean layerRemoving(LayerCollectionEvent e) {
			// Close editors
			for (final ILayer layer : e.getAffected()) {
				ILayer[] layers = new ILayer[] { layer };
				if (layer.acceptsChilds()) {
					layers = layer.getLayersRecursively();
				}
				for (ILayer lyr : layers) {
					EditorManager em = Services.getService(EditorManager.class);
					IEditor[] editors = em.getEditor(new EditableLayer(element,
							lyr));
					for (IEditor editor : editors) {
						if (!em.closeEditor(editor)) {
							return false;
						}
					}
				}
			}
			return true;
		}

		public void layerRemoved(final LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				removeLayerListenerRecursively(layer, ll);
			}
			treeModel.refresh();
		}

		public void nameChanged(LayerListenerEvent e) {
		}

		public void styleChanged(LayerListenerEvent e) {
			treeModel.refresh();
		}

		public void visibilityChanged(LayerListenerEvent e) {
			treeModel.refresh();
		}

		public void selectionChanged(SelectionEvent e) {
			treeModel.refresh();
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
			treeModel.refresh();
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
		private String[] draggedResources;

		public MoveProcess(String[] draggedResources, ILayer dropNode) {
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
					.getService(DataManager.class);
			for (int i = 0; i < draggedResources.length; i++) {
				String sourceName = draggedResources[i];
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo(100 * i / draggedResources.length);
					try {
						dropNode.insertLayer(dataManager
								.createLayer(sourceName), index);
					} catch (LayerException e) {
						throw new RuntimeException("Cannot "
								+ "add the layer to the destination", e);
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

	public void setMapContext(EditableElement element) {

		// Remove the listeners
		if (this.mapContext != null) {
			removeLayerListenerRecursively(this.mapContext.getLayerModel(), ll);
			this.mapContext.removeMapContextListener(myMapContextListener);
		}

		if (element != null) {
			this.mapContext = ((MapContext) element.getObject());
			this.element = element;
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
					.getService(DataManager.class);
			treeModel = new TocTreeModel(dataManager
					.createLayerCollection("root"), getTree());
			ignoreSelection = true;
			this.setModel(treeModel);
			ignoreSelection = false;
			this.mapContext = null;
			this.element = null;

			// Patch to remove any reference to the previous model
			myTreeUI = new MyTreeUI();
			((MyTreeUI) tree.getUI()).dispose();
			tree.setUI(myTreeUI);
		}

	}

	boolean isActive(ILayer layer) {
		if (mapContext != null) {
			return layer == mapContext.getActiveLayer();
		} else {
			return false;
		}
	}

	class ScaleListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			MapTransform mt = mapEditor.getMapTransform();

			if (mt != null) {

				Envelope envelope = mt.getAdjustedExtent();
				if (envelope != null) {

					JComboBox cb = (JComboBox) e.getSource();
					String scale = (String) cb.getSelectedItem();

					if (scale.equals(SCALE_1000)) {

						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 1000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_5000)) {

						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 5000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_10000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 10000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_25000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 25000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_50000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 50000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_100000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 100000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_1000000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 1000000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_500000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 500000);

						mt.setExtent(envelope);

					}

					else if (scale.equals(SCALE_5000000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 5000000);

						mt.setExtent(envelope);

					}

					else if (scale.equals(SCALE_10000000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 10000000);

						mt.setExtent(envelope);

					}

				}
			}
		}

		private Envelope getEnveloppeFromScale(Envelope oldEnvelope,
				int panelWidth, int scale) {

			// -- get zoom factor
			double factor = scale
					/ getHorizontalMapScale(panelWidth, oldEnvelope);

			// --calculating new screen using the envelope of the corner
			// LineString

			double xc = 0.5 * (oldEnvelope.getMaxX() + oldEnvelope.getMinX());
			double yc = 0.5 * (oldEnvelope.getMaxY() + oldEnvelope.getMinY());
			double xmin = xc - 1 / 2.0 * factor * oldEnvelope.getWidth();
			double xmax = xc + 1 / 2.0 * factor * oldEnvelope.getWidth();
			double ymin = yc - 1 / 2.0 * factor * oldEnvelope.getHeight();
			double ymax = yc + 1 / 2.0 * factor * oldEnvelope.getHeight();
			Coordinate[] coords = new Coordinate[] {
					new Coordinate(xmin, ymin), new Coordinate(xmax, ymax) };
			Geometry g1 = new GeometryFactory().createLineString(coords);

			return g1.getEnvelopeInternal();
		}

		/**
		 *
		 * This method has been copied from openjump GIS :
		 * http://wwww.openjump.org
		 *
		 * OpenJUMP is distributed under GPL 2 license. Delivers the scale of
		 * the map shown on the display. The scale is calculated for the
		 * horizontal map direction
		 * <p>
		 * note: The scale may differ for horizontal and vertical direction due
		 * to the type of map projection.
		 *
		 * @param panel
		 *            width and current envelope
		 *
		 * @return actual scale
		 */

		public double getHorizontalMapScale(double panelWidth,
				Envelope oldEnvelope) {

			double horizontalScale = 0;
			// [sstein] maybe store screenres on the blackboard
			// if obtaining is processing intensive?
			double SCREENRES = Toolkit.getDefaultToolkit()
					.getScreenResolution(); // 72 dpi or 96 dpi or ..
			double INCHTOCM = 2.54; // cm

			// panelWidth in pixel
			double modelWidth = oldEnvelope.getWidth(); // m
			// -----
			// example:
			// screen resolution: 72 dpi
			// 1 inch = 2.54 cm
			// ratio = 2.54/72 (cm/pix) ~ 0.35mm
			// mapLength[cm] = noPixel * ratio
			// scale = realLength *100 [m=>cm] / mapLength
			// -----
			horizontalScale = modelWidth * 100
					/ (INCHTOCM / SCREENRES * panelWidth);

			return horizontalScale;
		}
	}

	public void setMapContext(EditableElement element, MapEditor mapEditor) {

		this.mapEditor = mapEditor;
		setMapContext(element);
	}

}
