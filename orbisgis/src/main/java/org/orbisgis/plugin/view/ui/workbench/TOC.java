package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Point;
import java.awt.Rectangle;
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.gdms.data.DataSource;
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
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.BasicLayer;
import org.orbisgis.plugin.view.layerModel.CRSException;
import org.orbisgis.plugin.view.layerModel.GridCoverageReader;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.ui.style.UtilStyle;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.GeoCatalog;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.MyNode;

import com.hardcode.driverManager.DriverLoadException;

public class TOC extends JTree implements DropTargetListener {
	private static final long serialVersionUID = 1L;

	private LayerTreeCellRenderer ourTreeCellRenderer;

	private LayerTreeCellEditor ourTreeCellEditor;

	private JPopupMenu myPopup = null;

	private TreePath selectedTreePath = null; // This contains the current
												// tree path

	static ILayer selectedLayer = null; // This contains the current Layer

	VectorLayer vectorLayer = null;

	private boolean DragInTOC = false; // Useful to determine if we dragged in
										// TOC or elsewhere...

	private LayerTreeModel model = null;

	public TOC(LayerCollection root) {
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
		getPopupMenu(); // Add the popup menu to the tree
		setDragEnabled(true); // Enables drag on itself
		new DropTarget(this, this);

		setRootVisible(false);
		setShowsRootHandles(true);
		addMouseListener(new MyMouseAdapter());
		model.setTree(this);
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
		if (!DragInTOC) {
			selectedTreePath = TOC.this.getPathForLocation((int) e.getX(),
					(int) e.getY());
			if (selectedTreePath != null) {
				TOC.this.setSelectionPath(selectedTreePath);
				TOC.this.selectedLayer = (ILayer) selectedTreePath
						.getLastPathComponent();
			}
		}
	}

	/** Edit here the popup menu */
	public void getPopupMenu() {
		JMenuItem menuItem;
		myPopup = new JPopupMenu();
		// Edit the popup menu.
		menuItem = new JMenuItem("Import SLD");
		menuItem.setIcon(new ImageIcon(this.getClass().getResource(
				"geocatalog/sldStyle.png")));
		menuItem.addActionListener(new ActionsListener());
		menuItem.setActionCommand("ADDSLD");
		myPopup.add(menuItem);

		menuItem = new JMenuItem("Remove Layer");
		menuItem.setIcon(new ImageIcon(this.getClass()
				.getResource("remove.png")));
		menuItem.addActionListener(new ActionsListener());
		menuItem.setActionCommand("DELLAYER");
		myPopup.add(menuItem);

		menuItem = new JMenuItem("Zoom to layer");
		menuItem.setIcon(new ImageIcon(this.getClass().getResource(
				"zoomFull.png")));
		menuItem.addActionListener(new ActionsListener());
		menuItem.setActionCommand("ZOOMTOLAYER");
		myPopup.add(menuItem);

		menuItem = new JMenuItem("Open attributes");
		menuItem.setIcon(new ImageIcon(this.getClass().getResource(
				"openattributes.png")));
		menuItem.addActionListener(new ActionsListener());
		menuItem.setActionCommand("OPENATTRIBUTES");
		myPopup.add(menuItem);

	}

	public void dragEnter(DropTargetDragEvent evt) {
		// Called when the user is dragging and enters this drop target.
	}

	public void dragOver(DropTargetDragEvent evt) {
		setTreePath(evt.getLocation());
		// Called when the user is dragging and moves over this drop target.
	}

	public void dragExit(DropTargetEvent evt) {
		// Called when the user is dragging and leaves this drop target.
	}

	public void dropActionChanged(DropTargetDragEvent evt) {
		// Called when the user changes the drag action between copy or move.
	}

