package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import javax.swing.JDialog;
import javax.swing.JFrame;


public class AssistantAddSource extends JDialog{

	/** This assistant will help the user to add a source
	 *  It allows him to choose between flat files or databases
	 *  It keeps all the parameters given by the user and provide methods for GeoCatalog to retrieve them
	 *  
	 *  @author Samuel Chemla
	 */ 
	private static final long serialVersionUID = 1L;
	

	public AssistantAddSource(JFrame frame) {
		super(frame,"Assistant add source");
		add(new AddDataBasePanel());
		pack();
		setVisible(true);
	}
	
}