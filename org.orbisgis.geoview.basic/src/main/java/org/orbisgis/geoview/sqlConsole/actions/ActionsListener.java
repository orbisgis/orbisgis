package org.orbisgis.geoview.sqlConsole.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.customQuery.showAttributes.Table;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.geoview.sqlConsole.ui.ScrollPaneWest;
import org.orbisgis.geoview.sqlConsole.util.QueryHistory;
import org.orbisgis.geoview.sqlConsole.util.SQLConsoleUtilities;
import org.orbisgis.geoview.sqlConsole.util.SimpleFileFilter;
import org.orbisgis.pluginManager.PluginManager;

public class ActionsListener implements ActionListener {

	// Query history
	static final String historyFile = "SQLConsole.history"; //

	QueryHistory history = new QueryHistory(historyFile); //

	private JFileChooser saver;

	File fileSave = new File("./");

	private JFileChooser chooser;

	boolean continueSave = false;

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand() == "ERASE") {

			ScrollPaneWest.jTextArea.setForeground(Color.BLACK);
			ScrollPaneWest.jTextArea.setText("");

		}

		if (e.getActionCommand() == "OPENSQLFILE") {

			openSQLFile();

		}

		if (e.getActionCommand() == "SAVEQUERY") {

			saveCurrentQuery();
		}

		if (e.getActionCommand() == "EXECUTE") {

			execute();
		}

		if (e.getActionCommand() == "NEXT") {

			nextQuery();
		}

		if (e.getActionCommand() == "PREVIOUS") {

			previousQuery();
		}

	}

	/**
	 * Call the previous query in history.
	 */
	void previousQuery() {
		if (history.isPrevAvailable())
			setQuery(history.getPrev());
		updateHistoryButtons();
	}

	/**
	 * Call the next qsuery in history.
	 */
	void nextQuery() {
		if (history.isNextAvailable())
			setQuery(history.getNext());
		updateHistoryButtons();
	}

	/**
	 * Query setter.
	 */
	void setQuery(String query) {
		ScrollPaneWest.jTextArea.setText(query);
	}

	/**
	 * Enable/disable history buttons.
	 * 
	 * @param prev
	 *            A <code>boolean</code> value that gives the state of the
	 *            prev button.
	 * @param next
	 *            A <code>boolean</code> value that gives the state of the
	 *            next button.
	 */
	void setEnabled(boolean prev, boolean next) {
		SQLConsolePanel.jButtonPrevious.setEnabled(prev);
		SQLConsolePanel.jButtonNext.setEnabled(next);
	}

	/**
	 * This method is called to update history buttons.
	 */
	void updateHistoryButtons() {
		setEnabled(history.isPrevAvailable(), history.isNextAvailable());
	}

	public void saveCurrentQuery() {

		// c.f. text � sauvegarder
		// On cr�e un nouvel objet JFileChooser
		saver = new JFileChooser();

		// On applique un filtre
		saver.addChoosableFileFilter(new SimpleFileFilter("sql",
				"SQL script (*.sql)"));

		// On change le r�pertoire courant de d�part, on sera dans le
		// dossier
		// fileSave
		saver.setCurrentDirectory(fileSave);
		// On fait appara�tre le JFileChooser � l'�cran
		int returnVal = saver.showSaveDialog(ScrollPaneWest.geoview);

		// Si le fichier choisi peut etre sauv�
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			// Le fichier fileSave devient le fichier s�lectionner
			fileSave = saver.getSelectedFile();

			// On essaie
			try {

				FileWriter out = new FileWriter(fileSave);

				// D'�crire le contenu du textArea
				String contenu = ScrollPaneWest.jTextArea.getText();
				// A l'aide d'un FileWriter dans le fichier qu'on a choisi
				out.write(contenu);
				// On ferme l'objet FileWriter
				out.close();

			}
			// Si ca ne fonctionne pas
			catch (Exception ex) {
				// On cache la fen�tre
				saver.setVisible(false);
			}

		}
	}

	public void openSQLFile() {

		// On cr��e un nouvel objet JFileChooser
		chooser = new JFileChooser();

		// On applique un filtre
		chooser.addChoosableFileFilter(new SimpleFileFilter("sql",
				"SQL script (*.sql)"));

		// On change le r�pertoire courant de d�part, on sera dans le
		// dossier
		// fileSave
		chooser.setCurrentDirectory(fileSave);
		// On fait appara�tre le JFileChooser � l'�cran
		int returnVal = chooser.showOpenDialog(ScrollPaneWest.geoview);
		// Si le fichier choisi peut s'ouvrir
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// On efface le text contenu dans le textArea
			ScrollPaneWest.jTextArea.setText("");
			// Le fichier fileSave devient le fichier s�lectionner
			fileSave = chooser.getSelectedFile();
			// c.f sauvegarder
			continueSave = true;

			// On essaie
			try {

				FileReader in = new FileReader(fileSave);

				// De recopier le fichier dans le textArea
				int c;
				while ((c = in.read()) != -1) {
					String a = (char) c + "";
					ScrollPaneWest.jTextArea.append(a);
				}
				// On ferme le FileReader
				in.close();

			}
			// Si ca ne fonctionne pas
			catch (Exception ex) {
				// On ferme la fen�tre
				chooser.setVisible(false);
			}
		}

	}

	public void execute() {
		ScrollPaneWest.jTextArea.setForeground(Color.BLACK);
		String query = ScrollPaneWest.jTextArea.getText();

		if (query.length() > 0) {

			String[] queries = SQLConsoleUtilities.split(query, ";");
			history.add(query);

			try {
				for (int t = 0; t < queries.length; t++) {

					DataSourceFactory dsf = OrbisgisCore.getDSF();
					DataSource dsResult = null;

					// String startQuery = queries[t].substring(0, 6)
					// .toLowerCase();

					if (queries[t] != null) {

						if (queries[t].toLowerCase().startsWith("select")) {

							dsResult = dsf.executeSQL(queries[t]);

							if (dsResult != null) {

								dsResult.open();

								if (TypeFactory.IsSpatial(dsResult)) {

									VectorLayer layer = LayerFactory
											.createVectorialLayer(dsResult
													.getName(), dsResult);
									ScrollPaneWest.geoview.getViewContext()
											.getRootLayer().put(layer);
								} else {
									Table table = new Table(dsResult);
									JDialog dlg = new JDialog();
									dlg.setModal(true);
									dlg
											.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
									dlg.getContentPane().add(table);
									dlg.pack();
									dlg.setVisible(true);
								}

								dsResult.cancel();

							}
						} else if (queries[t].toLowerCase()
								.startsWith("create")) {
							dsf.executeSQL(queries[t]);
						}
					}
				}
			} catch (SyntaxException e1) {
				PluginManager.error("The has syntactic errors", e1);
			} catch (DriverLoadException e1) {
				throw new RuntimeException(e1);
			} catch (NoSuchTableException e1) {
				PluginManager.error("Table not found", e1);
			} catch (ExecutionException e1) {
				PluginManager.error("Error executing sql", e1);
			} catch (DriverException e1) {
				PluginManager.error("Data access error", e1);
			} catch (CRSException e1) {
				PluginManager.error("Cannot add layer", e1);
			}

		}
	}
}
