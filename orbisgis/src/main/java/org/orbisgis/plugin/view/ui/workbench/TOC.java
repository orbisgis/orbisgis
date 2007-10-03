package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Point;
import java.awt.Rectangle;
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
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.NullCRS;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.BasicLayer;
import org.orbisgis.plugin.view.layerModel.CRSException;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.MyNode;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.MyNodeTransferable;

import com.hardcode.driverManager.DriverLoadException;

public class TOC extends JTree implements DropTargetListener,
		DragGestureListener, DragSourceListener {

	static ILayer selectedLayer = null; // This contains the current Layer

	private LayerTreeCellRenderer ourTreeCellRenderer;

	private LayerTreeCellEditor ourTreeCellEditor;

	private JPopupMenu myPopup = null;

	// This contains the current tree path
	private TreePath selectedTreePath = null;

	VectorLayer vectorLayer = null;

	private LayerTreeModel model = null;

	// Used to create a transfer when dragging
	private DragSource source = null;

	private boolean is3D = false;

	/**
	 * Creates a Table Of Contents for a 2DViewer.
	 * 
	 * @param root
	 */
	public TOC(LayerCollection root) {
		this(root, false);
	}

	/**
	 * Creates a Table Of Content
	 * 
	 * @param root
	 * @param is3D
	 *            set this to true so you will have a special popup menu for the
	 *            3DViewer
	 */
	public TOC(LayerCollection root, boolean is3D) {
		this.is3D = is3D;
		model = new LayerTreeModel(root);
		setModel(model);
		// node's rendering
		ourTreeCellRenderer = new LayerTreeCellRenderer();
		setCellRenderer(ourTreeCellRenderer);
		// node's edition
		ourTreeCellEditor = new LayerTreeCellEditor(this);
		setCellEditor(ourTreeCellEditor);
		setInvokesStopCellEditing(true);
		setEditable(false);
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
		source = new DragSource();
		source.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_MOVE, this);
		new DropTarget(this, this);

		setRootVisible(false);
		setShowsRootHandles(true);
		addMouseListener(new MyMouseAdapter());
		model.setTree(this);
		initializePopupMenu();
	}

	private void addDatasource(MyNode myNode) {
		String name = myNode.toString();
		SpatialDataSourceDecorator ds = null;

		try {
			ds = new SpatialDataSourceDecorator(TempPluginServices.dsf
					.getDataSource(name));
			if (TypeFactory.IsSpatial(ds)) {
				vectorLayer = new VectorLayer(name, NullCRS.singleton);
				ds.open();
				TempPluginServices.dsf.getIndexManager().buildIndex(name,
						ds.getFieldName(ds.getSpatialFieldIndex()),
						SpatialIndex.SPATIAL_INDEX);
				ds.cancel();
				vectorLayer.setDataSource(ds);
				TempPluginServices.lc.put(vectorLayer, 0);
			}
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (NoSuchTableException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (DriverException e) {
			e.printStackTrace();
		} catch (IndexException e) {
			e.printStackTrace();
		} catch (CRSException e) {
			e.printStackTrace();
		}
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	public void dragEnter(DragSourceDragEvent dsde) {
	}

	public void dragEnter(DropTargetDragEvent evt) {
		// Called when the user is dragging and enters this drop target.
	}

	public void dragExit(DragSourceEvent dse) {
	}

	public void dragExit(DropTargetEvent evt) {
		// Called when the user is dragging and leaves this drop target.
	}

	public void dragGestureRecognized(DragGestureEvent dge) {
		setTreePath(dge.getDragOrigin());
		if (selectedLayer != null) {
			LayerTransferable data = new LayerTransferable(selectedLayer);
			if (data != null) {
				source.startDrag(dge, null, data, this);
			}
		}
	}

	public void dragOver(DragSourceDragEvent dsde) {
	}

	public void dragOver(DropTargetDragEvent evt) {
		setTreePath(evt.getLocation());
		// Called when the user is dragging and moves over this drop target.
	}

	public void drop(DropTargetDropEvent evt) {
		// Called when the user finishes or cancels the drag operation.
		// TODO : some threading here...

		Transferable transferable = evt.getTransferable();

		// Let's see if we received a MyNode or sth else...
		if (transferable.getTransferDataFlavors()[0].getParameter("name")
				.equals((MyNodeTransferable.myNodeFlavor.getParameter("name")))) {
			try {
				MyNode myNode = (MyNode) transferable
						.getTransferData(MyNodeTransferable.myNodeFlavor);

				int type = myNode.getType();
				String name = myNode.toString();
				switch (type) {
				case MyNode.datasource:
					addDatasource(myNode);
					break;

				case MyNode.sldfile:
					if (selectedTreePath != null) {
						final File sldFile = myNode.getFile();
						setSldStyle(sldFile, selectedLayer);
					}
					break;

				case MyNode.sqlquery:
					vectorLayer = new VectorLayer("Temp", NullCRS.singleton);
					try {
						vectorLayer
								.setDataSource(new SpatialDataSourceDecorator(
										TempPluginServices.dsf
												.executeSQL(myNode.getQuery())));
						TempPluginServices.lc.put(vectorLayer);
					} catch (SyntaxException e) {
						e.printStackTrace();
					} catch (DriverLoadException e) {
						e.printStackTrace();
					} catch (DriverException e) {
						e.printStackTrace();
					} catch (NoSuchTableException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (CRSException e) {
						e.printStackTrace();
					}
					break;

				case MyNode.sldlink:
					MyNode sourceNode = myNode.getParent();
					addDatasource(sourceNode);
					setSldStyle(myNode.getFile(), vectorLayer);
					break;

				case MyNode.raster:
					// TODO : clean the code
					CoordinateReferenceSystem crs = NullCRS.singleton;
					GeoRaster gcEsri = null;
					try {
						gcEsri = GeoRasterFactory.read(myNode.getFilePath());
						final RasterLayer esriGrid = new RasterLayer(name, crs);
						esriGrid.setGeoRaster(gcEsri);
						TempPluginServices.lc.put(esriGrid);
					} catch (CRSException e) {
						e.printStackTrace();
					}
					break;

				default:
				}

			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// So maybe we got a layer...let's change its position
		else if (transferable.getTransferDataFlavors()[0].getParameter("name")
				.equals((LayerTransferable.layerFlavor.getParameter("name")))) {

			try {
				ILayer draggedLayer = (ILayer) transferable
						.getTransferData(LayerTransferable.layerFlavor);

				Point point = evt.getLocation();
				TreePath droppedPath = getPathForLocation((int) point.getX(),
						(int) point.getY());
				int dropIndex = 0;
				int dragIndex = TempPluginServices.lc.getIndex(draggedLayer);

				// If we drop in void, put layer at bottom...
				if (droppedPath == null) {
					dropIndex = TempPluginServices.lc.size() - 1;
				} else {
					TOC.this.setSelectionPath(droppedPath);
					ILayer droppedLayer = (ILayer) droppedPath
							.getLastPathComponent();
					dropIndex = TempPluginServices.lc.getIndex(droppedLayer);
				}
				// now make the exchange
				if (dragIndex != dropIndex) {

					ILayer layer = TempPluginServices.lc.remove(draggedLayer
							.getName());
					TempPluginServices.lc.put(layer, dropIndex);

				}
			} catch (CRSException e) {
				e.printStackTrace();
			} catch (UnsupportedFlavorException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			updateUI();

		}
		evt.rejectDrop();

	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	public void dropActionChanged(DropTargetDragEvent evt) {
		// Called when the user changes the drag action between copy or move.
	}

	/**
	 * Edit here the popup menu. You have a boolean is3D to make the distinction
	 * between a 3D or a 2D viewer
	 * 
	 * @param is3D
	 */
	public void initializePopupMenu() {
		// Next line is used to make the popups behave as heavy components so
		// they get on top of the canvas
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		JMenuItem menuItem;
		ActionsListener acl = new ActionsListener();
		myPopup = new JPopupMenu();

		// Now edit the popup menu.
		if (!is3D) {
			menuItem = new JMenuItem("Import SLD");
			menuItem.setIcon(new ImageIcon(this.getClass().getResource(
					"geocatalog/sldStyle.png")));
			menuItem.addActionListener(acl);
			menuItem.setActionCommand("ADDSLD");
			myPopup.add(menuItem);
		} else {
			menuItem = new JMenuItem("Apply texture");
			menuItem.addActionListener(acl);
			menuItem.setActionCommand("TEXTURE");
			myPopup.add(menuItem);
		}

		menuItem = new JMenuItem("Zoom to layer");
		menuItem.setIcon(new ImageIcon(this.getClass().getResource(
				"zoomFull.png")));
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("ZOOMTOLAYER");
		myPopup.add(menuItem);

		menuItem = new JMenuItem("Open attributes");
		menuItem.setIcon(new ImageIcon(this.getClass().getResource(
				"openattributes.png")));
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("OPENATTRIBUTES");
		myPopup.add(menuItem);

		menuItem = new JMenuItem("Remove Layer");
		menuItem.setIcon(new ImageIcon(this.getClass()
				.getResource("remove.png")));
		menuItem.addActionListener(acl);
		menuItem.setActionCommand("DELLAYER");
		myPopup.add(menuItem);

	}

	private void setSldStyle(File sldFile, ILayer myLayer) {
		System.out.printf("=== %s : %s\n", sldFile, myLayer.getName());
		if (myLayer instanceof BasicLayer) {
			try {
				// ((BasicLayer) myLayer).setStyle(UtilStyle
				// .loadStyleFromXml(sldFile.getAbsolutePath()));
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}

	/**
	 * setTreePath allows to update the treePath and the currentLayer variables
	 * it should be called each time you need parameters of the current
	 * selection
	 * 
	 * @param e
	 * @return true if treePath isn't null
	 */
	private void setTreePath(Point e) {

		selectedTreePath = TOC.this.getPathForLocation((int) e.getX(), (int) e
				.getY());
		if (selectedTreePath != null) {
			TOC.this.setSelectionPath(selectedTreePath);
			selectedLayer = (ILayer) selectedTreePath.getLastPathComponent();
		}
	}

	/** ActionsListener is used to manage the events in the popup menu */
	private class ActionsListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// ADDSLD : applies a SLD file on the current Layer
			if ("ADDSLD".equals(e.getActionCommand())) {
				TempPluginServices.geoCatalog.show();

			} else if ("DELLAYER".equals(e.getActionCommand())) {
				TempPluginServices.lc.remove(selectedLayer.getName());
				updateUI();

			} else if ("ZOOMTOLAYER".equals(e.getActionCommand())) {
				TempPluginServices.vf.getGeoView2D().getMapControl().setExtent(
						selectedLayer.getEnvelope(),
						selectedLayer.getCoordinateReferenceSystem());
			} else if ("OPENATTRIBUTES".equals(e.getActionCommand())) {

				try {
					TempPluginServices.dsf
							.executeSQL("call show('select * from "
									+ selectedLayer.getName() + "','"
									+ selectedLayer.getName() + "' ) ;");
				} catch (SyntaxException e1) {
					e1.printStackTrace();
				} catch (DriverLoadException e1) {
					e1.printStackTrace();
				} catch (NoSuchTableException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/** MyMouseAdapter is used to manage mouse events in the TOC */
	private class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			final int x = e.getX();
			final int y = e.getY();
			final int mouseButton = e.getButton();
			int rowNodeLocation = TOC.this.getRowForLocation(x, y);
			Rectangle layerNodeLocation = TOC.this
					.getRowBounds(rowNodeLocation);

			if (selectedTreePath != null) {
				ILayer layer = selectedLayer;
				Rectangle checkBoxBounds = ourTreeCellRenderer
						.getCheckBoxBounds();
				checkBoxBounds.translate((int) layerNodeLocation.getX(),
						(int) layerNodeLocation.getY());
				if (checkBoxBounds.contains(e.getPoint())) {
					// mouse click inside checkbox
					layer.setVisible(!layer.isVisible());
				} else if ((MouseEvent.BUTTON1 == mouseButton)
						&& (2 <= e.getClickCount())) {
					startEditingAtPath(selectedTreePath);
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			setTreePath(new Point(e.getX(), e.getY()));
			ShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			ShowPopup(e);
		}

		private void ShowPopup(MouseEvent e) {
			if (e != null && selectedTreePath != null && e.isPopupTrigger()) {
				myPopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}