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

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.driver.DriverException;
import org.gdms.spatial.NullCRS;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.BasicLayer;
import org.orbisgis.plugin.view.layerModel.CRSException;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.ui.style.UtilStyle;

import com.hardcode.driverManager.DriverLoadException;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.GeoCatalog;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.MyNode;

public class TOC extends JTree implements DropTargetListener {
	private static final long serialVersionUID = 1L;

	private LayerTreeCellRenderer ourTreeCellRenderer;

	private LayerTreeCellEditor ourTreeCellEditor;

	private JPopupMenu myPopup = null;
	private TreePath currentTreePath=null;

	private ILayer currentLayer = null;// This contains the current Layer

	// selected. It is set by setTreePath in
	// MyMouse Adapter class

	public TOC(LayerCollection root) {
		LayerTreeModel model = new LayerTreeModel(root);
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
		new DropTarget(this, this);

		setRootVisible(false);
		setShowsRootHandles(true);
		addMouseListener(new MyMouseAdapter());
		model.setTree(this);
	}

	/** setTreePath allows to update the treePath and the currentLayer variables
	 *  it should be called each time you need parameters of the current selection
	 * 
	 * @param e
	 * @return true if treePath isn't null
	 */
	private void setTreePath (Point e) {
		currentTreePath = TOC.this.getPathForLocation((int)e.getX(), (int)e.getY());
		if (currentTreePath !=null) {
			TOC.this.setSelectionPath(currentTreePath);
			TOC.this.currentLayer = (ILayer) currentTreePath.getLastPathComponent();
		}
	}
	
	/** Edit here the popup menu */
	public void getPopupMenu() {
        JMenuItem menuItem;
        myPopup = new JPopupMenu();
		// Edit the popup menu.
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

    	MyNode myNode = GeoCatalog.getMyCatalog().getCurrentMyNode();
    	int type = myNode.getType();
    	String name = myNode.toString();
    	switch (type) {
    		case MyNode.datasource :
    			if ("Shapefile driver".equalsIgnoreCase(myNode.getDriverName())) {
    				final VectorLayer vectorLayer = new VectorLayer(name,
    						NullCRS.singleton);
    				try {
    					TempPluginServices.dsf.getIndexManager().buildIndex(name, "the_geom", SpatialIndex.SPATIAL_INDEX);
    					final DataSource ds = TempPluginServices.dsf.getDataSource(name);
    					final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
    					vectorLayer.setDataSource(sds);
    					TempPluginServices.lc.put(vectorLayer);
    				} catch (DataSourceCreationException ex) {
    					ex.printStackTrace();
    				} catch (DriverException ex) {
    					ex.printStackTrace();
    				} catch (CRSException ex) {
    					ex.printStackTrace();
    				} catch (DriverLoadException ex) {
    					ex.printStackTrace();
    				} catch (NoSuchTableException ex) {
    					ex.printStackTrace();
    				} catch (IndexException ex) {
    					ex.printStackTrace();
    				}
    			} else evt.rejectDrop();
    			break;
    		
    		case MyNode.sldfile :
    			if (currentTreePath!=null) {
    				final File sldFile = myNode.getFile();
    				ILayer myLayer = TOC.this.currentLayer;
    				System.out.printf("=== %s : %s\n", sldFile, myLayer.getName());
    				if (myLayer instanceof BasicLayer) {
    					try {
    						((BasicLayer) myLayer).setStyle(UtilStyle
    								.loadStyleFromXml(sldFile
    										.getAbsolutePath()));
    					} catch (Exception ee) {
    						ee.printStackTrace();
    					}
    				}
    			} else evt.rejectDrop();
				break;
    		default : evt.rejectDrop();
    	}
    }
    
	/** MyMouseAdapter is used to manage mouse events in the TOC */
	private class MyMouseAdapter extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			setTreePath(new Point(e.getX(),e.getY()));
			ShowPopup(e);
        }

		public void mouseReleased(MouseEvent e) {
			ShowPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			final int x = e.getX();
			final int y = e.getY();
			final int mouseButton = e.getButton();
			int rowNodeLocation = TOC.this.getRowForLocation(x, y);
			Rectangle layerNodeLocation = TOC.this
					.getRowBounds(rowNodeLocation);

			if (currentTreePath!=null) {
				ILayer layer = TOC.this.currentLayer;
				Rectangle checkBoxBounds = ourTreeCellRenderer
						.getCheckBoxBounds();
				checkBoxBounds.translate((int) layerNodeLocation.getX(),
						(int) layerNodeLocation.getY());
				System.out.println(e.getButton());
				if (checkBoxBounds.contains(e.getPoint())) {
					// mouse click inside checkbox
					layer.setVisible(!layer.isVisible());
				} else if ((MouseEvent.BUTTON1 == mouseButton)
						&& (2 <= e.getClickCount())) {
					startEditingAtPath(currentTreePath);
				}
			}
		}

		private void ShowPopup(MouseEvent e) {
            if (e!=null && currentTreePath !=null && e.isPopupTrigger()) {
                myPopup.show(e.getComponent(), e.getX(), e.getY());
                System.out.println("Popup sur " + TOC.this.currentLayer.getName());
            }
        }
	}

	/** ActionsListener is used to manage the events in the popup menu */
	private class ActionsListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			//DELLAYER : remove the current layer
			if ("DELLAYER".equals(e.getActionCommand())) {
				TempPluginServices.lc.remove(currentLayer.getName());
				updateUI();
			} else if ("ZOOMTOLAYER".equals(e.getActionCommand())) {
				TempPluginServices.vf.getGeoView2D().getMapControl().setExtent(
						currentLayer.getEnvelope(),
						currentLayer.getCoordinateReferenceSystem());
			}
		}
	}
}