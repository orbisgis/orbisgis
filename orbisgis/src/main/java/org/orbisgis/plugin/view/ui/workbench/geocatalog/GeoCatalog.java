package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.*;
import java.io.File;
import java.io.FileFilter;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import org.gdms.data.DataSourceFactory;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.LayerCollection;

/**
 * Graphical interface for the Geo Catalog This file mainly contains user
 * interface stuff It is the main application
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
	private ActionsListener acl = null;

	private Icon helpIcon = new ImageIcon(this.getClass().getResource(
			"help.png"));

	private Icon homeIcon = new ImageIcon(this.getClass().getResource(
			"home.png"));;

	public GeoCatalog() {

		jFrame = new JFrame();

		acl = new ActionsListener(); // Enables the action listener. It must
		// be instantied now or the listener won't work...
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setSize(FrameSize);

		java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
		jFrame.setIconImage(new ImageIcon(url).getImage());

		jFrame.setTitle("OrbisGIS : GeoCatalog");
		jFrame.setJMenuBar(getMenuBar()); // Add the menu bar

		// Creates a vertical box layout and add its elements
		Container contenu = jFrame.getContentPane();
		Box verticalBox = Box.createVerticalBox();
		contenu.add(verticalBox);
		verticalBox.add(getToolBar()); // Add the tool bar

		myCatalog = new Catalog(acl);
		verticalBox.add(myCatalog);

		// Force the listener to update its refernces to jFrame and myCatalog
		acl.setParameters(jFrame, myCatalog);

		myCatalog.addNode(new MyNode("Add datas here", MyNode.folder));
		MyNode sld = new MyNode("SLD", MyNode.folder);
		myCatalog.addNode(sld);

		File[] slds = new File("sld").listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().toLowerCase()
						.endsWith(".sld");
			}

		});
		for (File sldFile : slds) {
			MyNode fichier = new MyNode(sldFile.getName().substring(0,
					sldFile.getName().length() - 4), MyNode.sldfile, null,
					sldFile.getPath());
			myCatalog.addNode(fichier, sld);
		}

		jFrame.setVisible(true);

	}

	/**
	 * Initializes the Menu bar
	 * 
	 * @return JMenuBar
	 */
	private JMenuBar getMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(getFileMenu());
		menuBar.add(getHelpMenu());
		return menuBar;
	}

	/**
	 * Initializes the File Menu
	 * 
	 * @return JMenu
	 */
	private JMenu getFileMenu() {
		JMenuItem menuItem;
		JMenu menu = new JMenu();
		menu.setIcon(homeIcon);
		menu.setText("File");

		menuItem = new JMenuItem();
		menuItem.setText("Save session");
		menuItem.setActionCommand("SAVESESSION");
		menuItem.addActionListener(acl);
		menu.add(menuItem);
		
		menuItem = new JMenuItem();
		menuItem.setText("Load session");
		menuItem.setActionCommand("LOADSESSION");
		menuItem.addActionListener(acl);
		menu.add(menuItem);
		
		menuItem = new JMenuItem();
		menuItem.setText("Exit");
		menuItem.setActionCommand("EXIT");
		menuItem.addActionListener(acl);
		menu.add(menuItem);
		
		return menu;
	}

	/**
	 * Initializes the Help Menu
	 * 
	 * @return JMenu
	 */
	private JMenu getHelpMenu() {
		JMenuItem menuItem = new JMenuItem();
		JMenu menu = new JMenu();

		menuItem.setText("About");
		menuItem.setActionCommand("ABOUT");
		menuItem.addActionListener(acl);

		menu.setText("Help");
		menu.setIcon(helpIcon);
		menu.add(menuItem);

		return menu;
	}

	/**
	 * Initializes the Tool Bar
	 * 
	 * @return JToolBar
	 */
	private JToolBar getToolBar() {
		JToolBar toolBar = new JToolBar();
		JButton button = new JButton();

		toolBar.setMaximumSize(new Dimension(2048, 24)); // Set the max Heigh
		// of the bar
		toolBar.setFloatable(false); // non floatable toolbar

		button = new JButton("Show GeoView");
		button.setActionCommand("NEWGV");
		button.addActionListener(acl);
		toolBar.add(button);
		
		return toolBar;
	}

	/**
	 * Retrieves myCatalog Static method
	 * 
	 * @return myCatalog TODO : do sth better than a static myCatalog...
	 */
	public static Catalog getMyCatalog() {
		return myCatalog;
	}

	/** Restore and show the GeoCatalog */
	public void show() {
		jFrame.setExtendedState(Frame.NORMAL);
		jFrame.toFront();
	}

	public static void main(String[] args) {

		Splash w = new Splash(2000);

		w.setVisible(true);

		// Initializes TempPluginServices
		// TODO : do we keep TempPluginServices ???
		TempPluginServices.lc = new LayerCollection("my root");
		TempPluginServices.dsf = new DataSourceFactory();

		// Create one geoCatalog
		GeoCatalog geoCatalog = new GeoCatalog();

		// Register the Catalog in TempPluginService
		TempPluginServices.geoCatalog = geoCatalog;

		w.setVisible(false);
		w.dispose();
	}

}