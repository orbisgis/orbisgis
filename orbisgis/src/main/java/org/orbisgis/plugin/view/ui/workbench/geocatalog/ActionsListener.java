package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.ui.workbench.GeoView2DFrame;
import org.orbisgis.plugin.view.ui.workbench.FileChooser;

import com.hardcode.driverManager.DriverLoadException;

public class ActionsListener implements ActionListener {
	private JFrame jFrame = null;

	private Catalog myCatalog = null;

	public void setParameters(JFrame jFrame, Catalog myCatalog) {
		this.jFrame = jFrame;
		this.myCatalog = myCatalog;
	}

	public void actionPerformed(ActionEvent e) {

		if ("NEWGV".equals(e.getActionCommand())) {
			// Open a new GeoView
			// This code may be a little bit dirty...
			// TODO : cleanup !!
			if (TempPluginServices.vf == null) {
				PropertyConfigurator.configure(GeoView2DFrame.class
						.getResource("log4j.properties"));
				PatternLayout l = new PatternLayout("%p %t %C - %m%n");
				RollingFileAppender fa = null;
				try {
					fa = new RollingFileAppender(l, System
							.getProperty("user.home")
							+ File.separator
							+ "orbisgis"
							+ File.separator
							+ "orbisgis.log", false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				fa.setMaxFileSize("512KB");
				fa.setMaxBackupIndex(3);
				Logger.getRootLogger().addAppender(fa);
				GeoView2DFrame vf = new GeoView2DFrame(TempPluginServices.lc);
				// Register the geoview in temppluginservice
				TempPluginServices.vf = vf;
				vf.setLocation(100, 100);
				vf.setSize(800, 700);
				vf.setVisible(true);
			} else {
				TempPluginServices.vf.setVisible(true);
				TempPluginServices.vf.toFront();
			}

		} else if ("SAVESESSION".equals(e.getActionCommand())) {
			// Save the session
			System.err.println("Catalog will be saved in Test.xml");
			System.err.println("Do NOT clean the catalog, delete sources or close it :");
			System.err.println("DatasoureFactory would be deleted...");			

			try {
				XMLEncoder enc = new XMLEncoder(new BufferedOutputStream(
						new FileOutputStream("Test.xml")));

//				enc
//						.setPersistenceDelegate(MyNode.class,
//								new DefaultPersistenceDelegate(
//										MyNode.compatiblePersistenceString));

				MyNode test = myCatalog.getRootNode();
				
				enc.writeObject(test);

				enc.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

		} else if ("LOADSESSION".equals(e.getActionCommand())) {
			// Load a session
			System.err.println("This will load Test.xml");

			XMLDecoder d;
			try {
				d = new XMLDecoder(new BufferedInputStream(new FileInputStream(
						"Test.xml")));
				MyNode result = (MyNode) d.readObject();
				d.close();
				myCatalog.setRootNode(result);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			// SessionManager loader = new SessionManager(jFrame,
			// SessionManager.LOAD);

		} else if ("NEWFOLDER".equals(e.getActionCommand())) {
			String name = JOptionPane.showInputDialog(jFrame, "Name");
			if (name != null && name.length() != 0) {
				MyNode newNode = new MyNode(name, MyNode.folder);
				myCatalog.addNode(newNode);
			}

		} else if ("ADDDATA".equals(e.getActionCommand())) {
			// Creates a wizard to add a data source (flat file or database) or
			// a raster
			AssistantAddSource assistant = new AssistantAddSource(jFrame);

			// If the user pressed OK...
			if (assistant.userSayOk()) {
				Object data = assistant.getData();

				// ...and if we got files, lets add them to the catalog
				if (data instanceof File[]) {
					myCatalog.addFiles((File[]) data);

					// ...else if we got database parameters, let's register the
					// database
				} else if (data instanceof String[]) {
					myCatalog.addDataBase((String[]) data);
				}
			}

		} else if ("ADDSLDFILE".equals(e.getActionCommand())) {
			FileChooser ofc = new FileChooser("sld", "SLD files (*.sld)", true);
			ofc.showOpenDialog(jFrame);
			myCatalog.addFiles(ofc.getSelectedFiles());
		}

		else if ("OPENATTRIBUTES".equals(e.getActionCommand())) {

			if (myCatalog.getCurrentNode().getType() == MyNode.datasource) {

				String nodeName = myCatalog.getCurrentNode().toString();

				try {
					TempPluginServices.dsf
							.executeSQL("call SHOW('select * from " + nodeName
									+ "' , ' " + nodeName + " ')");
				} catch (SyntaxException e1) {
					e1.printStackTrace();
				} catch (DriverLoadException e1) {
					e1.printStackTrace();
				} catch (NoSuchTableException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
			}

		} else if ("DEL".equals(e.getActionCommand())) {
			// Removes the selected node
			if (JOptionPane.showConfirmDialog(jFrame,
					"Are you sure you want to delete this node ?",
					"Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
				myCatalog.removeNode();
			}

		} else if ("CLRCATALOG".equals(e.getActionCommand())) {
			// Clears the catalog
			if (JOptionPane.showConfirmDialog(jFrame,
					"Are you sure you want to clear the catalog ?",
					"Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
				myCatalog.clearCatalog();
			}
		} else if ("ADDSQL".equals(e.getActionCommand())) {
			// Add a SQL request

			AddSqlQuery sql = new AddSqlQuery(jFrame, null);

			if (sql.userSayOk()) {
				String name = sql.getName();
				String query = sql.getQuery();
				if (name.length() > 0 && name != null && query.length() > 0
						&& query != null) {
					MyNode newNode = new MyNode(name, MyNode.sqlquery, query);
					myCatalog.addNode(newNode);
				}
			}

		} else if ("CHANGESQL".equals(e.getActionCommand())) {
			// Modify a SQL request

			MyNode nodeToChange = myCatalog.getCurrentNode();
			AddSqlQuery sql = new AddSqlQuery(jFrame, nodeToChange);

			if (sql.userSayOk()) {
				String name = sql.getName();
				String query = sql.getQuery();
				if (name.length() > 0 && name != null && query.length() > 0
						&& query != null) {
					nodeToChange.setName(name);
					nodeToChange.setQuery(query);
				}
			}

		} else if ("EXIT".equals(e.getActionCommand())) {
			// Exit the program
			System.exit(0);

		} else if ("ABOUT".equals(e.getActionCommand())) {
			// Shows the about dialog
			JOptionPane.showMessageDialog(jFrame, "GeoCatalog\nVersion 0.0",
					"About GeoCatalog", JOptionPane.INFORMATION_MESSAGE);

		}
	}
}