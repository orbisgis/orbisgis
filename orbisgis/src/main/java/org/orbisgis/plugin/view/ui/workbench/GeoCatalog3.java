package org.orbisgis.plugin.view.ui.workbench;

import javax.swing.*;

import org.gdms.data.DataSourceFactory;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.LayerCollection;

import java.awt.*;
import java.awt.event.MouseListener;


/** Graphical interface for GeoCatalog
 *  This file mainly contains user interface stuff
 * 
 * @author Samuel Chemla
 * TODO lots...
 */

public class GeoCatalog3 implements Runnable {
	
   /** The frame is made of a vertical BoxLayout, which contains�:
	*	1-a menu bar
	*	2-a tool bar
	*	3-a scroll pane with a tree inside
	*/
	private Dimension FrameSize = new Dimension(300,500);	//Let you set the size of the frame
	private JFrame jFrame = null;
	private Box verticalBox = null;
	private JMenuBar MenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenu helpMenu = null;
	private JToolBar ToolBar = null;
	private JButton newGeoViewButton = null;
	private JButton newSaveSessionButton = null;
    private Catalog myCatalog = null;	//See Catalog.java
    private JPopupMenu treePopup = null;
    ActionsListenerGeoCatalog acl = null;	//Handles all the actions performed
	
	public GeoCatalog3(){
		
		//Initializes the frame
		jFrame = new JFrame();
		acl=new ActionsListenerGeoCatalog ();			//Enables the action listener. It must be instantied now or the listener won't work...
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setSize(FrameSize);
		jFrame.setTitle("OrbisGIS : GeoCatalog");
		jFrame.setJMenuBar(getMenuBar());	//Add the menu bar
		
		//Creates the vertical box layout and add its elements
		Container contenu=jFrame.getContentPane();
		verticalBox = Box.createVerticalBox();
		contenu.add(verticalBox);	
		verticalBox.add(getToolBar());		//Add the tool bar
		verticalBox.add(getCatalog());		//Add the tree containing the catalogs
		this.getPopupMenu();				//Enables the tree popup menu
		
		acl.setParameters(jFrame, myCatalog);	//Force the listener to update its refernces to jFrame and myCatalog
		
		//Finally displays the frame...
		jFrame.setVisible(true);
	}

	/** Initializes the Menu bar
	 * 
	 * @return JMenuBar
	 */
	private JMenuBar getMenuBar() {
		MenuBar = new JMenuBar();
		MenuBar.add(getFileMenu());
		MenuBar.add(getHelpMenu());
		return MenuBar;
	}
	
	/** Initializes the File Menu
	 * 
	 * @return JMenu
	 */
	private JMenu getFileMenu() {
		exitMenuItem = new JMenuItem();
		exitMenuItem.setText("Exit");
		exitMenuItem.setActionCommand("EXIT");
		exitMenuItem.addActionListener(acl);
		
		fileMenu = new JMenu();
		fileMenu.setText("File");
		fileMenu.add(exitMenuItem);
		return fileMenu;
	}
	
	/** Initializes the Help Menu
	 * 
	 * @return JMenu
	 */
	private JMenu getHelpMenu() {
		aboutMenuItem = new JMenuItem();
		aboutMenuItem.setText("About");
		aboutMenuItem.setActionCommand("ABOUT");
		aboutMenuItem.addActionListener(acl);
		
		helpMenu = new JMenu();
		helpMenu.setText("Help");
		helpMenu.add(aboutMenuItem);
		
		//helpMenu.add(getAboutMenuItem());
		return helpMenu;
	}
	
	/** Initializes the Tool Bar
	 * 
	 * @return JToolBar
	 */
	private JToolBar getToolBar() {
		ToolBar = new JToolBar();
		ToolBar.setMaximumSize(new Dimension(2048, 24));	//Set the max Heigh of the bar
		ToolBar.setFloatable(false);	//non floatable toolbar
		
		newGeoViewButton = new JButton("New GeoView");
		newGeoViewButton.setActionCommand("NEWGV");
		newGeoViewButton.addActionListener(acl);
		
		newSaveSessionButton = new JButton("Save session");
		newSaveSessionButton.setActionCommand("SAVESESSION");
		newSaveSessionButton.addActionListener(acl);
		
		ToolBar.add(newGeoViewButton);
		ToolBar.add(newSaveSessionButton);
		return ToolBar;
	}
	
	/** Initializes the Catalog
	 * 
	 * @return Catalog
	 */
	public Catalog getCatalog() { 
			myCatalog = new Catalog();
	        return myCatalog;
	 }
	
	/** Initialize the Popup Menu on the catalog
	 * 
	 */ 
	public void getPopupMenu() {
        JMenuItem menuItem;
        treePopup = new JPopupMenu();
        //Edit the popup menu.
        menuItem = new JMenuItem("Add a source");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDSOURCE");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a SQL Query");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("ADDSQL");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("DEL");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Clear all sources");
        menuItem.addActionListener(acl);
        menuItem.setActionCommand("CLRSOURCES");
        treePopup.add(menuItem);
        
        //Add listener to the Catalog area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(treePopup);
        myCatalog.tree.addMouseListener(popupListener);
	}

	public void run() {
	}

	public static void main(String[] args) throws Exception {
		
		//Initializes TempPluginServices
		TempPluginServices.lc = new LayerCollection("my root");
		TempPluginServices.dsf = new DataSourceFactory();
		
		//Create one geoCatalog and launch it as a thread
		GeoCatalog3 geoCatalog = new GeoCatalog3();
		new Thread(geoCatalog).start();
		
		//Examples just for demonstrations
		geoCatalog.myCatalog.addQuery("SELECT * FROM myTable");
	}
	
}