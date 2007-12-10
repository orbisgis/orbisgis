package org.orbisgis.geocatalog.resources.db;

import java.awt.Component;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;
import org.sif.SQLUIPanel;

public class FirstUIPanel implements SQLUIPanel {
	private FirstJPanel firstJPanel;

	public String[] getErrorMessages() {
		return new String[] { "The type must be jdbc:h2 or jdbc:portgresql" };
	}

	public String[] getFieldNames() {
		return new String[] { "dbType", "host", "port", "dbName", "user",
				"password" };
	}

	public int[] getFieldTypes() {
		return new int[] { STRING, STRING, INT, STRING, STRING, STRING };
	}

	public String getId() {
		return "org.orbisgis.geocatalog.resources.db.FirstUIPanel";
	}

	public String[] getValidationExpressions() {
		return new String[] { "((dbType LIKE 'jdbc:h2'))" };
		// return new String[] { "((dbType LIKE 'jdbc:h2') OR (dbType LIKE
		// 'jdbc:postgresql'))" };
	}

	public String[] getValues() {
		return new String[] { getDBType(), getHost(), getPort(), getDBName(),
				getUser(), getPassword() };
	}

	public void setValue(String fieldName, String fieldValue) {
		if (fieldName.equals("dbType")) {
			fieldValue = getDBType();
		} else if (fieldName.equals("host")) {
			fieldValue = getHost();
		} else if (fieldName.equals("port")) {
			fieldValue = getPort();
		} else if (fieldName.equals("dbName")) {
			fieldValue = getDBName();
		} else if (fieldName.equals("user")) {
			fieldValue = getUser();
		} else if (fieldName.equals("password")) {
			fieldValue = getPassword();
		} else {
			throw new Error();
		}
	}

	public Component getComponent() {
		if (null == firstJPanel) {
			firstJPanel = new FirstJPanel();
		}
		return firstJPanel;
	}

	public URL getIconURL() {
		return null;
	}

	public String getTitle() {
		return "Enter your DataBase parameters...";
	}

	public String initialize() {
		return null;
	}

	public String validate() {
		return null;
	}

	private String getDBType() {
		return firstJPanel.dbType.getText();
	}

	private String getHost() {
		return firstJPanel.host.getText();
	}

	private String getPort() {
		return firstJPanel.port.getText();
	}

	private String getDBName() {
		return firstJPanel.dbName.getText();
	}

	private String getUser() {
		return firstJPanel.user.getText();
	}

	private String getPassword() {
		return new String(firstJPanel.password.getPassword());
	}

	private class FirstJPanel extends JPanel {
		final int DEFAULT_TEXTFIELD_LENGTH = 20;
		JTextField dbType;
		JTextField host;
		JTextField port;
		JTextField dbName;
		JTextField user;
		JPasswordField password;

		FirstJPanel() {
			setLayout(new CRFlowLayout());
			dbType = new JTextField("jdbc:h2", DEFAULT_TEXTFIELD_LENGTH);
			host = new JTextField("localhost", DEFAULT_TEXTFIELD_LENGTH);
			port = new JTextField("-1", DEFAULT_TEXTFIELD_LENGTH);
			// dbName = new JTextField("/tmp/h2/essai1",
			// DEFAULT_TEXTFIELD_LENGTH);
			dbName = new JTextField("d:\\\\temp\\\\monH2db",
					DEFAULT_TEXTFIELD_LENGTH);
			user = new JTextField("sa", DEFAULT_TEXTFIELD_LENGTH);
			password = new JPasswordField("", DEFAULT_TEXTFIELD_LENGTH);

			final JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new CRFlowLayout());
			labelPanel.add(new JLabel("DataBase_type"));
			labelPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("Host name"));
			labelPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("Port number"));
			labelPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("DataBase name"));
			labelPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("User name"));
			labelPanel.add(new CarriageReturn());
			labelPanel.add(new JLabel("Password"));

			final JPanel fieldPanel = new JPanel();
			fieldPanel.setLayout(new CRFlowLayout());
			fieldPanel.add(dbType);
			fieldPanel.add(new CarriageReturn());
			fieldPanel.add(host);
			fieldPanel.add(new CarriageReturn());
			fieldPanel.add(port);
			fieldPanel.add(new CarriageReturn());
			fieldPanel.add(dbName);
			fieldPanel.add(new CarriageReturn());
			fieldPanel.add(user);
			fieldPanel.add(new CarriageReturn());
			fieldPanel.add(password);

			add(labelPanel);
			add(fieldPanel);
		}
	}
}