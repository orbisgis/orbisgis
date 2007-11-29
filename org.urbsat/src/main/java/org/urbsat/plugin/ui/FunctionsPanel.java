package org.urbsat.plugin.ui;

import java.awt.BorderLayout;
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

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.sqlConsole.ui.SQLConsolePanel;
import org.urbsat.kmeans.KMeans;
import org.urbsat.landcoverIndicators.custom.Density;
import org.urbsat.landcoverIndicators.function.Chaillou;
import org.urbsat.utilities.CreateGrid;
import org.urbsat.utilities.GetZDEM;

public class FunctionsPanel extends JPanel {

	private GeoView2D geoview;

	public static DefaultMutableTreeNode racine;

	static DefaultTreeModel m_model;

	private DefaultTreeModel treeModel;

	private DefaultMutableTreeNode folderAerodynamic;

	private DefaultMutableTreeNode folderLandcover;

	private DefaultMutableTreeNode folderOthers;

	private DefaultMutableTreeNode folderMorphological;

	static HashMap<String, String> queries;

	private DefaultMutableTreeNode rootNode;

	private JScrollPane jScrollPane;

	private JTree tree;

	public FunctionsPanel(DescriptionScrollPane descriptionScrollPane) {
//		this.geoview = descriptionScrollPane;
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
		rootNode = new DefaultMutableTreeNode();
		queries = new HashMap<String, String>();

		folderLandcover = new DefaultMutableTreeNode("Landcover indicators");
		folderMorphological = new DefaultMutableTreeNode(
				"Morphological indicators");
		folderAerodynamic = new DefaultMutableTreeNode("Aerodynamic indicators");
		folderOthers = new DefaultMutableTreeNode("Others");

		tree = new JTree(rootNode);
		// Customized JTree icons.
		DefaultTreeCellRenderer myRenderer = new DefaultTreeCellRenderer();

		// Changement de l'icône pour les feuilles de l'arbre.
		myRenderer.setLeafIcon(new ImageIcon(this.getClass().getResource(
				"map.png")));
		// Changement de l'icône pour les noeuds fermés.
		myRenderer.setClosedIcon(new ImageIcon(this.getClass().getResource(
				"folder.png")));
		// Changement de l'icône pour les noeuds ouverts.
		myRenderer.setOpenIcon(new ImageIcon(this.getClass().getResource(
				"folder_magnify.png")));

		// Application de l'afficheur à l'arbre.
		tree.setCellRenderer(myRenderer);

		rootNode.add(folderLandcover);
		rootNode.add(folderMorphological);
		rootNode.add(folderAerodynamic);
		rootNode.add(folderOthers);
		addQueries();

		expandAll();
		// tree.expandPath(new TreePath(rootNode.getPath()));
		tree.setRootVisible(false);
		tree.setDragEnabled(true);
		tree.addMouseListener(new MyMouseAdapter());

		return tree;
	}

	public void expandAll() {
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}

	protected class MyMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
			mouseClicked(e);
		}

		public void mouseClicked(MouseEvent e) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			if (node == null) {
				return;
			} else {
				if (node.isLeaf()) {
					DescriptionScrollPane.jTextArea.setText(getQuery(node
							.getUserObject().toString()));
				} else {
				}
			}
		}

		private void showPopup(MouseEvent e) {
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			if (node == null) {
				return;
			} else {
				if (node.isLeaf()) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						final TreePath path = tree.getPathForLocation(e.getX(),
								e.getY());
						final TreePath[] selectionPaths = tree
								.getSelectionPaths();
						if ((selectionPaths != null) && (path != null)) {
							if (!contains(selectionPaths, path)) {
								tree.setSelectionPath(path);
							}
						} else {
							tree.setSelectionPath(path);
						}
					}
					final TreePath tp = tree.getSelectionPath();
					if (e.isPopupTrigger()) {
						getPopup().show(e.getComponent(), e.getX(), e.getY());
					}

				}

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

	public JPopupMenu getPopup() {

		JPopupMenu popupMenu = new JPopupMenu();

		popupMenu.add(getExecuteMenu());

		return popupMenu;

	}

	public JMenuItem getExecuteMenu() {
		JMenuItem menuItem = new JMenuItem("Execute");
		menuItem.setIcon(new ImageIcon(getClass().getResource("cog_go.png")));

		return menuItem;

	}

	public static String getQuery(String name) {

		return queries.get(name);

	}

	public HashMap<String, String> addQuery(String name, Class queryClassName,
			DefaultMutableTreeNode father) {
		final DefaultMutableTreeNode child = new DefaultMutableTreeNode(name);
		father.add(child);
		try {
			final Object newInstance = queryClassName.newInstance();
			if (newInstance instanceof CustomQuery) {
				final CustomQuery customQuery = (CustomQuery) queryClassName
						.newInstance();
				queries.put(name, customQuery.getDescription());
				SQLConsolePanel.addExternalQuery(name, customQuery
						.getSqlOrder());
			} else if (newInstance instanceof Function) {
				final Function function = (Function) queryClassName
						.newInstance();
				queries.put(name, (function.getDescription()));
				SQLConsolePanel.addExternalQuery(name, function.getSqlOrder());
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return queries;

	}

	public void addQueries() {
		// folderLandcover
		addQuery("Grass Density", Density.class, folderLandcover);
		addQuery("Building Density", Density.class, folderLandcover);
		addQuery("Vegetation Density", Density.class, folderLandcover);
		addQuery("Roads Density", Density.class, folderLandcover);

		// folderMorphological
		addQuery("Mean height", Density.class, folderMorphological);
		addQuery("Mean volume", Density.class, folderMorphological);
		addQuery("Perimeter", Density.class, folderLandcover);
		addQuery("Compacity", Density.class, folderLandcover);
		addQuery("Building numbers per cell", Density.class, folderLandcover);
		addQuery("Mean space", Density.class, folderLandcover);

		// folderAerodynamic
		addQuery("Average Build Height", Density.class, folderAerodynamic);

		// folderOthers
		addQuery("Create Grid", CreateGrid.class, folderOthers);
		addQuery("Create Oriented Grid", CreateGrid.class, folderOthers);
		addQuery("K-means", KMeans.class, folderOthers);
		addQuery("GetZDEM", GetZDEM.class, folderOthers);
		addQuery("Chaillou classification", Chaillou.class, folderOthers);
	}
}