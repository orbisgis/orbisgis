package org.orbisgis.plugin.sqlconsole.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.plugin.sqlconsole.actions.ActionsListener;



public class SQLConsolePanel extends JPanel{



	private JButton executeBT = null;
	private JButton eraseBT = null;


	private JButton saveQuery = null;
	private JButton openQuery = null;
	private JButton stopQueryBt = null;
	public static DefaultMutableTreeNode racine;
	 static DefaultTreeModel m_model;

	public static JButton jButtonNext = null;
	public static JButton jButtonPrevious = null;
	static JButton tableViewBt = null;

	ActionsListener acl = new ActionsListener();
	private JScrollPane jScrollPane2;

	static HashMap<String, String> queries;
	private DefaultMutableTreeNode rootNode;
	private JSplitPane splitPanel;
	private JPanel centerPanel;
	
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode folderData;
	private DefaultMutableTreeNode folderSpatial;
	private DefaultMutableTreeNode folderUtilities;
	


	/**
	 * This is the default constructor
	 */
	public SQLConsolePanel() {
		super();

		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getNorthPanel(), BorderLayout.NORTH);
		this.add(getCenterPanel(),BorderLayout.CENTER);

	}

	private JPanel getNorthPanel() {

		JPanel northPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		northPanel.setLayout(flowLayout);


		northPanel.add(getExecuteBT(), null);
		northPanel.add(getEraseBT(), null);
		northPanel.add(getStopQueryBt(), null);

		northPanel.add(getJButtonPrevious(), null);
		northPanel.add(getJButtonNext(), null);

		northPanel.add(getOpenQuery(), null);
		northPanel.add(getSaveQuery(), null);

		return northPanel;

	}

	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());;
			centerPanel.add(getSplitPane(), BorderLayout.CENTER);
		}

		return centerPanel;

	}



	private Component getSplitPane() {
		if (splitPanel == null) {
			splitPanel = new JSplitPane();
			splitPanel.setLeftComponent(new ScrollPaneWest());
			splitPanel.setRightComponent(getJScrollPaneEast());
			splitPanel.setOneTouchExpandable(true);
			splitPanel.setResizeWeight(1);
			splitPanel.setContinuousLayout(true);
			splitPanel.setPreferredSize(new Dimension(400, 140));
		}

		return splitPanel;
	}

	/**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getExecuteBT() {
		if (executeBT == null) {
			executeBT = new JButton();
			executeBT.setMargin(new Insets(0,0,0,0));
			executeBT.setText("");
			executeBT.setToolTipText("Click to execute query");
			executeBT.setIcon(new ImageIcon(getClass().getResource("Execute.png")));
			executeBT.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
			executeBT.setActionCommand("EXECUTE");
			executeBT.addActionListener(acl);


		}
		return executeBT;
	}




	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getEraseBT() {
		if (eraseBT == null) {
			eraseBT = new JButton();
			eraseBT.setMargin(new Insets(0,0,0,0));
			eraseBT.setText("");
			eraseBT.setIcon(new ImageIcon(getClass().getResource("Erase.png")));
			eraseBT.setFont(new Font("Dialog", Font.BOLD, 10));
			eraseBT.setToolTipText("Clear the query");
			eraseBT.setActionCommand("ERASE");
			eraseBT.addActionListener(acl);
		}
		return eraseBT;
	}



	/**
	 * This method initializes saveQuery
	 *
	 * Elle permet d'ouvrir une interface d'ouverture de fenetre.
	 * @return javax.swing.JButton
	 */
	private JButton getSaveQuery() {
		if (saveQuery == null) {
			saveQuery = new JButton();
			saveQuery.setMargin(new Insets(0,0,0,0));
			saveQuery.setIcon(new ImageIcon(getClass().getResource("Save.png")));
			saveQuery.setActionCommand("SAVEQUERY");
			saveQuery.addActionListener(acl);


		}
		return saveQuery;
	}

	/**
	 * This method initializes openQuery
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOpenQuery() {
		if (openQuery == null) {
			openQuery = new JButton();
			openQuery.setMargin(new Insets(0,0,0,0));

			openQuery.setIcon(new ImageIcon(getClass().getResource("Open.png")));
			openQuery.setActionCommand("OPENSQLFILE");
			openQuery.addActionListener(acl);
		}
		return openQuery;
	}

	/**
	 * This method initializes stopQueryBt
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getStopQueryBt() {
		if (stopQueryBt == null) {
			stopQueryBt = new JButton();
			stopQueryBt.setMargin(new Insets(0,0,0,0));
			stopQueryBt.setFont(new Font("Dialog", Font.BOLD, 10));
			stopQueryBt.setToolTipText("Stop the query");
			stopQueryBt.setIcon(new ImageIcon(getClass().getResource("Stop.png")));
			stopQueryBt.setText("");
			stopQueryBt.setMnemonic(KeyEvent.VK_UNDEFINED);
			stopQueryBt.setEnabled(true);
		}
		return stopQueryBt;
	}





	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneEast() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();

			jScrollPane2.setViewportView(getTree());
		}
		return jScrollPane2;
	}




	/**
	 * This method initializes jButtonNext
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonNext() {
		if (jButtonNext == null) {
			jButtonNext = new JButton();
			jButtonNext.setMargin(new Insets(0,0,0,0));
			jButtonNext.setIcon(new ImageIcon(getClass().getResource("go-next.png")));

			jButtonNext.setFont(new Font("Dialog", Font.BOLD, 10));
			jButtonNext.setToolTipText("Next query");
			jButtonNext.setActionCommand("NEXT");
			jButtonNext.addActionListener(acl);

		}
		return jButtonNext;
	}

	/**
	 * This method initializes jButtonPrevious
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonPrevious() {
		if (jButtonPrevious == null) {
			jButtonPrevious = new JButton();
			jButtonPrevious.setMargin(new Insets(0,0,0,0));

			jButtonPrevious.setIcon(new ImageIcon(getClass().getResource("go-previous.png")));

			jButtonPrevious.setFont(new Font("Dialog", Font.BOLD, 10));
			jButtonPrevious.setToolTipText("Previous query");

			jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {



	            }
	        });



		}
		return jButtonPrevious;
	}

	private JTree getTree() {


		rootNode = new DefaultMutableTreeNode();;
		queries = new HashMap<String, String>();
		
		folderData = new DefaultMutableTreeNode("Register");
		
		 folderSpatial = new DefaultMutableTreeNode("Spatial");
		
		 folderUtilities = new DefaultMutableTreeNode("Utility");

		
		JTree tree = new JTree(rootNode);
		//Customized JTree icons.
		DefaultTreeCellRenderer myRenderer = new DefaultTreeCellRenderer();

		//Changement de l'icône pour les feuilles de l'arbre.
		myRenderer.setLeafIcon(new ImageIcon(this.getClass().getResource("help.png")));
		//Changement de l'icône pour les noeuds fermés.
		myRenderer.setClosedIcon(new ImageIcon(this.getClass().getResource("folder.png")));
		//Changement de l'icône pour les noeuds ouverts.
		myRenderer.setOpenIcon(new ImageIcon(this.getClass().getResource("open_folder.png")));

		//Application de l'afficheur à l'arbre.
		tree.setCellRenderer(myRenderer);

		rootNode.add(folderData);
		rootNode.add(folderSpatial);
		rootNode.add(folderUtilities);
		addQueries ();
		
		tree.expandPath(new TreePath( rootNode.getPath()));
		tree.setRootVisible(false);
		tree.setDragEnabled(true);
		tree.setPreferredSize(new Dimension(100, 100));

		return tree;


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
		   
		   //Register node
		   addQuery("File", "call register('/tmp/myshape.shp','aName')", folderData);
		   addQuery("H2 database", "call register('h2','', '0', 'path+databaseName','','','tableName', 'name')", folderData);
		   
		   //Spatial node
		   addQuery("Buffer", "select Buffer(geomcolumn) from table a;", folderSpatial);
			
		   addQuery("Intersection", "select Intersection(a.geomcolumn, b.geomcolumn) from table a, table1 b where Intersects(a.geomcolumn, b.geomcolumn);", folderSpatial);
		  
		   //Utility node
		   addQuery("Show", "Call SHOW ('select * from table');", folderUtilities);
			
		   
		   
	   }
	
	
}
