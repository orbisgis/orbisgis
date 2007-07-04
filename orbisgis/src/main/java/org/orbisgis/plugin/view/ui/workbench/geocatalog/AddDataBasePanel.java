package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AddDataBasePanel extends JPanel{

	// Names for databases drivers. Please put the driver name used by "call register..."
	private final static String h2 = "h2";
	private String[] type = {h2};
	
	private static final long serialVersionUID = 1L;
	private JComboBox typeDB = null;
	private JTextField connectName = null;
	private JTextField host = null;
	private JTextField port = null;
	private JTextField DBName = null;
	private JTextField tableName = null;
	private JTextField alias = null;
	private JTextField userName = null;
	private JPasswordField password = null;

	public AddDataBasePanel() {
		JLabel label = null;
		setLayout(new GridLayout(10,2,10,10)); //8Lines, 2Columns, 10HorizontalSpace, 10VerticalSpace
		
		label = new JLabel("Connection name:");
		add(label);
		
		connectName = new JTextField();
		add(connectName);
		
		label = new JLabel("Database type:");
		add(label);
		
		typeDB = new JComboBox(type);
		add(typeDB);
		
		label = new JLabel("Host:");
		add(label);
		
		host = new JTextField();
		add(host);
		
		label = new JLabel("Port:");
		add(label);
		
		port = new JTextField();
		add(port);
		
		label = new JLabel("Database name:");
		add(label);
		
		DBName = new JTextField();
		add(DBName);
		
		label = new JLabel("Table name:");
		add(label);
		
		tableName = new JTextField();
		add(tableName);
		
		label = new JLabel("Alias:");
		add(label);
		
		alias = new JTextField();
		add(alias);
		
		label = new JLabel("User name:");
		add(label);
		
		userName = new JTextField();
		add(userName);

		label = new JLabel("Password:");
		add(label);
		
		password = new JPasswordField();
		add(password);
		
		//TODO : Choose table, path for h2, alias
	}

	/**
	 * 
	 * @return driver host port DBName userName password
	 */
	public String[] getParameters(){
		String parameters[] = {(String)typeDB.getSelectedItem(),host.getText(),port.getText(),DBName.getText(),userName.getText(),getPassword(),tableName.getText(),alias.getText()};
		return parameters;
	}
	
	public String getPassword(){
		String passwd = "";
		char[] pass = password.getPassword();
		int length = pass.length;
		for (int i=0; i<length; i++) {
			passwd = passwd + pass[i];
		}
		return passwd;
	}
}
