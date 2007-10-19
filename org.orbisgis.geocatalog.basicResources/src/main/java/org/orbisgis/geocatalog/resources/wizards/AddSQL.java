package org.orbisgis.geocatalog.resources.wizards;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.orbisgis.geocatalog.CRFlowLayout;
import org.orbisgis.geocatalog.CarriageReturn;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.SQLQuery;

public class AddSQL extends JPanel implements IAddRessourceWizard {
	
	private final Dimension preferredSize = new Dimension(500, 25);

	private JTextField name = null;

	private JTextField query = null;

	public AddSQL() {
		super();
		setLayout(new CRFlowLayout());
		add(new JLabel("Name"));
		name = new JTextField();
		name.setPreferredSize(preferredSize);
		add(name);

		add(new CarriageReturn());

		add(new JLabel("Query"));
		query = new JTextField();
		query.setPreferredSize(preferredSize);
		add(query);

	}

	public IResource[] getNewResources() {
		String theName = name.getText();
		String theQuery = query.getText();
		SQLQuery[] sql = new SQLQuery[1];

		if (theName.length() > 0 && theName != null && theQuery.length() > 0
				&& theQuery != null) {
			sql[0] = new SQLQuery(theName, query.getText());
		}
		
		return sql;
		
	}

	public JPanel getWizardUI() {
		return this;
	}

}
