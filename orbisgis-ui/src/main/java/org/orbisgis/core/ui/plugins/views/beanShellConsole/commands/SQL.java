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
package org.orbisgis.core.ui.plugins.views.beanShellConsole.commands;

import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.showAttributes.Table;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.TokenMgrError;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.progress.IProgressMonitor;

import bsh.CallStack;
import bsh.Interpreter;

public class SQL {

	private static final Logger logger = Logger.getLogger(SQL.class);

	/**
	 * Implement the command action.
	 */
	public static void invoke(Interpreter env, CallStack callstack, String script) {

		execute(script);
	}

	public static void execute(String text) {
		BackgroundManager bm = (BackgroundManager) Services
				.getService(BackgroundManager.class);
		bm.backgroundOperation(new ExecuteScriptProcess(text));
	}

	private static class ExecuteScriptProcess implements BackgroundJob {

		private String script;

		public ExecuteScriptProcess(String script) {
			this.script = script;
		}

		public String getTaskName() {
			return "Executing script";
		}

		public void run(IProgressMonitor pm) {
			DataSourceFactory dsf = ((DataManager) Services
					.getService(DataManager.class)).getDataSourceFactory();
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

							if (spatial) {

								try {
									final ILayer layer = dataManager
											.createLayer(ds);
									if (vc != null) {
										vc.getLayerModel()
												.insertLayer(layer, 0);
									}
								} catch (LayerException e) {
									Services.getErrorManager().error(
											"Impossible to create the layer:"
													+ ds.getName(), e);
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
