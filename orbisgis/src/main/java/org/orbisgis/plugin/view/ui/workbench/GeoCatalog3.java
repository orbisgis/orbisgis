package org.orbisgis.plugin.view.ui.workbench;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;


/** Graphical interface for GeoCatalog
 *  This file mainly contains user interface stuff
 * 
 * @author Samuel Chemla
 * TODO lots...
 */

public class GeoCatalog3 implements Runnable, ActionListener{
	
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
    private Catalog myCatalog = null;	//See Catalog.java
    private JPopupMenu treePopup = null;
	
	public GeoCatalog3(){
		
		//Initializes the frame
		jFrame = new JFrame();
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
		exitMenuItem.addActionListener(this);
		
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
		aboutMenuItem.addActionListener(this);
		
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
		newGeoViewButton.addActionListener(this);
		newGeoViewButton.setActionCommand("NEWGV");
		
		newSaveSessionButton = new JButton("Save session");
		newSaveSessionButton.addActionListener(this);
		newSaveSessionButton.setActionCommand("SAVESESSION");
		
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
        menuItem.addActionListener(this);
        menuItem.setActionCommand("ADDSOURCE");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Add a SQL Query");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("ADDSQL");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("DEL");
        treePopup.add(menuItem);
        menuItem = new JMenuItem("Clear all sources");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("CLRSOURCES");
        treePopup.add(menuItem);
        
        //Add listener to the Catalog area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(treePopup);
        myCatalog.tree.addMouseListener(popupListener);
	}

	/** Manages all the actions performed in the window
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if ("NEWGV".equals(e.getActionCommand())) {
			//Open a new GeoView
			System.out.println("I will now open a new GeoView");
		} else if ("SAVESESSION".equals(e.getActionCommand())){
			//Save the session
			System.out.println("I will now save the Sesion");
		} else if ("ADDSOURCE".equals(e.getActionCommand())){
			//Add a source
			AssistantAddSource tmp=new AssistantAddSource(jFrame);
			for (File file : tmp.getFiles()) {
				try {
					myCatalog.addSource(file);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			tmp=null;
		} else if ("DEL".equals(e.getActionCommand())) {
			//Removes the selected source or query
			myCatalog.removeCurrentNode();
		} else if ("CLRSOURCES".equals(e.getActionCommand())) {
			//Removes all Datasources
			if (JOptionPane.showConfirmDialog(jFrame, "Are you sure you want to clear all sources ?", "Confirmation", JOptionPane.YES_NO_OPTION)==0){
				myCatalog.clearsources();
			}
		} else if ("ADDSQL".equals(e.getActionCommand())) {
			//Add a SQL request
			String txt = JOptionPane.showInputDialog(jFrame, "Tapez votre requête SQL");
			if (!txt.isEmpty()) {
				myCatalog.addQuery(txt);
			}
		} else if ("EXIT".equals(e.getActionCommand())) {
			//Exit the program
			System.exit(0);
		} else if ("ABOUT".equals(e.getActionCommand())) {
			//Shows the about dialog
			JOptionPane.showMessageDialog(jFrame, "GeoCatalog\nVersion 0.0", "About GeoCatalog",JOptionPane.INFORMATION_MESSAGE);
		}
    }
	

	public void run() {
	}

	public static void main(String[] args) throws Exception {
		//Create one geoCatalog and launch it as a thread
		GeoCatalog3 geoCatalog = new GeoCatalog3();
		new Thread(geoCatalog).start();
		
		//Essais
		//DataSource DS1 = geoCatalog.myCatalog.addSource("../../datas2tests/shp/mediumshape2D/bzh5_communes.shp");
		//DataSource DS2 = geoCatalog.myCatalog.addSource("../../datas2tests/shp/mediumshape2D/hedgerow.shp");
		
		geoCatalog.myCatalog.addQuery("SELECT * FROM myTable");
	}
	
	
	class PopupListener extends MouseAdapter {
        private JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            ShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            ShowPopup(e);
        }

        private void ShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }
    }
	
}