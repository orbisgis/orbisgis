package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AddSourceChoosePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private SourceChooseActionListener acl = null;
	private JComboBox typeDS = null;
	private String[] type = {"Database","Flat File"};
	private AddDataBasePanel databaseParameters = null;

	public AddSourceChoosePanel() {
		setLayout(new CRFlowLayout());
		acl = new SourceChooseActionListener();
		
		add(new JLabel("Type of Datasource : "));
		typeDS = new JComboBox(type);
		typeDS.setToolTipText("Chose here between a flat file or a Database");
		typeDS.setActionCommand("REFRESH");
		typeDS.addActionListener(acl);
		add(typeDS);
		
		add(new CarriageReturn());
		
		if (typeDS.getSelectedItem().equals("Database")) {
			databaseParameters = new AddDataBasePanel();
			add(databaseParameters);
		}

		add(new CarriageReturn());
	}

	private class SourceChooseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ("REFRESH".equals(e.getActionCommand())) {
				System.err.println("should refresh now");
			}
		}
	}
}
