package org.orbisgis.geoview.views.sqlConsole.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.customQuery.showAttributes.Table;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.views.sqlConsole.ui.ConsoleAction;
import org.orbisgis.geoview.views.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.geoview.views.sqlConsole.util.QueryHistory;
import org.orbisgis.geoview.views.sqlConsole.util.SQLConsoleUtilities;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class ActionsListener implements ActionListener {
	// Query history
	static final String historyFile = "SQLConsole.history"; //

	QueryHistory history = new QueryHistory(historyFile); //

	private SQLConsolePanel consolePanel;

	private final String EOL = System.getProperty("line.separator");

	public ActionsListener(SQLConsolePanel consolePanel) {
		this.consolePanel = consolePanel;
	}

	public void actionPerformed(ActionEvent e) {
		switch (new Integer(e.getActionCommand())) {
		case ConsoleAction.EXECUTE:
			execute();
			break;

		case ConsoleAction.CLEAR:
			consolePanel.getJTextArea().setForeground(Color.BLACK);
			consolePanel.getJTextArea().setText("");
			break;

		case ConsoleAction.STOP:
			break;

		case ConsoleAction.PREVIOUS:
			previous();
			break;

		case ConsoleAction.NEXT:
			next();
			break;

		case ConsoleAction.OPEN:
			open();
			break;

		case ConsoleAction.SAVE:
			save();
			break;
		}
	}

	/**
	 * Call the previous query in history.
	 */
	void previous() {
		if (history.isPrevAvailable())
			setQuery(history.getPrev());
		updateHistoryButtons();
	}

	/**
	 * Call the next qsuery in history.
	 */
	void next() {
		if (history.isNextAvailable())
			setQuery(history.getNext());
		updateHistoryButtons();
	}

	/**
	 * Query setter.
	 */
	void setQuery(String query) {
		consolePanel.getJTextArea().setText(query);
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
		consolePanel.getBtPrevious().setEnabled(prev);
		consolePanel.getBtNext().setEnabled(next);
	}

	/**
	 * This method is called to update history buttons.
	 */
	void updateHistoryButtons() {
		setEnabled(history.isPrevAvailable(), history.isNextAvailable());
	}

	public void save() {
		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.geoview.sqlConsoleOutFile", "Select a sql file");
		outfilePanel.addFilter("sql", "SQL script (*.sql)");
		outfilePanel.addFilter("txt", "Text file (*.txt)");

		if (UIFactory.showDialog(outfilePanel)) {
			try {
				final BufferedWriter out = new BufferedWriter(new FileWriter(
						outfilePanel.getSelectedFile()));
				out.write(consolePanel.getJTextArea().getText());
				out.close();
			} catch (IOException e) {
				PluginManager.warning("IOException with "
						+ outfilePanel.getSelectedFile(), e);
			}
		}
	}

	private void open() {
		final OpenFilePanel inFilePanel = new OpenFilePanel(
				"org.orbisgis.geoview.sqlConsoleInFile", "Select a sql file");
		inFilePanel.addFilter("sql", "SQL script (*.sql)");

		if (UIFactory.showDialog(inFilePanel)) {
			try {
				for (File selectedFile : inFilePanel.getSelectedFiles()) {
					final BufferedReader in = new BufferedReader(
							new FileReader(selectedFile));
					String line;
					while ((line = in.readLine()) != null) {
						consolePanel.getJTextArea().append(line + EOL);
					}
					in.close();
				}
			} catch (FileNotFoundException e) {
				PluginManager.warning("SQL script file not found : "
						+ inFilePanel.getSelectedFile(), e);
			} catch (IOException e) {
				PluginManager.warning("IOException with "
						+ inFilePanel.getSelectedFile(), e);
			}
		}
	}

	public void execute() {
		final DataSourceFactory dsf = OrbisgisCore.getDSF();
		consolePanel.getJTextArea().setForeground(Color.BLACK);
		final String queryPanelContent = consolePanel.getJTextArea().getText();
		String currentQuery = null;

		if (queryPanelContent.length() > 0) {
			final String[] queries = queryPanelContent.split(";");
			history.add(queryPanelContent);
			try {
				for (String query : queries) {
					query = query.trim();
					currentQuery = query;
					if (query.length() > 1) {
						final DataSource ds = dsf.executeSQL(query);
						if (null != ds) {
							ds.open();
							if (MetadataUtilities.isSpatial(ds.getMetadata())) {
								final VectorLayer layer = LayerFactory
										.createVectorialLayer(ds.getName(), ds);
								consolePanel.getGeoview().getViewContext()
										.getRootLayer().put(layer);
							} else {
								final JDialog dlg = new JDialog();
								dlg.setModal(true);
								dlg
										.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
								dlg.getContentPane().add(new Table(ds));
								dlg.pack();
								dlg.setVisible(true);
							}
							ds.cancel();
						}
					}
				}
			} catch (SyntaxException e) {
				PluginManager.error("Syntactic errors in instruction : "
						+ currentQuery, e);
			} catch (DriverLoadException e) {
				throw new RuntimeException(e);
			} catch (NoSuchTableException e) {
				PluginManager.error("Table not found", e);
			} catch (ExecutionException e) {
				PluginManager.error("Error executing sql instruction : "
						+ currentQuery, e);
			} catch (DriverException e) {
				PluginManager.error("Data access error", e);
			} catch (CRSException e) {
				PluginManager.error("Cannot add vector layer", e);
			} catch (LayerException e) {
				PluginManager.error("Cannot add vector layer", e);
			}
		}
	}
}