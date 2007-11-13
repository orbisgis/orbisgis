package org.urbsat.plugin.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.geoview.GeoView2D;

public class UrbsatPanel extends JPanel {

	private GeoView2D geoview;
	public static DefaultMutableTreeNode racine;
	 static DefaultTreeModel m_model;
	 
	 private DefaultTreeModel treeModel;
		private DefaultMutableTreeNode folderAerodynamic;
		private DefaultMutableTreeNode folderLandcover;
		private DefaultMutableTreeNode folderOthers;
		static HashMap<String, String> queries;
		private DefaultMutableTreeNode rootNode;
		
		private JScrollPane jScrollPane;
		private JTree tree;
		
		

	public UrbsatPanel(GeoView2D geoview) {
		this.geoview = geoview;
		initialize();
	}


	private void initialize() {
		this.setLayout(new BorderLayout());
		
		this.add(getJScrollPane());
	}
	
	
	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();

			jScrollPane.setViewportView(getTree());
		}
		return jScrollPane;
	}
	
	
	private JTree getTree() {


		rootNode = new DefaultMutableTreeNode();;
		queries = new HashMap<String, String>();

		folderAerodynamic = new DefaultMutableTreeNode("Aerodynamic indicators");

		 folderLandcover = new DefaultMutableTreeNode("Landcover indicators");

		 folderOthers = new DefaultMutableTreeNode("Others");


		tree = new JTree(rootNode);
		//Customized JTree icons.
		DefaultTreeCellRenderer myRenderer = new DefaultTreeCellRenderer();

		//Changement de l'icône pour les feuilles de l'arbre.
		myRenderer.setLeafIcon(new ImageIcon(this.getClass().getResource("map.png")));
		//Changement de l'icône pour les noeuds fermés.
		myRenderer.setClosedIcon(new ImageIcon(this.getClass().getResource("folder.png")));
		//Changement de l'icône pour les noeuds ouverts.
		myRenderer.setOpenIcon(new ImageIcon(this.getClass().getResource("folder_magnify.png")));

		//Application de l'afficheur à l'arbre.
		tree.setCellRenderer(myRenderer);

		rootNode.add(folderAerodynamic);
		rootNode.add(folderLandcover);
		rootNode.add(folderOthers);
		addQueries ();

		tree.expandPath(new TreePath( rootNode.getPath()));
		tree.setRootVisible(false);
		tree.setDragEnabled(true);
		tree.addMouseListener(new MyMouseAdapter());

		return tree;


	}
	
	
	protected class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			showPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
		}

		private void showPopup(MouseEvent e) {
			
			if (e.getButton() == MouseEvent.BUTTON3) {
				TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				TreePath[] selectionPaths = tree.getSelectionPaths();
				if ((selectionPaths != null) && (path != null)) {
					if (!contains(selectionPaths, path)) {
						tree.setSelectionPath(path);
					}
				} else {
					tree.setSelectionPath(path);
				}
			}
			TreePath tp = tree.getSelectionPath();
			
			if (e.isPopupTrigger()) {
				getPopup().show(e.getComponent(), e.getX(), e.getY());
			}
		}

		private boolean contains(TreePath[] selectionPaths, TreePath path) {
			for (TreePath treePath : selectionPaths) {
				boolean equals = true;
				Object[] objectPath = treePath.getPath();
				Object[] testPath = path.getPath();
				if (objectPath.length != testPath.length) {
					equals = false;
				} else {
					for (int i = 0; i < testPath.length; i++) {
						if (testPath[i] != objectPath[i]) {
							equals = false;
						}
					}
				}
				if (equals) {
					return true;
				}
			}

			return false;
		}
	}
	
	public  JPopupMenu getPopup() {
		
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Execute");
		popupMenu.add(menuItem );
		
		
		return popupMenu;
		
		
	}
	
	
	
	public static String getQuery(String name){


		   return queries.get(name);

	   }

	   public HashMap<String, String> addQuery(String name, String query, DefaultMutableTreeNode father){
		   DefaultMutableTreeNode child = new DefaultMutableTreeNode(name);
		   father.add(child);
		   queries.put(name, query);


		   return queries;

	   }


	   public void addQueries () {

		   
		   //folderAerodynamic
		   addQuery("Average Build Height", "select * ...", folderAerodynamic);
		  
		   //folderLandcover
		   addQuery("Grass Density", "select Density(a.the_geom, b.the_geom) from table a, table b where type = grass;", folderLandcover);

		   
		   //folderOthers
		   addQuery("Create Grid", "select creategrid(x_size, y_size, orientation", folderOthers);
			 


	   }


}
