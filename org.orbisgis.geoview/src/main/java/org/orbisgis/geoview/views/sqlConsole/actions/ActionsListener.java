package org.orbisgis.geoview.views.sqlConsole.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JDialog;

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
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.views.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.geoview.views.sqlConsole.ui.ScrollPaneWest;
import org.orbisgis.geoview.views.sqlConsole.util.QueryHistory;
import org.orbisgis.geoview.views.sqlConsole.util.SQLConsoleUtilities;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.sif.UIFactory;

public class ActionsListener implements ActionListener {

	// Query history
	static final String historyFile = "SQLConsole.history"; //

	QueryHistory history = new QueryHistory(historyFile); //

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

		SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.geoview.sqlConsoleOutFile", "Select a sql file");
		outfilePanel.addFilter("sql", "SQL script (*.sql)");
		outfilePanel.addFilter("txt", "Text file (*.txt)");

		boolean ok = UIFactory.showDialog(outfilePanel);

		if (ok) {

			FileWriter out;
			try {
				out = new FileWriter(outfilePanel.getSelectedFile());

				// D'�crire le contenu du textArea
				String contenu = ScrollPaneWest.jTextArea.getText();
				// A l'aide d'un FileWriter dans le fichier qu'on a choisi
				out.write(contenu);
				// On ferme l'objet FileWriter
				out.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void openSQLFile() {

		OpenFilePanel inFilePanel = new OpenFilePanel(
				"org.orbisgis.geoview.sqlConsoleInFile", "Select a sql file");
		inFilePanel.addFilter("sql", "SQL script (*.sql)");

		boolean ok = UIFactory.showDialog(inFilePanel);

		if (ok) {

			FileReader in;
			try {
				in = new FileReader(inFilePanel.getSelectedFile());

				// De recopier le fichier dans le textArea
				int c;
				while ((c = in.read()) != -1) {
					String a = (char) c + "";
					ScrollPaneWest.jTextArea.append(a);
				}
				// On ferme le FileReader
				in.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			} catch (LayerException e) {
				PluginManager.error("Cannot add layer", e);
			}

		}
	}
}
