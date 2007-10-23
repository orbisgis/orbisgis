package org.orbisgis.geocatalog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.orbisgis.geocatalog.resources.Folder;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;

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

	// Action Listener for GeoCatalog and Catalog
	private ActionListener al = new MenuActionListener();

	public GeoCatalog() {

		jFrame = new JFrame();

		// be instantied now or the listener won't work...
		jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jFrame.setSize(FrameSize);
		jFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				int answer = JOptionPane.showConfirmDialog(jFrame,
						"Really quit?", "OrbisGIS", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					RegistryFactory.shutdown();
				}
			}

		});

		java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
		jFrame.setIconImage(new ImageIcon(url).getImage());

		jFrame.setTitle("OrbisGIS : GeoCatalog");
		jFrame.setJMenuBar(getMenuBar()); // Add the menu bar

		// Creates a vertical box layout and add its elements
		Box verticalBox = Box.createVerticalBox();
		jFrame.add(verticalBox);

		myCatalog = new Catalog();

		myCatalog.getCatalogModel().insertNode(new Folder("Add datas here"));
		myCatalog.getCatalogModel().insertNode(new Folder("Another folder"));
		myCatalog.getCatalogModel().insertNode(new Folder("third folder"));

		// Add the catalog
		verticalBox.add(myCatalog);

		jFrame.setVisible(true);

	}

	/**
	 * Initializes the Menu bar
	 *
	 * @return JMenuBar
	 */
	private JMenuBar getMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg.getExtensions("org.orbisgis.geocatalog.Action");
		MenuTree menuTree = new MenuTree(al);
		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/menu)");
			for (int i = 0; i < n; i++) {
				String base = "/extension/menu[" + (i + 1) + "]";
				String parent = c.getAttribute(base, "parent");
				String id = c.getAttribute(base, "id");
				String text = c.getAttribute(base, "text");
				String icon = c.getAttribute(base, "icon");
				Menu m = new Menu(parent, id, text, icon);
				menuTree.addMenu(m);
			}
		}
		for (int j = 0; j < exts.length; j++) {
			Configuration c = exts[j].getConfiguration();
			int n = c.evalInt("count(/extension/action)");
			for (int i = 0; i < n; i++) {
				String base = "/extension/action[" + (i + 1) + "]";
				String parent = c.getAttribute(base, "parent");
				String id = c.getAttribute(base, "id");
				String text = c.getAttribute(base, "text");
				String icon = c.getAttribute(base, "icon");
				Menu m = new Menu(parent, id, text, icon);
				menuTree.addMenu(m);
			}
		}
		JMenuItem[] menus = menuTree.getJMenus();
		for (int i = 0; i < menus.length; i++) {
			menuBar.add(menus[i]);
		}

		return menuBar;
	}

	/** Restore and show the GeoCatalog */
	public void show() {
		jFrame.setExtendedState(JFrame.NORMAL);
		jFrame.toFront();
	}

	private class MenuActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			ExtensionPointManager<IGeocatalogAction> epm = new ExtensionPointManager<IGeocatalogAction>(
					"org.orbisgis.geocatalog.Action");
			IGeocatalogAction action = epm.instantiateFrom(
					"/extension/action[@id='" + e.getActionCommand() + "']",
					"class");
			action.actionPerformed(myCatalog);
		}

	}

}