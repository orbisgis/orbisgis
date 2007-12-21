package org.orbisgis.geoview.views.sqlConsole.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.views.sqlConsole.actions.ActionsListener;

public class SQLConsolePanel extends JPanel {
	private JButton btExecute = null;
	private JButton btClear = null;
	private JButton btStop = null;
	private JButton btPrevious = null;
	private JButton btNext = null;
	private JButton btOpen = null;
	private JButton btSave = null;

	private ActionsListener actionsListener;
	private GeoView2D geoview;
	private JPanel centerPanel;
	private ScrollPaneWest scrollPanelWest;

	// public static DefaultMutableTreeNode racine;
	// static DefaultTreeModel m_model;
	// static HashMap<String, String> queries;
	// private DefaultTreeModel treeModel;
	// static JButton tableViewBt = null;

	/**
	 * This is the default constructor
	 * 
	 * @param geoview
	 */
	public SQLConsolePanel(GeoView2D geoview) {
		this.geoview = geoview;
		actionsListener = new ActionsListener(this);

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

	// getters
	private JPanel getNorthPanel() {
		final JPanel northPanel = new JPanel();
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		northPanel.setLayout(flowLayout);

		northPanel.add(getBtExecute());
		northPanel.add(getBtClear());
		// northPanel.add(getBtStop());

		northPanel.add(getBtPrevious());
		northPanel.add(getBtNext());

		northPanel.add(getBtOpen());
		northPanel.add(getBtSave());

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

	private ScrollPaneWest getScrollPanelWest() {
		if (scrollPanelWest == null) {
			scrollPanelWest = new ScrollPaneWest();
		}
		return scrollPanelWest;
	}

	public JButton getBtExecute() {
		if (null == btExecute) {
			btExecute = new SQLConsoleButton(ConsoleAction.EXECUTE,
					actionsListener);
		}
		return btExecute;
	}

	public JButton getBtClear() {
		if (null == btClear) {
			btClear = new SQLConsoleButton(ConsoleAction.CLEAR, actionsListener);
		}
		return btClear;
	}

	public JButton getBtStop() {
		if (null == btStop) {
			btStop = new SQLConsoleButton(ConsoleAction.STOP, actionsListener);
		}
		return btStop;
	}

	public JButton getBtPrevious() {
		if (null == btPrevious) {
			btPrevious = new SQLConsoleButton(ConsoleAction.PREVIOUS,
					actionsListener);
		}
		return btPrevious;
	}

	public JButton getBtNext() {
		if (null == btNext) {
			btNext = new SQLConsoleButton(ConsoleAction.NEXT, actionsListener);
		}
		return btNext;
	}

	public JButton getBtOpen() {
		if (null == btOpen) {
			btOpen = new SQLConsoleButton(ConsoleAction.OPEN, actionsListener);
		}
		return btOpen;
	}

	public JButton getBtSave() {
		if (null == btSave) {
			btSave = new SQLConsoleButton(ConsoleAction.SAVE, actionsListener);
		}
		return btSave;
	}

	public JTextArea getJTextArea() {
		return getScrollPanelWest().getJTextArea();
	}

	public GeoView2D getGeoview() {
		return geoview;
	}

	public void setText(String text) {
		getScrollPanelWest().setText(text);
	}

	public void execute() {
		actionsListener.execute();
	}
}