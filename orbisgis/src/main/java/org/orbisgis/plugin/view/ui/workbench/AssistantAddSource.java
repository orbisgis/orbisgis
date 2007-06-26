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
	//private String sourceType = null;	//sourceType="flat" or "database"
	private File[] files = null;	//The files to load
	private String name = null; //Name to give to the datasource
	
	public AssistantAddSource(JFrame jFrame) {
		
		//csv and dbf are NOT supported for the moment
		//JOptionPane.showMessageDialog(jFrame, "Only shp, csv, dbf files for the moment !", "Add a source",JOptionPane.INFORMATION_MESSAGE);
		JOptionPane.showMessageDialog(jFrame, "Only shp files for the moment !", "Add a source",JOptionPane.INFORMATION_MESSAGE);
		OurFileChooser ofc = new OurFileChooser("shp", "SHP files (*.shp)", true);
		ofc.showOpenDialog(jFrame);
		this.files = ofc.getSelectedFiles();
	}
	
	public File[] getFiles() {
		return files;
	}
	
	public String getName() {
		return name;
	}
	
	
	
}