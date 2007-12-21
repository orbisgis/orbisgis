package org.orbisgis.geoview.views.sqlConsole.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;

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

	private ActionsListener actionsListener = new ActionsListener();

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
					"Execute.png"), "Click to execute query",
					SQLConsoleAction.EXECUTE, actionsListener);
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
					"Clear console", SQLConsoleAction.CLEAR, actionsListener);
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
					SQLConsoleAction.SAVE, actionsListener);
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
					"Open an already saved SQL script", SQLConsoleAction.OPEN,
					actionsListener);
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
					"Stop.png"), "Stop the query", SQLConsoleAction.STOP, actionsListener);
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
			jButtonNext = new SQLConsoleButton(getClass().getResource(
					"go-next.png"), "Next query", SQLConsoleAction.NEXT, actionsListener);
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
			jButtonPrevious = new SQLConsoleButton(getClass().getResource(
					"go-previous.png"), "Previous query",
					SQLConsoleAction.PREVIOUS, actionsListener);
		}
		return jButtonPrevious;
	}

	public void setText(String text) {
		getScrollPanelWest().setText(text);
	}

	public void execute() {
		actionsListener.execute();
	}
}