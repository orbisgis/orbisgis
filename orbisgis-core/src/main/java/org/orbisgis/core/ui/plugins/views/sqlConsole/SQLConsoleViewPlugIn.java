/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.views.sqlConsole;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.TokenMgrError;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.OrbisGISPersitenceConfig;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.OpenFilePanel;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.geocognition.TransferableGeocognitionElement;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.views.OutputManager;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.SQLConsoleKeyListener;
import org.orbisgis.core.ui.plugins.views.sqlConsole.codereformat.CodeReformator;
import org.orbisgis.core.ui.plugins.views.sqlConsole.codereformat.CommentSpec;
import org.orbisgis.core.ui.plugins.views.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.core.ui.plugins.views.sqlConsole.util.CodeErrors;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.progress.IProgressMonitor;

public class SQLConsoleViewPlugIn extends ViewPlugIn {

	private SQLConsolePanel panel;
	private static final Logger logger = Logger
			.getLogger(SQLConsoleViewPlugIn.class);
	private final String EOL = System.getProperty("line.separator");
	private JMenuItem menuItem;

	static CommentSpec[] COMMENT_SPECS = new CommentSpec[] {
			new CommentSpec("/*", "*/"), new CommentSpec("--", "\n") };

	public void initialize(PlugInContext context) throws Exception {
		panel = new SQLConsolePanel(new ConsoleListener() {

			public void save(String text) throws IOException {
				final SaveFilePanel outfilePanel = new SaveFilePanel(
						"org.orbisgis.core.ui.views.sqlConsoleOutFile",
						"Save script");
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
						"org.orbisgis.plugins.core.ui.views.sqlConsoleInFile",
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
				BackgroundManager bm = (BackgroundManager) Services
						.getService(BackgroundManager.class);
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
									.equals(OrbisGISPersitenceConfig.GeocognitionFunctionFactory_ID))) {
								Function f = FunctionManager
										.getFunction(elems[0].getId());
								if (f != null) {
									return f.getSqlOrder();
								}
							} else if ((elems[0].getTypeId()
									.equals(OrbisGISPersitenceConfig.GeocognitionCustomQueryFactory_id))) {
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

		CodeReformator codeReformator = new CodeReformator(";", COMMENT_SPECS);

		//panel.setText("-- SELECT * FROM myTable;");
		JTextComponent txt = panel.getScriptPanel().getTextComponent();
		txt.addKeyListener(new SQLConsoleKeyListener(panel, codeReformator));

		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.SQLCONSOLE, true,
				OrbisGISIcon.SQLCONSOLE_ICON, null, panel, context);
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
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

			DataManager dataManager = (DataManager) Services
					.getService(DataManager.class);
			DataSourceFactory dsf = dataManager.getDataSourceFactory();
			SQLProcessor sqlProcessor = new SQLProcessor(dsf);
			String[] instructions = new String[0];

			long t1 = System.currentTimeMillis();
			try {
				logger.debug("Preparing script: " + script);
				try {
					instructions = sqlProcessor.getScriptInstructions(script);
				} catch (SemanticException e) {
					Services.getErrorManager().error(
							"Semantic error in the script", e);
				} catch (ParseException e) {
					Services.getErrorManager().error("Cannot parse script", e);
					panel.updateCodeError(CodeErrors.getCodeError(e, script));
				} catch (TokenMgrError e) {
					Services.getErrorManager().error("Cannot parse script", e);
				}

				MapContext vc = ((MapContextManager) Services
						.getService(MapContextManager.class))
						.getActiveMapContext();

				for (int i = 0; i < instructions.length; i++) {

					logger.debug("Preparing instruction: " + instructions[i]);
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

							DataSource ds = dsf.getDataSource(instruction,
									DataSourceFactory.DEFAULT, pm);

							if (pm.isCancelled()) {
								break;
							}

							if (spatial && vc != null) {

								try {
									final ILayer layer = dataManager
											.createLayer(ds);

									vc.getLayerModel().insertLayer(layer, 0);

								} catch (LayerException e) {
									Services.getErrorManager().error(
											"Cannot create the layer:"
													+ ds.getName(), e);
									break;
								}
							} else {

								ds.open();
								StringBuilder aux = new StringBuilder();
								int fc = ds.getMetadata().getFieldCount();
								int rc = (int) ds.getRowCount();

								for (int j = 0; j < fc; j++) {
									aux.append(ds.getFieldName(j)).append("\t");
								}
								aux.append("\n");
								for (int row = 0; row < rc; row++) {
									for (int j = 0; j < fc; j++) {
										aux.append(ds.getFieldValue(row, j))
												.append("\t");
									}
									aux.append("\n");
									if (row > 100) {
										aux.append("and more... total " + rc
												+ " rows");
										break;
									}
								}
								ds.close();

								OutputManager om = Services
										.getService(OutputManager.class);
								om.println(aux.toString());
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
				Services.getErrorManager().error("Data access error :", e);
			}

			long t2 = System.currentTimeMillis();
			logger.debug("Execution time: " + ((t2 - t1) / 1000.0));
			panel.setStatusMessage("Execution time: " + ((t2 - t1) / 1000.0));
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isSelected() {
		boolean isSelected = false;
		isSelected = getPlugInContext().viewIsOpen(getId());
		menuItem.setSelected(isSelected);
		return isSelected;
	}

	public String getName() {
		return "SQL view";
	}

}
