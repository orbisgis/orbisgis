package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AddSourceChoosePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private SourceChooseActionListener acl = null;
	private JComboBox typeDS = null;
	private AddDataBasePanel databasePanel = null;
	private AddFlatFilePanel flatFilePanel = null;
	private JPanel panCard = null;
	private CardLayout card = null;
	
	private final static String database = "Database";
	private final static String flatfile = "Flat File";
	private String[] type = {flatfile,database};
	

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
		
		//Adds a panel with a card layout so the user can choose between database of flat file
		panCard = new JPanel();
		card = new CardLayout(20,20); //20,20 : set space between components
		panCard.setLayout(card);
		add(panCard);
		
		databasePanel = new AddDataBasePanel();
		flatFilePanel = new AddFlatFilePanel();
		panCard.add(flatFilePanel, flatfile);
		panCard.add(databasePanel, database);
		
	}
	
	/** Retrieves the files selected or database parameters as an object */
	public Object getData() {
		Object ret = null;
		if (typeDS.getSelectedItem().equals(database)) {
			ret = databasePanel.getParameters();
		} else if (typeDS.getSelectedItem().equals(flatfile)) {
			ret = flatFilePanel.getFiles();
		}
		return ret;
	}

	private class SourceChooseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ("REFRESH".equals(e.getActionCommand())) {
				//Shows the panel the user selected...
				card.show(panCard, (String)typeDS.getSelectedItem());
			}
		}
	}
}
