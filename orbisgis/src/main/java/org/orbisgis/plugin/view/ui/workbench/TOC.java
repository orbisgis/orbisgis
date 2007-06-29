package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
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
import org.orbisgis.plugin.view.ui.workbench.geocatalog.GeoCatalog;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.MyNode;

import com.hardcode.driverManager.DriverLoadException;

public class TOC extends JTree implements DropTargetListener {
	private static final long serialVersionUID = 1L;

	private LayerTreeCellRenderer ourTreeCellRenderer;

	private LayerTreeCellEditor ourTreeCellEditor;

	private JPopupMenu myPopup = null;
	
	private TreePath selectedTreePath=null;//This contains the current tree path

	static ILayer selectedLayer = null;// This contains the current Layer
	
	VectorLayer vectorLayer = null;

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
		selectedTreePath = TOC.this.getPathForLocation((int)e.getX(), (int)e.getY());
		if (selectedTreePath !=null) {
			TOC.this.setSelectionPath(selectedTreePath);
			TOC.this.selectedLayer = (ILayer) selectedTreePath.getLastPathComponent();
		}
	}
	

	/** Edit here the popup menu */
	public void getPopupMenu() {
		JMenuItem menuItem;
		myPopup = new JPopupMenu();
		// Edit the popup menu.
		menuItem = new JMenuItem("Import SLD");
		menuItem.setIcon(new ImageIcon(this.getClass().getResource(
				"sldStyle.png")));
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

    	MyNode myNode = GeoCatalog.getMyCatalog().getCurrentMyNode();
    	int type = myNode.getType();
    	String name = myNode.toString();
    	switch (type) {
    		case MyNode.datasource :
    			if ("Shapefile driver".equalsIgnoreCase(myNode.getDriverName())) {
    				 vectorLayer = new VectorLayer(name,
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
    			}
    			//Applies linked style if any
    			DefaultMutableTreeNode node=myNode.getTreeNode();
    			
    			if (!node.isLeaf()) {
    				int count = node.getChildCount();
    				for (int i=0; i<count; i++) {
    					DefaultMutableTreeNode linkNode = (DefaultMutableTreeNode)node.getChildAt(i);
    					MyNode myLinkNode = (MyNode)linkNode.getUserObject();
    					File sldFile = myLinkNode.getFile();
    					setSldStyle(sldFile,vectorLayer);
    				}
    				
    			}
    			evt.rejectDrop();
    			break;
    		
    		case MyNode.sldfile :
    			if (selectedTreePath!=null) {
    				final File sldFile = myNode.getFile();
    				setSldStyle(sldFile,selectedLayer);
    			} else evt.rejectDrop();
				break;
    		case MyNode.sqlquery :
    			
    			 vectorLayer = new VectorLayer("Temp", NullCRS.singleton);
    			 
    			 try {
					vectorLayer.setDataSource(new SpatialDataSourceDecorator(TempPluginServices.dsf.executeSQL(name)));
					TempPluginServices.lc.put(vectorLayer);
    			 } catch (SyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DriverLoadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchTableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CRSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			
    			break;
    			
    		default : evt.rejectDrop();
    	}
	}
	
	private void setSldStyle(File sldFile, ILayer myLayer) {
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

			if (selectedTreePath!=null) {
				ILayer layer = TOC.this.selectedLayer;
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
					startEditingAtPath(selectedTreePath);
				}
			}
		}

		private void ShowPopup(MouseEvent e) {
			if (e!=null && selectedTreePath !=null && e.isPopupTrigger()) {
                myPopup.show(e.getComponent(), e.getX(), e.getY());
                System.out.println("Popup sur " + TOC.this.selectedLayer.getName());
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
					TempPluginServices.dsf.executeSQL("call show('select * from "+ selectedLayer.getName() + "') ;");
				} catch (SyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DriverLoadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchTableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
		}
	}
}