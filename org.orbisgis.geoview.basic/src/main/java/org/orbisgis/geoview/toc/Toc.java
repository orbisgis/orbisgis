package org.orbisgis.geoview.toc;

import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.actions.IAction;
import org.orbisgis.core.actions.IActionFactory;
import org.orbisgis.core.actions.ISelectableAction;
import org.orbisgis.core.actions.MenuTree;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.geocatalog.resources.AbstractGdmsSource;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.TransferableResource;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.ViewContextListener;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerCollection;
import org.orbisgis.geoview.layerModel.LayerCollectionEvent;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.LayerListener;
import org.orbisgis.geoview.layerModel.LayerListenerEvent;
import org.orbisgis.geoview.layerModel.UnsupportedSourceException;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.ViewContext;

public class Toc extends ResourceTree {
	private MyLayerListener ll;

	private GeoView2D geoview;

	private TocRenderer tocRenderer;

	private TocTreeModel treeModel;

	private boolean ignoreSelection = false;

	private MyViewContextListener myViewContextListener;

	private MySourceListener mySourceListener;

	public Toc(final GeoView2D geoview) {
		this.geoview = geoview;

		this.ll = new MyLayerListener();

		ILayer root = geoview.getViewContext().getRootLayer();
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

		myViewContextListener = new MyViewContextListener();
		geoview.getViewContext().addViewContextListener(myViewContextListener);

		this.getTree().getSelectionModel().addTreeSelectionListener(
				new TreeSelectionListener() {

					public void valueChanged(TreeSelectionEvent e) {
						TreePath[] selectedPaths = Toc.this.getSelection();
						ILayer[] selected = new ILayer[selectedPaths.length];
						for (int i = 0; i < selected.length; i++) {
							selected[i] = (ILayer) selectedPaths[i]
									.getLastPathComponent();
						}
						ignoreSelection = true;
						geoview.getViewContext().setSelectedLayers(selected);
						ignoreSelection = false;
					}

				});

		mySourceListener = new MySourceListener(geoview);
		OrbisgisCore.getDSF().getSourceManager().addSourceListener(
				mySourceListener);
	}

	@Override
	public JPopupMenu getPopup() {
		MenuTree menuTree = new MenuTree();
		LayerActionFactory factory = new LayerActionFactory();
		EPTocLayerActionHelper.createPopup(menuTree, factory, this,
				"org.orbisgis.geoview.toc.LayerAction");
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
							PluginManager.error("Cannot move layer", e);
						}
					}
				} else {
					ILayer parent = dropNode.getParent();
					if (parent != null) {
						for (ILayer layer : draggedLayers) {
							if (layer.getParent() == dropNode.getParent()) {
								int index = parent.getIndex(dropNode);
								layer.moveTo(parent, index);
							}
						}
					}
				}
			} else if (trans.isDataFlavorSupported(TransferableResource
					.getResourceFlavor())) {
				IResource[] draggedResources;
				draggedResources = (IResource[]) trans
						.getTransferData(TransferableResource
								.getResourceFlavor());

				for (IResource resource : draggedResources) {
					if (resource.getResourceType() instanceof AbstractGdmsSource) {
						try {
							dropNode.put(LayerFactory.createLayer(resource
									.getName()));
						} catch (DriverLoadException e) {
							throw new RuntimeException(e);
						} catch (NoSuchTableException e) {
							throw new RuntimeException(e);
						} catch (DataSourceCreationException e) {
							throw new RuntimeException(e);
						} catch (CRSException e) {
							PluginManager.error("The resource and the "
									+ "existing layers have different CRS", e);
						} catch (LayerException e) {
							throw new RuntimeException("Cannot "
									+ "add the layer to the destination", e);
						} catch (UnsupportedSourceException e) {
							PluginManager.error("The specified resource "
									+ "cannot be used as a layer", e);
						}
					}
				}
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

	private final class MySourceListener implements SourceListener {
		private final GeoView2D geoview;

		private MySourceListener(GeoView2D geoview) {
			this.geoview = geoview;
		}

		public void sourceRemoved(final SourceRemovalEvent e) {
			LayerCollection.processLayersLeaves(geoview.getViewContext()
					.getRootLayer(), new DeleteLayerFromResourceAction(e));
		}

		public void sourceNameChanged(SourceEvent e) {
		}

		public void sourceAdded(SourceEvent e) {
		}
	}

	private final class MyViewContextListener implements ViewContextListener {
		public void layerSelectionChanged(ViewContext viewContext) {
			if (!ignoreSelection) {
				ILayer[] selected = viewContext.getSelectedLayers();
				TreePath[] selectedPaths = new TreePath[selected.length];
				for (int i = 0; i < selectedPaths.length; i++) {
					selectedPaths[i] = new TreePath(selected[i].getLayerPath());
				}

				Toc.this.setSelection(selectedPaths);
			}
		}
	}

	private final class DeleteLayerFromResourceAction implements
			org.orbisgis.geoview.layerModel.ILayerAction {

		private ArrayList<String> resourceNames = new ArrayList<String>();

		private DeleteLayerFromResourceAction(SourceRemovalEvent e) {
			String[] aliases = e.getNames();
			for (String string : aliases) {
				resourceNames.add(string);
			}

			resourceNames.add(e.getName());
		}

		public void action(ILayer layer) {
			String layerName = layer.getName();
			if (resourceNames.contains(layerName)) {
				layer.getParent().remove(layer);
			}
		}
	}

	private final class LayerActionFactory implements IActionFactory {
		private final class LayerActionDecorator implements IAction {
			private ILayerAction action;

			public LayerActionDecorator(Object action) {
				this.action = (ILayerAction) action;
			}

			public boolean isVisible() {
				TreePath[] res = getSelection();
				ILayerAction tocAction = (ILayerAction) action;
				boolean acceptsAllResources = true;
				if (tocAction.acceptsSelectionCount(res.length)) {
					for (TreePath resource : res) {
						ILayer layer = (ILayer) resource.getLastPathComponent();
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

			public boolean isEnabled() {
				return true;
			}

			public void actionPerformed() {
				EPTocLayerActionHelper.execute(geoview, action,
						toLayerArray(getSelection()));
			}
		}

		public IAction getAction(Object action) {
			return new LayerActionDecorator(action);
		}

		public ISelectableAction getSelectableAction(Object action) {
			throw new RuntimeException(
					"Bug. Layer actions cannot be selectable");
		}
	}

	private class MyLayerListener implements LayerListener {

		public void layerAdded(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				layer.addLayerListenerRecursively(ll);
			}
			treeModel.refresh();
		}

		public void layerMoved(LayerCollectionEvent e) {
			treeModel.refresh();
		}

		public void layerRemoved(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
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

	public void delete() {
		geoview.getViewContext().removeViewContextListener(
				myViewContextListener);
		OrbisgisCore.getDSF().getSourceManager().removeSourceListener(
				mySourceListener);
	}
}