	public void drop(DropTargetDropEvent evt) {
		// Called when the user finishes or cancels the drag operation.
		// Let's see if the user dragged in the TOC or elsewhere
		if (DragInTOC) {
			ILayer draggedLayer = selectedLayer;
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
				try {
					ILayer layer = TempPluginServices.lc.remove(draggedLayer
							.getName());
					TempPluginServices.lc.put(layer, dropIndex);
				} catch (CRSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			updateUI();
			DragInTOC = false;

			// So we dragged from the GeoCatalog...
		} else {
			MyNode myNode = GeoCatalog.getMyCatalog().getCurrentMyNode();
			int type = myNode.getType();
			String name = myNode.toString();
			switch (type) {
			case MyNode.datasource:
				addDatasource(myNode);
				evt.rejectDrop();
				break;

			case MyNode.sldfile:
				if (selectedTreePath != null) {
					final File sldFile = myNode.getFile();
					setSldStyle(sldFile, selectedLayer);
				} else
					evt.rejectDrop();
				break;

			case MyNode.sqlquery:
				vectorLayer = new VectorLayer("Temp", NullCRS.singleton);
				try {
					vectorLayer.setDataSource(new SpatialDataSourceDecorator(
							TempPluginServices.dsf.executeSQL(name)));
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
				DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) myNode
						.getTreeNode().getParent();
				MyNode sourceMyNode = (MyNode) sourceNode.getUserObject();
				addDatasource(sourceMyNode);
				setSldStyle(myNode.getFile(), vectorLayer);
				break;

			case MyNode.raster:
				// TODO : clean the code
				CoordinateReferenceSystem crs = NullCRS.singleton;
				GridCoverage gcEsri = null;
				try {
					gcEsri = new GridCoverageReader(myNode.getFile()).getGc();
					RasterLayer esriGrid = new RasterLayer(name, crs);
					esriGrid.setGridCoverage(gcEsri);
					TempPluginServices.lc.put(esriGrid);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (CRSException e) {
					e.printStackTrace();
				}
				break;

			default:
				evt.rejectDrop();
			}
		}

	}

	private void setSldStyle(File sldFile, ILayer myLayer) {
		System.out.printf("=== %s : %s\n", sldFile, myLayer.getName());
		if (myLayer instanceof BasicLayer) {
			try {
				((BasicLayer) myLayer).setStyle(UtilStyle
						.loadStyleFromXml(sldFile.getAbsolutePath()));
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}

	private void addDatasource(MyNode myNode) {
		String name = myNode.toString();
		DataSource ds = null;

		try {
			ds = TempPluginServices.dsf.getDataSource(name);
			if (TypeFactory.IsSpatial(ds)) {
				vectorLayer = new VectorLayer(name, NullCRS.singleton);
				TempPluginServices.dsf.getIndexManager().buildIndex(name,
						"the_geom", SpatialIndex.SPATIAL_INDEX);
				final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
						ds);
				vectorLayer.setDataSource(sds);
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

	/** MyMouseAdapter is used to manage mouse events in the TOC */
	private class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			setTreePath(new Point(e.getX(), e.getY()));
			// Maybe we begin a drop in TOC, so set the var...
			if (selectedTreePath != null) {
				DragInTOC = true;
			} else {
				DragInTOC = false;
			}
			ShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			ShowPopup(e);
			// We released the mouse so there was no drag...
			DragInTOC = false;
		}

		public void mouseClicked(MouseEvent e) {
			final int x = e.getX();
			final int y = e.getY();
			final int mouseButton = e.getButton();
			int rowNodeLocation = TOC.this.getRowForLocation(x, y);
			Rectangle layerNodeLocation = TOC.this
					.getRowBounds(rowNodeLocation);

			if (selectedTreePath != null) {
				ILayer layer = TOC.this.selectedLayer;
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

		private void ShowPopup(MouseEvent e) {
			if (e != null && selectedTreePath != null && e.isPopupTrigger()) {
				myPopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	/** ActionsListener is used to manage the events in the popup menu */
	private class ActionsListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// ADDSLD : applies a SLD file on the current Layer
			if ("ADDSLD".equals(e.getActionCommand())) {
				TempPluginServices.geoCatalog.jFrame.toFront();

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
}