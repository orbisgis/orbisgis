package org.orbisgis.plugin.view.ui.workbench;

import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;



public class AssistantAddSource extends JDialog {

	/** This assistant will help the user to add a source
	 *  It allows him to choose between flat files or databases
	 *  It keeps all the parameters given by the user and provide methods for GeoCatalog3 to retrieve them
	 *  
	 *  @author Samuel Chemla
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sourceType = null;	//sourceType="flat" or "database"
	private File[] files = null;	//The files to load
	private String name = null; //Name to give to the datasource
	
	public AssistantAddSource(JFrame jFrame) {
		
		JOptionPane.showMessageDialog(jFrame, "Only shp files for the moment !", "Add a source",JOptionPane.INFORMATION_MESSAGE);
		FileChooser fc = new FileChooser(jFrame);
		this.files=fc.getFiles();
	}
	
	public File[] getFiles() {
		return files;
	}
	
	public String getName() {
		return name;
	}
	
	
	
}