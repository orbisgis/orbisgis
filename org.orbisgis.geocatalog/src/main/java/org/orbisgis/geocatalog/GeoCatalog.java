//TODO : comment everything
package org.orbisgis.geocatalog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.orbisgis.geocatalog.resources.Folder;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;
import org.sif.UIFactory;
import org.sif.UIPanel;

/**
 * Graphical interface for the Geo Catalog This file mainly contains user
 * interface stuff It is the main application
 *
 * @author Samuel Chemla
 * @version beta1
 */

public class GeoCatalog {

	private static final String NEWRESOURCE = "NEWRESOURCE";

	private static final String EXIT = "EXIT";

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
	private ActionListener acl = new MenuActionListener();

	private final Icon helpIcon = new ImageIcon(getClass().getResource(
			"help.png"));

	private final Icon homeIcon = new ImageIcon(getClass().getResource(
			"home.png"));

	public GeoCatalog() {

		jFrame = new JFrame();

		// be instantied now or the listener won't work...
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setSize(FrameSize);

		java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
		jFrame.setIconImage(new ImageIcon(url).getImage());

		jFrame.setTitle("OrbisGIS : GeoCatalog");
		jFrame.setJMenuBar(getMenuBar()); // Add the menu bar

		// Creates a vertical box layout and add its elements
		Box verticalBox = Box.createVerticalBox();
		jFrame.add(verticalBox);

		myCatalog = new Catalog(jFrame);

		myCatalog.getCatalogModel().insertNode(new Folder("Add datas here"));
		myCatalog.getCatalogModel().insertNode(new Folder("Another folder"));

		/**
		 * Plugin section : load plugins
		 */
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] extensions;

		/**
		 * Loads plugins "Catalog Toolbars"
		 *
		 * TODO : test a sample toolbar
		 */
		extensions = reg.getExtensions("org.orbisgis.geocatalog.Action");
		for (int i = 0; i < extensions.length; i++) {
			Configuration element = extensions[i].getConfiguration();

			IToolbar extension;
			extension = (IToolbar) element
					.instantiateFromAttribute("", "class");
			verticalBox.add(extension.getToolBar());
		}

		// Add the catalog after the tooblars
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
		menuItem.setText("New Resource...");
		menuItem.setActionCommand(NEWRESOURCE);
		menuItem.addActionListener(acl);
		menu.add(menuItem);

		menuItem = new JMenuItem();
		menuItem.setText("Exit");
		menuItem.setActionCommand(EXIT);
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

	/** Restore and show the GeoCatalog */
	public void show() {
		jFrame.setExtendedState(JFrame.NORMAL);
		jFrame.toFront();
	}

	public static void main(String[] args) {

		// TODO : set splash time to 2000
		System.out.println("Splash time temporary reduced");
		Splash w = new Splash(2);

		w.setVisible(true);

		// Initializes TempPluginServices
		// TODO : do we keep TempPluginServices ???
		// TempPluginServices.lc = new LayerCollection("my root");
		// TempPluginServices.dsf = new DataSourceFactory();

		// Create one geoCatalog
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				@SuppressWarnings("unused")
				GeoCatalog geoCatalog = new GeoCatalog();

				// Register the Catalog in TempPluginService
				// TempPluginServices.geoCatalog = geoCatalog;
			}
		});

		w.setVisible(false);
		w.dispose();
	}

	private class MenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if ("NEWRESOURCE".equals(e.getActionCommand())) {
				IExtensionRegistry reg = RegistryFactory.getRegistry();
				Extension[] exts = reg
						.getExtensions("org.orbisgis.geocatalog.resourceWizard");
				ArrayList<INewResource> wizards = new ArrayList<INewResource>();
				for (int i = 0; i < exts.length; i++) {
					Configuration c = exts[i].getConfiguration();

					INewResource nr = (INewResource) c
							.instantiateFromAttribute("/extension/wizard",
									"class");
					wizards.add(nr);
				}
				String[] names = new String[wizards.size()];
				for (int i = 0; i < names.length; i++) {
					names[i] = wizards.get(i).getName();
				}
				ChoosePanel cp = new ChoosePanel("Select the resource type", names);
				boolean accepted = UIFactory.showDialog(cp);
				if (accepted) {
					int index = cp.getSelectedIndex();
					INewResource wizard = wizards.get(index);
					UIPanel[] panels = wizard.getWizardPanels();
					boolean ok = UIFactory.showDialog(panels);
					if (ok) {
						myCatalog.setIgnoreSourceOperations(true);
						IResource[] resources = wizard.getResources();
						myCatalog.setIgnoreSourceOperations(false);
						for (IResource resource : resources) {
							myCatalog.getCatalogModel().insertNode(resource);
						}
					}
				}

			} else if ("EXIT".equals(e.getActionCommand())) {
				// Exit the program
				RegistryFactory.shutdown();

			} else if ("ABOUT".equals(e.getActionCommand())) {
				// Shows the about dialog
				JOptionPane.showMessageDialog(jFrame,
						"GeoCatalog\nVersion 0.0", "About GeoCatalog",
						JOptionPane.INFORMATION_MESSAGE);

			}

		}

	}

}