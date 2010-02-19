package org.orbisgis.plugins.core.ui.views;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;

import javax.swing.JMenuItem;

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
import org.orbisgis.plugins.core.DataManager;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.background.BackgroundJob;
import org.orbisgis.plugins.core.background.BackgroundManager;
import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.plugins.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.LayerException;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.editors.map.MapContextManager;
import org.orbisgis.plugins.core.ui.geocognition.TransferableGeocognitionElement;
import org.orbisgis.plugins.core.ui.views.sqlConsole.ConsoleListener;
import org.orbisgis.plugins.core.ui.views.sqlConsole.ConsolePanel;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.sif.OpenFilePanel;
import org.orbisgis.plugins.sif.SaveFilePanel;
import org.orbisgis.plugins.sif.UIFactory;
import org.orbisgis.progress.IProgressMonitor;

public class SQLConsoleViewPlugIn extends ViewPlugIn {

	private ConsolePanel panel;
	private static final Logger logger = Logger
			.getLogger(SQLConsoleViewPlugIn.class);
	private final String EOL = System.getProperty("line.separator");
	private JMenuItem menuItem;

	public void initialize(PlugInContext context) throws Exception {
		panel = new ConsolePanel(true, new ConsoleListener() {

			public void save(String text) throws IOException {
				final SaveFilePanel outfilePanel = new SaveFilePanel(
						"org.orbisgis.plugins.core.ui.views.sqlConsoleOutFile",
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
									.equals(GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID))) {
								Function f = FunctionManager
										.getFunction(elems[0].getId());
								if (f != null) {
									return f.getSqlOrder();
								}
							} else if ((elems[0].getTypeId()
									.equals(GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID))) {
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
		// setComponent(panel,"Memory", getIcon("utilities-system-monitor.png"),
		// context);

		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.SQLCONSOLE, true,
				getIcon(Names.SQLCONSOLE_ICON), null, panel, null, null,
				context.getWorkbenchContext());
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
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
			DataSourceFactory dsf = ((DataManager) Services
					.getService(DataManager.class)).getDSF();
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
				} catch (TokenMgrError e) {
					Services.getErrorManager().error("Cannot parse script", e);
				}

				MapContext vc = ((MapContextManager) Services
						.getService(MapContextManager.class))
						.getActiveMapContext();

				DataManager dataManager = (DataManager) Services
						.getService(DataManager.class);
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
											"Impossible to create the layer:"
													+ ds.getName(), e);
									break;
								}
							} else {

								/*
								 * This code is from OrbisGIS fork GearScape.
								 * http://forge.osor.eu/projects/gearscape/date
								 * : 24/09/2009
								 */

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
				Services.getErrorManager().error("Data access error:", e);
			}

			long t2 = System.currentTimeMillis();
			logger.debug("Execution time: " + ((t2 - t1) / 1000.0));
		}
	}

	public void update(Observable o, Object arg) {
		setSelected();
	}

	public void setSelected() {
		menuItem.setSelected(isVisible());
	}

	public boolean isVisible() {
		return getUpdateFactory().viewIsOpen(getId());
	}

}
