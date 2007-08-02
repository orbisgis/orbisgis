package org.orbisgis.plugin.view3d;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.orbisgis.plugin.view.layerModel.LayerCollection;

/**
 * This class is responsible for creating and showing a 3D viewer. IMPORTANT :
 * keep in mind that the 3DCanvas of java monkey is an heavyweight component so
 * try to avoid conflicts with swing components...
 * 
 * @author Samuel CHEMLA
 * 
 */
public class GeoView3DFrame extends JFrame {

	// The Contents of the frame
	private GeoView3DPanel geoView3D = null;

	// See ActionsListener def
	private ActionsListener acl = null;

	/**
	 * Constructor : initializes the main frame, add a menu, add the GeoView3D
	 * panel
	 * 
	 */
	public GeoView3DFrame(LayerCollection root) {
		super("GeoView3D");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		acl = new ActionsListener();
		setJMenuBar(addMenuBar()); // Add the menu bar
		geoView3D = new GeoView3DPanel(root);
		setLayout(new BorderLayout());
		getContentPane().add(geoView3D, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	/**
	 * Initializes the Menu bar
	 * 
	 * @return JMenuBar
	 */
	private JMenuBar addMenuBar() {
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
		menu.setText("File");

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
		menu.add(menuItem);

		return menu;
	}

	/**
	 * Class responsible for handling actions in GeoView3DFrame
	 * 
	 * @author samuel
	 * 
	 */
	private class ActionsListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ("EXIT".equals(e.getActionCommand())) {
				// Exit the program
				System.exit(0);

			} else if ("ABOUT".equals(e.getActionCommand())) {
				// TODO Open an about window
			}
		}
	}
}
