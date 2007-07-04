package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AddDataBasePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	JTextField connectName = null;
	JTextField host = null;
	JTextField port = null;
	JTextField DBName = null;
	JTextField userName = null;
	JPasswordField password = null;

	public AddDataBasePanel() {
		JLabel label = null;
		setLayout(new GridLayout(8,2,10,10)); //8Lines, 2Columns, 10HorizontalSpace, 10VerticalSpace
		
		label = new JLabel("Connection name:");
		add(label);
		
		connectName = new JTextField();
		add(connectName);
		
		label = new JLabel("Database type:");
		add(label);
		
		add(new JComboBox());
		
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
		
		label = new JLabel("User name:");
		add(label);
		
		userName = new JTextField();
		add(userName);

		label = new JLabel("Password:");
		add(label);
		
		password = new JPasswordField();
		add(password);
		
		//TODO : Choose table
	}

	public String[] getParameters(){
		String parameters[] = {host.getText(),port.getText(),DBName.getText(),userName.getText()};
		return parameters;
	}
	
	public char[] getPassword(){
		return password.getPassword();
	}
}
