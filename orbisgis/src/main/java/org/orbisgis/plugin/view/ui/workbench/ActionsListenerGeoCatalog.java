package org.orbisgis.plugin.view.ui.workbench;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.orbisgis.plugin.view.utilities.file.FileUtility;

public class ActionsListenerGeoCatalog implements ActionListener {
	private JFrame jFrame = null;
	private Catalog myCatalog = null;

	public void setParameters (JFrame jFrame, Catalog myCatalog) {
		this.jFrame=jFrame;
		this.myCatalog=myCatalog;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if ("NEWGV".equals(e.getActionCommand())) {
			//Open a new GeoView
			System.out.println("I will now open a new GeoView");
		} else if ("SAVESESSION".equals(e.getActionCommand())){
			//Save the session
			System.out.println("I will now save the Sesion");
		} else if ("ADDSOURCE".equals(e.getActionCommand())){
			//Add a source
			AssistantAddSource assistant=new AssistantAddSource(jFrame);
			for (File file : assistant.getFiles()) {
				String name = file.getName();
				//Removes the extension : a bad idea : it could creates some bugs if two files share the same name but have different extensions...
				//name = name.substring(0, name.indexOf("."+FileUtility.getFileExtension(file)));
				try {
					myCatalog.addSource(file, name);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			assistant=null;
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
}