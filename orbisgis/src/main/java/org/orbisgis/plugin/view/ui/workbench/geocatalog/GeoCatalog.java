package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import javax.swing.*;
import org.gdms.data.DataSourceFactory;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import java.awt.*;


/** Graphical interface for GeoCatalog
 *  This file mainly contains user interface stuff
 * 
 * @author Samuel Chemla
 */

public class GeoCatalog {
	
   /** The frame is made of a vertical BoxLayout, which contains :
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
    private static Catalog myCatalog = null;	//See Catalog.java
    ActionsListener acl = null;	//Handles all the actions performed in GeoCatalog (including Catalog)
	
	public GeoCatalog(){
		
		//Initializes the frame
		jFrame = new JFrame();
		acl=new ActionsListener();			//Enables the action listener. It must be instantied now or the listener won't work...
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setSize(FrameSize);
		jFrame.setTitle("OrbisGIS : GeoCatalog");
		jFrame.setJMenuBar(getMenuBar());	//Add the menu bar
		
		//Creates the vertical box layout and add its elements
		Container contenu=jFrame.getContentPane();
		verticalBox = Box.createVerticalBox();
		contenu.add(verticalBox);	
		verticalBox.add(getToolBar());		//Add the tool bar
		verticalBox.add(getCatalog());
		
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
	private Catalog getCatalog() { 
			myCatalog = new Catalog(acl);
	        return myCatalog;
	 }
	
	public static Catalog getMyCatalog() {
		return myCatalog;
	}
	
	public static void main(String[] args) {
		//Initializes TempPluginServices
		//TODO : this shouldn't be there...
		TempPluginServices.lc = new LayerCollection("my root");
		TempPluginServices.dsf = new DataSourceFactory();
		
		//Create one geoCatalog
		GeoCatalog geoCatalog = new GeoCatalog();
	}

}