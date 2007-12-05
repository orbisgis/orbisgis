package org.orbisgis.geoview.sqlConsole.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.sqlConsole.actions.ActionsListener;
import org.orbisgis.toolsMenuPanel.jaxb.Menu;
import org.orbisgis.toolsMenuPanel.jaxb.MenuItem;
import org.orbisgis.toolsMenuPanel.jaxb.SqlInstr;

public class SQLConsolePanel extends JPanel {
	private final String EOL = System.getProperty("line.separator");

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

	// private DefaultTreeModel treeModel;
	private GeoView2D geoview;
	private ScrollPaneWest scrollPanelWest;

	/**
	 * This is the default constructor
	 * 
	 * @param geoview
	 */
	public SQLConsolePanel(GeoView2D geoview) {
		this.geoview = geoview;

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
		this.add(getCenterPanel(), BorderLayout.CENTER);

	}

	private JPanel getNorthPanel() {
		final JPanel northPanel = new JPanel();
		final FlowLayout flowLayout = new FlowLayout();
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
			centerPanel.setLayout(new BorderLayout());
			centerPanel.add(getSplitPane(), BorderLayout.CENTER);
		}
		return centerPanel;
	}

	private Component getSplitPane() {
		if (splitPanel == null) {
			splitPanel = new JSplitPane();
			splitPanel.setLeftComponent(getScrollPanelWest());
			splitPanel.setRightComponent(getJScrollPaneEast());
			splitPanel.setOneTouchExpandable(true);
			splitPanel.setResizeWeight(1);
			splitPanel.setContinuousLayout(true);
			splitPanel.setPreferredSize(new Dimension(400, 140));
		}

		return splitPanel;
	}

	public ScrollPaneWest getScrollPanelWest() {
		if (scrollPanelWest == null) {
			scrollPanelWest = new ScrollPaneWest(geoview);
		}

		return scrollPanelWest;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getExecuteBT() {
		if (executeBT == null) {
			executeBT = new JButton();
			executeBT.setMargin(new Insets(0, 0, 0, 0));
			executeBT.setText("");
			executeBT.setToolTipText("Click to execute query");
			executeBT.setIcon(new ImageIcon(getClass().getResource(
					"Execute.png")));
			executeBT.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
					10));
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
			eraseBT.setMargin(new Insets(0, 0, 0, 0));
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
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSaveQuery() {
		if (saveQuery == null) {
			saveQuery = new JButton();
			saveQuery.setMargin(new Insets(0, 0, 0, 0));
			saveQuery
					.setIcon(new ImageIcon(getClass().getResource("Save.png")));
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
			openQuery.setMargin(new Insets(0, 0, 0, 0));

			openQuery
					.setIcon(new ImageIcon(getClass().getResource("Open.png")));
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
			stopQueryBt.setMargin(new Insets(0, 0, 0, 0));
			stopQueryBt.setFont(new Font("Dialog", Font.BOLD, 10));
			stopQueryBt.setToolTipText("Stop the query");
			stopQueryBt.setIcon(new ImageIcon(getClass()
					.getResource("Stop.png")));
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
			jButtonNext.setMargin(new Insets(0, 0, 0, 0));
			jButtonNext.setIcon(new ImageIcon(getClass().getResource(
					"go-next.png")));

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
			jButtonPrevious.setMargin(new Insets(0, 0, 0, 0));

			jButtonPrevious.setIcon(new ImageIcon(getClass().getResource(
					"go-previous.png")));

			jButtonPrevious.setFont(new Font("Dialog", Font.BOLD, 10));
			jButtonPrevious.setToolTipText("Previous query");

			jButtonPrevious
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent evt) {
						}
					});
		}
		return jButtonPrevious;
	}

	private JTree getTree() {
		rootNode = new DefaultMutableTreeNode();
		queries = new HashMap<String, String>();

		final JTree tree = new JTree(rootNode);
		// Customized JTree icons.
		final DefaultTreeCellRenderer myRenderer = new DefaultTreeCellRenderer();

		myRenderer.setLeafIcon(new ImageIcon(this.getClass().getResource(
				"help.png")));
		myRenderer.setClosedIcon(new ImageIcon(this.getClass().getResource(
				"folder.png")));
		myRenderer.setOpenIcon(new ImageIcon(this.getClass().getResource(
				"open_folder.png")));

		tree.setCellRenderer(myRenderer);

		addQueries();

		tree.expandPath(new TreePath(rootNode.getPath()));
		tree.setRootVisible(false);
		tree.setDragEnabled(true);
		return tree;
	}

	public static String getQuery(String name) {
		return queries.get(name);
	}

	private void addMenu(final Menu menu,
			final DefaultMutableTreeNode parentNode)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		final List<Object> subMenus = menu.getMenuOrMenuItem();
		for (Object subMenu : subMenus) {
			if (subMenu instanceof Menu) {
				final DefaultMutableTreeNode node = new DefaultMutableTreeNode(
						((Menu) subMenu).getLabel());
				parentNode.add(node);
				addMenu((Menu) subMenu, node);
			} else {
				addMenu((MenuItem) subMenu, parentNode);
			}
		}
	}

	private void addMenu(final MenuItem menuItem,
			final DefaultMutableTreeNode parentNode)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		final String label = menuItem.getLabel();
		final DefaultMutableTreeNode child = new DefaultMutableTreeNode(label);
		parentNode.add(child);

		if (null != menuItem.getClassName()) {
			final String className = menuItem.getClassName().getContent()
					.trim();
			final Object newInstance = Class.forName(className).newInstance();
			if (newInstance instanceof Function) {
				queries.put(label, ((Function) newInstance).getSqlOrder());
			} else if (newInstance instanceof CustomQuery) {
				queries.put(label, ((CustomQuery) newInstance).getSqlOrder());
			}
		} else {
			final StringBuilder sb = new StringBuilder();
			for (SqlInstr sqlInstr : menuItem.getSqlBlock().getSqlInstr()) {
				sb.append(sqlInstr.getContent()).append(EOL);
			}
			queries.put(label, sb.toString());
		}
	}

	public void addQueries() {
		final URL xmlFileUrl = SQLConsolePanel.class
				.getResource("OrbisGISToolsMenuPanel.xml");
		try {
			final Menu rootMenu = (Menu) JAXBContext.newInstance(
					"org.orbisgis.toolsMenuPanel.jaxb",
					this.getClass().getClassLoader()).createUnmarshaller()
					.unmarshal(xmlFileUrl);
			addMenu(rootMenu, rootNode);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setText(String text) {
		getScrollPanelWest().setText(text);
	}

	public void execute() {
		acl.execute();
	}
}