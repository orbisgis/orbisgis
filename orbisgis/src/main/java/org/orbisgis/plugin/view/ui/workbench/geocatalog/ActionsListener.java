package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.ui.workbench.GeoView2DFrame;

public class ActionsListener implements ActionListener {
	private JFrame jFrame = null;
	private Catalog myCatalog = null;

	public void setParameters (JFrame jFrame, Catalog myCatalog) {
		this.jFrame=jFrame;
		this.myCatalog=myCatalog;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if ("NEWGV".equals(e.getActionCommand())) {
			//Open a new GeoView
			//This code may be a little bit dirty...
			//TODO : cleanup !!
			PropertyConfigurator.configure(GeoView2DFrame.class
					.getResource("log4j.properties"));
			PatternLayout l = new PatternLayout("%p %t %C - %m%n");
			RollingFileAppender fa = null;
			try {
				fa = new RollingFileAppender(l,
						System.getProperty("user.home") + File.separator + "orbisgis"
								+ File.separator + "orbisgis.log", false);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			fa.setMaxFileSize("512KB");
			fa.setMaxBackupIndex(3);
			Logger.getRootLogger().addAppender(fa);
			GeoView2DFrame vf = new GeoView2DFrame(TempPluginServices.lc);
			//myCatalog.addGeoView(vf); used for multiple catalogs
			vf.pack();
			vf.setVisible(true);
			
		} else if ("SAVESESSION".equals(e.getActionCommand())){
			//Save the session
			System.out.println("I will now save the Sesion");
		} else if ("NEWFOLDER".equals(e.getActionCommand())) {
			String name = JOptionPane.showInputDialog(jFrame,"Name");
			if (!name.isEmpty()) {
				MyNode newNode = new MyNode(name,MyNode.folder);
				myCatalog.addNode(newNode);
			}
		} else if ("ADDSOURCE".equals(e.getActionCommand())){
			//Add a source
			AssistantAddSource assistant=new AssistantAddSource(jFrame);
			for (File file : assistant.getFiles()) {
				String name = file.getName();
				try {
					myCatalog.addSource(file, name);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
			assistant=null;
		} else if ("DEL".equals(e.getActionCommand())) {
			//Removes the selected node
			myCatalog.removeNode();
		} /*else if ("CLRCATALOG".equals(e.getActionCommand())) {
			//Clears the catalog
			if (JOptionPane.showConfirmDialog(jFrame, "Are you sure you want to clear all sources ?", "Confirmation", JOptionPane.YES_NO_OPTION)==0){
				myCatalog.clear();
			}
		}*/ else if ("ADDSQL".equals(e.getActionCommand())) {
			//Add a SQL request
			String name = JOptionPane.showInputDialog(jFrame,"Enter your SQL request");
			if (!name.isEmpty()) {
				MyNode newNode = new MyNode(name,MyNode.sqlquery);
				myCatalog.addNode(newNode);
			}
		} else if ("EXIT".equals(e.getActionCommand())) {
			//Exit the program
			System.exit(0);
		} else if ("ABOUT".equals(e.getActionCommand())) {
			//Shows the about dialog
			JOptionPane.showMessageDialog(jFrame, "GeoCatalog\nVersion 0.0", "About GeoCatalog",JOptionPane.INFORMATION_MESSAGE);
		}
	}
}