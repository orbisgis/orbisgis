package org.orbisgis.geocatalog.resources.db;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.gdms.driver.h2.H2spatialDriver;
import org.sif.UIPanel;

public class SecondUIPanel implements UIPanel {
	private FirstUIPanel firstPanel;
	private SecondJPanel secondJPanel;
	private String[] allTablesNames = new String[0];
	private H2spatialDriver dBDriver;
	private Connection connection;

	public SecondUIPanel(final FirstUIPanel firstPanel) {
		this.firstPanel = firstPanel;
	}

	public Component getComponent() {
		if (null == secondJPanel) {
			secondJPanel = new SecondJPanel();
		}
		return new JScrollPane(secondJPanel);
	}

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		return "Select table(s) name(s)...";
	}

	public String initialize() {
		final String[] firstPanelValues = firstPanel.getValues();
		final String host = firstPanelValues[0];
		final int port = new Integer(firstPanelValues[1]);
		final String dbName = firstPanelValues[2];
		final String user = firstPanelValues[3];
		final String password = firstPanelValues[4];

		try {
			dBDriver = new H2spatialDriver();
			connection = dBDriver.getConnection(host, port, dbName, user,
					password);
			final TableDescription[] tableDescriptions = dBDriver
					.getTables(connection);
			allTablesNames = new String[tableDescriptions.length];

			for (int i = 0; i < tableDescriptions.length; i++) {
				allTablesNames[i] = tableDescriptions[i].getName();
			}
			secondJPanel.jList.setListData(allTablesNames);
			return null;
		} catch (SQLException e) {
			return e.getMessage();
		} catch (DriverException e) {
			return e.getMessage();
		}
	}

	public String validate() {
		if (-1 == secondJPanel.jList.getSelectedIndex()) {
			return "Select at least one table !";
		}
		return null;
	}

	private class SecondJPanel extends JPanel {
		JList jList;

		SecondJPanel() {
			this.setLayout(new BorderLayout());
			// setPreferredSize(new Dimension(300,300));
			jList = new JList(allTablesNames);
			jList.setToolTipText("You can select several tables");
//			jList.setVisibleRowCount(15);
			jList.setAutoscrolls(true);
			add(jList, BorderLayout.CENTER);
		}
	}
}