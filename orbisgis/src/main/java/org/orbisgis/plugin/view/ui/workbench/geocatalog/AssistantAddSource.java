package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AssistantAddSource extends JPanel{

	/** This assistant will help the user to add a source
	 *  It allows him to choose between flat files or databases
	 *  It keeps all the parameters given by the user and provide methods for GeoCatalog to retrieve them
	 *  
	 *  @author Samuel Chemla
	 */ 
	private static final long serialVersionUID = 1L;
	

	/** @param jFrame The mother JFrame */
	public AssistantAddSource() {
		setLayout(new CRFlowLayout());
		add(new JLabel("Connection name"));
		add(new JTextField());
		add(new CarriageReturn());
		add(new JLabel("Host"));
		add(new JTextField());
		add(new CarriageReturn());
	}
	
}