/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.views.sqlConsole;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.showAttributes.Table;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editors.map.MapContextManager;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.view.IView;
import org.orbisgis.views.geocognition.TransferableGeocognitionElement;
import org.orbisgis.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.views.sqlConsole.ui.ConsolePanel;
import org.sif.UIFactory;

public class ConsoleView implements IView {

	private static final Logger logger = Logger.getLogger(ConsoleView.class);

	private final String EOL = System.getProperty("line.separator");

	public Component getComponent() {
		return new ConsolePanel(true, new ConsoleListener() {

			public void save(String text) throws IOException {
				final SaveFilePanel outfilePanel = new SaveFilePanel(
						"org.orbisgis.views.sqlConsoleOutFile", "Save script");
				outfilePanel.addFilter("sql", "SQL script (*.sql)");

				if (UIFactory.showDialog(outfilePanel)) {
					final BufferedWriter out = new BufferedWriter(
							new FileWriter(outfilePanel.getSelectedFile()));
					out.write(text);
					out.close();
				}
			}

			public String open() throws IOException {
				final OpenFilePanel inFilePanel = new OpenFilePanel(
						"org.orbisgis.views.sqlConsoleInFile", "Load script");
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
				BackgroundManager bm = (BackgroundManager) Services
						.getService("org.orbisgis.BackgroundManager");
				bm.backgroundOperation(new ExecuteScriptProcess(text));
			}

			@Override
			public void change() {
			}

			@Override
			public boolean showControlButtons() {
				return true;
			}

			@Override
			public String doDrop(Transferable t) {
				DataFlavor geocogFlavor = TransferableGeocognitionElement.geocognitionFlavor;
				if (t.isDataFlavorSupported(geocogFlavor)) {
					try {
						GeocognitionElement[] elems = (GeocognitionElement[]) t
								.getTransferData(geocogFlavor);
						if (elems.length == 1) {
							if ((elems[0].getTypeId()
									.equals(GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID))
									|| (elems[0].getTypeId()
											.equals(GeocognitionFunctionFactory.JAVA_FUNCTION_ID))) {
								Function f = FunctionManager
										.getFunction(elems[0].getId());
								if (f != null) {
									return f.getSqlOrder();
								}
							} else if ((elems[0].getTypeId()
									.equals(GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID))
									|| (elems[0].getTypeId()
											.equals(GeocognitionCustomQueryFactory.JAVA_QUERY_ID))) {
								CustomQuery cq = QueryManager.getQuery(elems[0]
										.getId());
								if (cq != null) {
									return cq.getSqlOrder();
								}
							}
						}
					} catch (UnsupportedFlavorException e) {
						logger.error("bug dropping function", e);
					} catch (IOException e) {
						logger.error("bug dropping function", e);
					}
				}
				return null;
			}

		});
	}

	public void loadStatus() {
	}

	public void saveStatus() {
	}

	public void delete() {
	}

	public void initialize() {
	}

	private class ExecuteScriptProcess implements BackgroundJob {

		private String script;

		public ExecuteScriptProcess(String script) {
			this.script = script;
		}

		public String getTaskName() {
			return "Executing script";
		}

		public void run(IProgressMonitor pm) {
			SQLProcessor sqlProcessor = new SQLProcessor(
					((DataManager) Services
							.getService("org.orbisgis.DataManager")).getDSF());
			String[] instructions = new String[0];

			long t1 = System.currentTimeMillis();
			try {

				try {
					instructions = sqlProcessor.getScriptInstructions(script);
				} catch (SemanticException e) {
					Services.getErrorManager().error(
							"Semantic error in the script", e);
				} catch (ParseException e) {
					Services.getErrorManager().error("Cannot parse script", e);
				}

				MapContext vc = ((MapContextManager) Services
						.getService("org.orbisgis.MapContextManager"))
						.getActiveView();

				DataManager dataManager = (DataManager) Services
						.getService("org.orbisgis.DataManager");
				for (int i = 0; i < instructions.length; i++) {

					try {
						Instruction instruction = sqlProcessor
								.prepareInstruction(instructions[i]);

						Metadata metadata = instruction.getResultMetadata();
						if (metadata != null) {
							boolean spatial = false;
							for (int k = 0; k < metadata.getFieldCount(); k++) {
								int typeCode = metadata.getFieldType(k)
										.getTypeCode();
								if ((typeCode == Type.GEOMETRY)
										|| (typeCode == Type.RASTER)) {
									spatial = true;
								}
							}

							DataSource ds = instruction.getDataSource(pm);

							if (pm.isCancelled()) {
								break;
							}

							if (spatial) {

								final ILayer layer = dataManager
										.createLayer(ds);
								try {
									if (vc != null) {
										vc.getLayerModel()
												.insertLayer(layer, 0);
									}
								} catch (LayerException e) {
									Services.getErrorManager().error(
											"Impossible to create the layer:"
													+ layer.getName(), e);
									break;
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
								ds.close();
							}
						} else {
							instruction.execute(pm);

							if (pm.isCancelled()) {
								break;
							}

						}
					} catch (ExecutionException e) {
						Services.getErrorManager().error(
								"Error executing the instruction:"
										+ instructions[i], e);
						break;
					} catch (SemanticException e) {
						Services.getErrorManager().error(
								"Semantic error in instruction:"
										+ instructions[i], e);
						break;
					} catch (DataSourceCreationException e) {
						Services.getErrorManager().error(
								"Cannot create the DataSource:"
										+ instructions[i], e);
						break;
					} catch (ParseException e) {
						Services.getErrorManager().error(
								"Parse error in statement:" + instructions[i],
								e);
						break;
					}

					pm.progressTo(100 * i / instructions.length);
				}

			} catch (DriverException e) {
				Services.getErrorManager().error("Data access error:", e);
			}

			long t2 = System.currentTimeMillis();
			logger.debug("Execution time: " + ((t2 - t1) / 1000.0));
		}
	}

}