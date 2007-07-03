package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class AssistantAddSource extends JDialog{

	/** This assistant will help the user to add a source
	 *  It allows him to choose between flat files or databases
	 *  It keeps all the parameters given by the user and provide methods for GeoCatalog to retrieve them
	 *  
	 *  @author Samuel Chemla
	 */ 
	private static final long serialVersionUID = 1L;
	private Actions actions = null;
	private JButton ok = null;
	private JButton cancel = null;
	private JComboBox typeDS = null;
	private String[] type = {"Flat File","Database"};

	public AssistantAddSource(JFrame frame) {
		super(frame,"Assistant add source");
		setLayout(new CRFlowLayout());
		actions = new Actions();
		
		add(new JLabel("Type of Datasource : "));
		typeDS = new JComboBox(type);
		typeDS.setToolTipText("Chose here between a flat file or a Database");
		
		add(typeDS);
		
		add(new CarriageReturn());
		
		add(new AddDataBasePanel());
		
		add(new CarriageReturn());
		
		ok = new JButton("OK");
		ok.setActionCommand("OK");
		ok.addActionListener(actions);
		add(ok);
		
		cancel = new JButton("Cancel");
		cancel.setActionCommand("CANCEL");
		cancel.addActionListener(actions);
		add(cancel);
		
		pack();
		setVisible(true);
	}
	
	private class Actions implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if ("OK".equals(e.getActionCommand())) {
				
			} else if ("CANCEL".equals(e.getActionCommand())) {
				
			}
			
		}
		
	}
	
}