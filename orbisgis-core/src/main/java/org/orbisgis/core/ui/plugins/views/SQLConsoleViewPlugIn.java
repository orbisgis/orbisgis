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
package org.orbisgis.core.ui.plugins.views;

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
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.core.OrbisGISPersitenceConfig;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.sif.OpenFilePanel;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.geocognition.TransferableGeocognitionElement;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.views.sqlConsole.ConsoleListener;
import org.orbisgis.core.ui.plugins.views.sqlConsole.ConsolePanel;
import org.orbisgis.core.ui.plugins.views.sqlConsole.ExecuteScriptProcess;
import org.orbisgis.core.ui.plugins.views.sqlConsole.SQLConsoleKeyListener;

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
		JTextComponent txt = panel.getTextComponent();
		txt.addKeyListener(new SQLConsoleKeyListener(txt));

		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.SQLCONSOLE, true,
				getIcon(IconNames.SQLCONSOLE_ICON), null, panel, context);
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	public void update(Observable o, Object arg) {
		menuItem.setSelected(getPlugInContext().viewIsOpen(getId()));
	}

	public String getName() {
		return "SQL view";
	}

}
