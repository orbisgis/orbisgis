package org.orbisgis.geocatalog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.orbisgis.core.ActionExtensionPointHelper;
import org.orbisgis.core.MenuTree;
import org.orbisgis.core.resourceTree.Folder;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;

/**
 * Graphical interface for the Geo Catalog This file mainly contains user
 * interface stuff
 *
 * @author Samuel Chemla
 * @version beta1
 */

public class GeoCatalog {

	/**
	 * The frame is made of a vertical BoxLayout, which contains : 1-a menu bar
	 * 2-a tool bar 3-a scroll pane with a grid layout inside with a tree inside
	 */

	// Let you set the size of the frame
	private final Dimension FrameSize = new Dimension(250, 640);

	// The frame containing everything.
	private JFrame jFrame = null;

	private static Catalog myCatalog = null; // See Catalog.java

	public GeoCatalog() {

		jFrame = new JFrame();

		// be instantied now or the listener won't work...
		jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jFrame.setSize(FrameSize);
		jFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
			}

		});

		java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
		jFrame.setIconImage(new ImageIcon(url).getImage());

		jFrame.setTitle("OrbisGIS : GeoCatalog");
		JMenuBar menuBar = new JMenuBar();
		JToolBar toolBar = new JToolBar();
		myCatalog = new Catalog();
		MenuTree menuTree = new MenuTree();
		ActionExtensionPointHelper.configureMenuAndToolBar(
				"org.orbisgis.geocatalog.Action", new MyActionListener(),
				menuTree, toolBar);
		EPResourceWizardHelper.addWizardMenus(menuTree, new MyWizardListener());
		JComponent[] menus = menuTree.getJMenus();
		for (int i = 0; i < menus.length; i++) {
			menuBar.add(menus[i]);
		}
		jFrame.setJMenuBar(menuBar); // Add the menu bar
		jFrame.getContentPane().setLayout(new BorderLayout());
		jFrame.getContentPane().add(toolBar, BorderLayout.PAGE_START);

		jFrame.getContentPane().add(myCatalog, BorderLayout.CENTER);

		myCatalog.getTreeModel().insertNode(new Folder("Add datas here"));
		myCatalog.getTreeModel().insertNode(new Folder("Another folder"));
		myCatalog.getTreeModel().insertNode(new Folder("third folder"));

		jFrame.setVisible(true);
	}

	/** Restore and show the GeoCatalog */
	public void show() {
		jFrame.setExtendedState(JFrame.NORMAL);
		jFrame.toFront();
	}

	private class MyActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			EPGeocatalogActionHelper.executeAction(myCatalog, e
					.getActionCommand());
		}

	}

	private class MyWizardListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			EPResourceWizardHelper.runWizard(myCatalog, e.getActionCommand(),
					null);
		}

	}
}