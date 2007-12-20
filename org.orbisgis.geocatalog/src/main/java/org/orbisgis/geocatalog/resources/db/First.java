package org.orbisgis.geocatalog.resources.db;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.gdms.data.types.Type;
import org.sif.AbstractUIPanel;
import org.sif.SQLUIPanel;

public class First extends AbstractUIPanel implements SQLUIPanel {
	private final static int LENGTH = 20;
	private FirstJPanel firstJPanel;

	// public First() {
	// super("org.orbisgis.geocatalog.resources.db.FirstUIPanel",
	// "Connect to database");
	// setInfoText("Introduce the connection parameters");
	// addInput("dbType", "DataBase type", "jdbc:postgresql", new StringType(
	// LENGTH));
	// addValidationExpression(
	// "((dbType LIKE 'jdbc:h2') or (dbType LIKE 'jdbc:postgresql'))",
	// "DataBase type must be jdbc:h2 or jdbc:postgresql");
	// addInput("host", "Host name", "192.168.10.53", new StringType(LENGTH));
	// addInput("port", "Port number", "5432", new IntType(LENGTH));
	// addValidationExpression("(port >= 0) and (port <= 32767)",
	// "Port number is a number in the range [0,32767]");
	// addInput("dbName", "DataBase name", "gdms", new StringType(LENGTH));
	// addValidationExpression("strlen(dbName) > 0",
	// "DataBase name is mandatory!");
	// addInput("user", "User name", "postgres", new StringType(LENGTH));
	// addInput("password", "Password", "", new PasswordType(LENGTH));
	// }

	public Component getComponent() {
		if (null == firstJPanel) {
			firstJPanel = new FirstJPanel();
		}
		return firstJPanel;
	}

	public String getTitle() {
		return "Connect to database";
	}

	public String validateInput() {
		// TODO Auto-generated method stub
		return null;
	}

	private class FirstJPanel extends JPanel {
		JTextField dbType;
		JTextField host;
		JTextField port;
		JTextField dbName;
		JTextField user;
		JPasswordField password;

		FirstJPanel() {
			// this.setLayout(new BorderLayout());

			dbType = new JTextField(10);
			host = new JTextField(10);
			port = new JTextField(10);
			dbName = new JTextField(10);
			user = new JTextField(10);
			password = new JPasswordField(10);

			add(dbType);
			add(host);
			add(port);
			add(dbName);
			add(user);
			add(password);
		}
	}

	public String getValue(final String fieldName) {
		if (fieldName.equals("dbType")) {
			return firstJPanel.dbType.getText();
		} else if (fieldName.equals("host")) {
			return firstJPanel.host.getText();
		} else if (fieldName.equals("port")) {
			return firstJPanel.port.getText();
		} else if (fieldName.equals("dbName")) {
			return firstJPanel.dbName.getText();
		} else if (fieldName.equals("user")) {
			return firstJPanel.user.getText();
		} else if (fieldName.equals("password")) {
			return new String(firstJPanel.password.getPassword());
		}
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getErrorMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getFieldNames() {
		// TODO Auto-generated method stub
		return new String[] { "dbType", "host", "port", "dbName", "user",
				"password" };
	}

	public int[] getFieldTypes() {
		// TODO Auto-generated method stub
		return new int[] { STRING, STRING, STRING, STRING, STRING, STRING };
	}

	public String getId() {
		// TODO Auto-generated method stub
		return "AAAAAAA";
	}

	public String[] getValidationExpressions() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getValues() {
		// TODO Auto-generated method stub
		return new String[] { firstJPanel.dbType.getText(),
				firstJPanel.host.getText(), firstJPanel.port.getText(),
				firstJPanel.dbName.getText(), firstJPanel.user.getText(),
				new String(firstJPanel.password.getPassword()) };
	}

	public void setValue(String fieldName, String fieldValue) {
		if (fieldName.equals("dbType")) {
			firstJPanel.dbType.setText(fieldValue);
		} else if (fieldName.equals("host")) {
			firstJPanel.host.setText(fieldValue);
		} else if (fieldName.equals("port")) {
			firstJPanel.port.setText(fieldValue);
		} else if (fieldName.equals("dbName")) {
			firstJPanel.dbName.setText(fieldValue);
		} else if (fieldName.equals("user")) {
			firstJPanel.user.setText(fieldValue);
		} else if (fieldName.equals("password")) {
			firstJPanel.password.setText(fieldValue);
		}
		// TODO Auto-generated method stub

	}
}