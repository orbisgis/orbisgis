package org.orbisgis.geoview.views.sqlConsole.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.views.sqlConsole.actions.ActionsListener;

public class SQLConsolePanel extends JPanel {
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

	static HashMap<String, String> queries;
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
			centerPanel.add(getScrollPanelWest(), BorderLayout.CENTER);
		}
		return centerPanel;
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
			executeBT = new SQLConsoleButton(getClass().getResource(
					"Execute.png"), "Click to execute query", "EXECUTE", acl);
			// executeBT = new JButton();
			// executeBT.setMargin(new Insets(0, 0, 0, 0));
			// executeBT.setText("");
			// executeBT.setToolTipText("Click to execute query");
			// executeBT.setIcon(new ImageIcon(getClass().getResource(
			// "Execute.png")));
			// executeBT.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
			// 10));
			// executeBT.setActionCommand("EXECUTE");
			// executeBT.addActionListener(acl);
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
			eraseBT = new SQLConsoleButton(getClass().getResource("Erase.png"),
					"Clear console", "ERASE", acl);
			// eraseBT = new JButton();
			// eraseBT.setMargin(new Insets(0, 0, 0, 0));
			// eraseBT.setText("");
			// eraseBT.setIcon(new
			// ImageIcon(getClass().getResource("Erase.png")));
			// eraseBT.setFont(new Font("Dialog", Font.BOLD, 10));
			// eraseBT.setToolTipText("Clear the query");
			// eraseBT.setActionCommand("ERASE");
			// eraseBT.addActionListener(acl);
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
			saveQuery = new SQLConsoleButton(
					getClass().getResource("Save.png"), "Save current console",
					"SAVEQUERY", acl);
			// saveQuery = new JButton();
			// saveQuery.setMargin(new Insets(0, 0, 0, 0));
			// saveQuery
			// .setIcon(new ImageIcon(getClass().getResource("Save.png")));
			// saveQuery.setActionCommand("SAVEQUERY");
			// saveQuery.addActionListener(acl);
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
			openQuery = new SQLConsoleButton(
					getClass().getResource("Open.png"),
					"Open an already saved SQL script", "OPENSQLFILE", acl);
			// openQuery = new JButton();
			// openQuery.setMargin(new Insets(0, 0, 0, 0));
			//
			// openQuery
			// .setIcon(new ImageIcon(getClass().getResource("Open.png")));
			// openQuery.setActionCommand("OPENSQLFILE");
			// openQuery.addActionListener(acl);
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
			stopQueryBt = new SQLConsoleButton(getClass().getResource(
					"Stop.png"), "Stop the query", "STOPQUERY", acl);
			// stopQueryBt = new JButton();
			// stopQueryBt.setMargin(new Insets(0, 0, 0, 0));
			// stopQueryBt.setFont(new Font("Dialog", Font.BOLD, 10));
			// stopQueryBt.setToolTipText("Stop the query");
			// stopQueryBt.setIcon(new ImageIcon(getClass()
			// .getResource("Stop.png")));
			// stopQueryBt.setText("");
			// stopQueryBt.setMnemonic(KeyEvent.VK_UNDEFINED);
			// stopQueryBt.setEnabled(true);
		}
		return stopQueryBt;
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

	public void setText(String text) {
		getScrollPanelWest().setText(text);
	}

	public void execute() {
		acl.execute();
	}
}