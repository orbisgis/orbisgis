package org.orbisgis.plugin.sqlconsole.actions;

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
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.strategies.FirstStrategy;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.sqlconsole.ui.SQLConsolePanel;
import org.orbisgis.plugin.sqlconsole.ui.ScrollPaneWest;
import org.orbisgis.plugin.sqlconsole.ui.Table;
import org.orbisgis.plugin.sqlconsole.util.QueryHistory;
import org.orbisgis.plugin.sqlconsole.util.SQLConsoleUtilities;
import org.orbisgis.plugin.view.layerModel.CRSException;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.utilities.file.SimpleFileFilter;
import org.urbsat.utilities.CreateGrid;

import com.hardcode.driverManager.DriverLoadException;

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

			ScrollPaneWest.jTextArea.setForeground(Color.BLACK);
			String query = ScrollPaneWest.jTextArea.getText();

			if (query.length() > 0) {

				String[] queries = SQLConsoleUtilities.split(query, ";");
				history.add(query);

				for (int t = 0; t < queries.length; t++) {

					DataSourceFactory dsf = TempPluginServices.dsf;

					String startQuery = queries[t].substring(0, 6)
							.toLowerCase();

					if (startQuery.equalsIgnoreCase("select")) {

						try {
							System.out.println(dsf.getDataSourcesDefinition()
									.toString());

							DataSource dsResult = dsf.executeSQL(queries[t]);
							dsResult.open();
							
							if (TypeFactory.IsSpatial(dsResult)) {
								SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
										dsResult);
								// System.out.println(sds.getAlias());
								// System.out.println(sds.getName());
								VectorLayer layer = new VectorLayer(dsResult
										.getName(), sds.getCRS(sds
										.getDefaultGeometry()));
								layer.setParent(TempPluginServices.lc);
								layer.setDataSource(sds);
								TempPluginServices.lc.put(layer);
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

						} catch (SyntaxException e1) {
							e1.printStackTrace();
						} catch (DriverLoadException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchTableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (DriverException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CRSException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} else if (queries[t].substring(0, 4).equalsIgnoreCase(
							"call")) {
						try {
							Class.forName(org.urbsat.Register.class.getName());

							DataSource dsResult = dsf.executeSQL(queries[t]);

							if (dsResult != null) {
								dsResult.open();

								Metadata m = dsResult.getMetadata();
								boolean isSpatial = false;
								for (int i = 0; i < m.getFieldCount(); i++) {
									if (m.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
										isSpatial = true;
										break;
									}
								}
								try {
									m = dsResult.getMetadata();

									for (int i = 0; i < m.getFieldCount(); i++) {
										if (m.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
											isSpatial = true;
											break;
										}
									}
								} catch (DriverException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

								if (isSpatial) {

									SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
											dsResult);

									dsf.getIndexManager().buildIndex(
											sds.getName(), "the_geom",
											SpatialIndex.SPATIAL_INDEX);

									FirstStrategy.indexes = true;

									// System.out.println(sds.getAlias());
									// System.out.println(sds.getName());
									VectorLayer layer = new VectorLayer(
											dsResult.getName(), sds.getCRS(sds
													.getDefaultGeometry()));
									layer.setParent(TempPluginServices.lc);
									layer.setDataSource(sds);
									try {
										TempPluginServices.lc.put(layer);
									} catch (CRSException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
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

							else {

							}

						} catch (SyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (DriverLoadException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchTableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (DriverException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IndexException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}

					else if (startQuery.equalsIgnoreCase("create")) {
						try {
							dsf.executeSQL(queries[t]);
						} catch (SyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (DriverLoadException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchTableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

			}

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
		boolean continueSave = true;
		// On cr�e un nouvel objet JFileChooser
		saver = new JFileChooser();

		// On applique un filtre
		saver.addChoosableFileFilter(new SimpleFileFilter("sql",
				"SQL script (*.sql)"));

		// On change le r�pertoire courant de d�part, on sera dans le dossier
		// fileSave
		saver.setCurrentDirectory(fileSave);
		// On fait appara�tre le JFileChooser � l'�cran
		int returnVal = saver.showSaveDialog(TempPluginServices.vf);

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

		// On change le r�pertoire courant de d�part, on sera dans le dossier
		// fileSave
		chooser.setCurrentDirectory(fileSave);
		// On fait appara�tre le JFileChooser � l'�cran
		int returnVal = chooser.showOpenDialog(TempPluginServices.vf);
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
}
