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
	
	final static String database = "Database";
	final static String flatfile = "Flat File";
	private String[] type = {database, flatfile};
	

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
		
		panCard = new JPanel();
		card = new CardLayout(30,10);
		panCard.setLayout(card);
		add(panCard);
		
		databasePanel = new AddDataBasePanel();
		flatFilePanel = new AddFlatFilePanel();
		panCard.add(databasePanel, database);
		panCard.add(flatFilePanel, flatfile);
		

		add(new CarriageReturn());
	}

	private class SourceChooseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ("REFRESH".equals(e.getActionCommand())) {
				card.show(panCard, (String)typeDS.getSelectedItem());
			}
		}
	}
}
