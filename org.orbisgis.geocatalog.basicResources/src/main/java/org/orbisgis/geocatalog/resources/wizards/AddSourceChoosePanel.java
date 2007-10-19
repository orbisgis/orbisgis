package org.orbisgis.geocatalog.resources.wizards;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geocatalog.CRFlowLayout;
import org.orbisgis.geocatalog.CarriageReturn;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.utilities.FileUtility;

/**
 * Panel to add datasources of any kind : flat files AND databases
 *
 * @author Samuel CHEMLA
 *
 */
public class AddSourceChoosePanel extends JPanel implements IAddRessourceWizard {

	private static final long serialVersionUID = 1L;
	private SourceChooseActionListener acl = null;
	private JComboBox typeDS = null;
	private AddDataBasePanel databasePanel = null;
	private AddFlatFilePanel flatFilePanel = null;
	private JPanel panCard = null;
	private CardLayout card = null;

	private final static String database = "Database";
	private final static String flatfile = "Flat File";
	private String[] type = { flatfile, database };

	/**
	 * List of supported files that the user will be able to choose.
	 *
	 * See FileChooser.java for the syntax of supported files. It is located in
	 * org.orbisgis.plugin.view.ui.workbench
	 */
	final String[][] supportedFiles = {
			{ "shp", "csv", "dbf" },
			{ "Vector files (*.shp, *.csv, *.dbf)" },
			{ "tif", "tiff", "asc" },
			{ "Raster Files (*.tif, *.tiff, *.asc)" },
			{ "shp" },
			{ "SHP Files (*.shp)" },
			{ "cir" },
			{ "CIR Files (*.cir)" },
			{ "png" },
			{ "PNG Files (*.png)" },
			{ "shp", "csv", "dbf", "tif", "tiff", "asc", "cir", "png" },
			{ "All supported files (*.shp, *.csv, *.dbf, *.tif, *.tiff, *.asc, *.cir, *.png)" } };

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

		// Adds a panel with a card layout so the user can choose between
		// database of flat file
		panCard = new JPanel();
		card = new CardLayout(20, 20); // 20,20 : set space between components
		panCard.setLayout(card);
		add(panCard);

		databasePanel = new AddDataBasePanel();
		flatFilePanel = new AddFlatFilePanel(supportedFiles);

		panCard.add(flatFilePanel, flatfile);
		panCard.add(databasePanel, database);

	}

	private class SourceChooseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ("REFRESH".equals(e.getActionCommand())) {
				// Shows the panel the user selected...
				card.show(panCard, (String) typeDS.getSelectedItem());
			}
		}
	}

	public IResource[] getNewResources() {
		ArrayList<IResource> ressources = new ArrayList<IResource>();

		if (typeDS.getSelectedItem().equals(database)) {
			// TODO databasePanel.getParameters();

		} else if (typeDS.getSelectedItem().equals(flatfile)) {
			File[] files = flatFilePanel.getFiles();

			for (File file : files) {
				String name = file.getName();
				String extension = FileUtility.getFileExtension(file);
				String nickname = name.substring(0, name.indexOf("."
						+ extension));
				DataSourceDefinition def = new FileSourceDefinition(file);

				try {

					if ("shp".equalsIgnoreCase(extension)
							| "csv".equalsIgnoreCase(extension)
							| "cir".equalsIgnoreCase(extension)
							| "dbf".equalsIgnoreCase(extension)
							| "asc".equalsIgnoreCase(extension)
							| "tif".equalsIgnoreCase(extension)
							| "tiff".equalsIgnoreCase(extension)
							| "png".equalsIgnoreCase(extension)) {

						// Check for an already existing DataSource with the
						// name
						// provided
						// and change it if necessary TODO : datasourcefactory
						// should rename
						// by itself datasources and return the name he choosed
						int i = 0;
						String tmpName = nickname;
						while (OrbisgisCore.getDSF().existDS(tmpName)) {
							i++;
							tmpName = nickname + "_" + i;
						}
						nickname = tmpName;

						OrbisgisCore.getDSF().registerDataSource(nickname, def);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return ressources.toArray(new IResource[0]);
	}

	public JPanel getWizardUI() {
		return this;
	}
}
