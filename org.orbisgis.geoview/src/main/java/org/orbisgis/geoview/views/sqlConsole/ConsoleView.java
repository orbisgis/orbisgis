/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.views.sqlConsole;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.showAttributes.Table;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.IProgressMonitor;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.geoview.views.sqlConsole.ui.ConsolePanel;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.background.BlockingBackgroundJob;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.tools.ViewContext;
import org.sif.UIFactory;

import com.Ostermiller.Syntax.HighlightedDocument;

public class ConsoleView implements IView {

	private final String EOL = System.getProperty("line.separator");
	private ViewContext viewContext;

	public Component getComponent(GeoView2D geoview) {
		return new ConsolePanel(HighlightedDocument.SQL_STYLE,
				new ConsoleListener() {

					public void save(String text) throws IOException {
						final SaveFilePanel outfilePanel = new SaveFilePanel(
								"org.orbisgis.geoview.sqlConsoleOutFile",
								"Save script");
						outfilePanel.addFilter("sql", "SQL script (*.sql)");

						if (UIFactory.showDialog(outfilePanel)) {
							final BufferedWriter out = new BufferedWriter(
									new FileWriter(outfilePanel
											.getSelectedFile()));
							out.write(text);
							out.close();
						}
					}

					public String open() throws IOException {
						final OpenFilePanel inFilePanel = new OpenFilePanel(
								"org.orbisgis.geoview.sqlConsoleInFile",
								"Load script");
						inFilePanel.addFilter("sql", "SQL script (*.sql)");

						if (UIFactory.showDialog(inFilePanel)) {
							File selectedFile = inFilePanel.getSelectedFile();
							final BufferedReader in = new BufferedReader(
									new FileReader(selectedFile));
							String line;
							StringBuffer ret = new StringBuffer();
							while ((line = in.readLine()) != null) {
								ret.append(line + EOL);
							}
							in.close();

							return ret.toString();
						} else {
							return null;
						}
					}

					public void execute(String text) {
						PluginManager
								.backgroundOperation(new ExecuteScriptProcess(
										text));
					}

				});
	}

	public void loadStatus(InputStream ois) {
	}

	public void saveStatus(OutputStream oos) {
	}

	public void delete() {
	}

	public void initialize(GeoView2D geoView2D) {
		this.viewContext = geoView2D.getViewContext();
	}

	private class ExecuteScriptProcess implements BlockingBackgroundJob {

		private String script;

		public ExecuteScriptProcess(String script) {
			this.script = script;
		}

		public String getTaskName() {
			return "Executing script";
		}

		public void run(IProgressMonitor pm) {
			SQLProcessor sqlProcessor = new SQLProcessor(OrbisgisCore.getDSF());
			Instruction[] instructions = new Instruction[0];

			try {

				try {
					instructions = sqlProcessor.prepareScript(script);
				} catch (SemanticException e) {
					PluginManager.error("Semantic error in the script", e);
				} catch (ParseException e) {
					PluginManager.error("Cannot parse script", e);
				}

				for (int i = 0; i < instructions.length; i++) {

					Instruction instruction = instructions[i];
					try {

						Metadata metadata = instruction.getResultMetadata();
						if (metadata != null) {
							boolean spatial = false;
							for (int k = 0; k < metadata.getFieldCount(); k++) {
								if (metadata.getFieldType(k).getTypeCode() == Type.GEOMETRY) {
									spatial = true;
								}
							}

							DataSource ds = instruction.getDataSource(pm);

							if (pm.isCancelled()) {
								break;
							}

							if (spatial) {

								ds.open();
								if (ds.getRowCount() > 0) {
									ds.cancel();
									final VectorLayer layer = LayerFactory
											.createVectorialLayer(ds);
									try {
										viewContext.getLayerModel()
												.insertLayer(layer, 0);
									} catch (LayerException e) {
										PluginManager.error(
												"Impossible to create the layer:"
														+ layer.getName(), e);
										break;
									} catch (CRSException e) {
										PluginManager.error(
												"CRS error in layer: "
														+ layer.getName(), e);
										break;
									}
								} else {
									JOptionPane.showMessageDialog(null,
											"The instruction : "
													+ instruction.getSQL()
													+ " returns no result.");
								}
							} else {
								final JDialog dlg = new JDialog();

								dlg.setTitle("Result from : "
										+ instruction.getSQL());
								dlg.setModal(true);
								dlg
										.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
								ds.open();
								dlg.getContentPane().add(new Table(ds));
								dlg.pack();
								dlg.setVisible(true);
								ds.cancel();
							}
						} else {
							instruction.execute(pm);

							if (pm.isCancelled()) {
								break;
							}

						}
					} catch (ExecutionException e) {
						PluginManager.error("Error executing the instruction:"
								+ instruction.getSQL(), e);
						break;
					} catch (SemanticException e) {
						PluginManager.error("Semantic error in instruction:"
								+ instruction.getSQL(), e);
						break;
					} catch (DataSourceCreationException e) {
						PluginManager.error("Cannot create the DataSource:"
								+ instruction.getSQL(), e);
						break;
					}

					pm.progressTo(100 * i / instructions.length);
				}

			} catch (DriverException e) {
				PluginManager.error("Data access error:", e);
			}
		}
	}

}