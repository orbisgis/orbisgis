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
package org.orbisgis.geoview.views.beanshellConsole;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;
import org.orbisgis.geoview.views.outputView.OutputPanel;
import org.orbisgis.geoview.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.geoview.views.sqlConsole.ui.ConsolePanel;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.sif.UIFactory;

import bsh.EvalError;
import bsh.Interpreter;

public class BSHConsoleView implements IView {
	private Interpreter interpreter;
	private GeoView2D geoview;
	private ByteArrayOutputStream scriptOutput;

	public Component getComponent(GeoView2D geoview) {
		return new ConsolePanel(false,
				new ConsoleListener() {

					public void save(String text) throws IOException {
						final SaveFilePanel outfilePanel = new SaveFilePanel(
								"org.orbisgis.geoview.BSHConsoleOutFile",
								"Save script");
						outfilePanel.addFilter("bsh",
								"BeanShell script (*.bsh)");

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
								"org.orbisgis.geoview.BSHConsoleInFile",
								"Open script");
						inFilePanel
								.addFilter("bsh", "BeanShell script (*.bsh)");

						if (UIFactory.showDialog(inFilePanel)) {
							File selectedFile = inFilePanel.getSelectedFile();
							long fileLength = selectedFile.length();
							if (fileLength > 1048576) {
								throw new IOException("Script files of more "
										+ "than 1048576 bytes can't be read !!");
							} else {
								FileReader fr = new FileReader(selectedFile);
								char[] buff = new char[(int) fileLength];
								fr.read(buff, 0, (int) fileLength);
								String string = new String(buff);
								fr.close();
								return string;
							}
						} else {
							return null;
						}
					}

					public void execute(String text) {
						if (text.trim().length() > 0) {
							eval(text);
						} else {

						}
					}

				});
	}

	private String getOutput() {
		String ret = new String(scriptOutput.toByteArray());
		scriptOutput.reset();
		return ret;
	}

	private void eval(String text) {
		try {
			interpreter.eval(text);

			String out = getOutput();
			if (out.length() > 0) {
				Component comp = geoview
						.getView("org.orbisgis.geoview.OutputView");
				if (comp != null) {
					OutputPanel outputPanel = (OutputPanel) comp;
					outputPanel.add(out);
				} else {
					PluginManager
							.error("Script successfully executed but cannot find "
									+ "the output view to show the result");
				}
			}
		} catch (EvalError e) {
			PluginManager.error("Error executing beanshell script", e);
		}
	}

	public void loadStatus(InputStream ois) {
	}

	public void saveStatus(OutputStream oos) {
	}

	public void delete() {

	}

	public void initialize(GeoView2D geoView2D) {
		this.geoview = geoView2D;
		interpreter = new Interpreter();
		try {
			interpreter.set("bshEditor", this);

			scriptOutput = new ByteArrayOutputStream();

			PrintStream outStream = new PrintStream(scriptOutput);
			interpreter.setOut(outStream);
			interpreter.setErr(outStream);

			interpreter.setClassLoader(OrbisgisCore.getDSF().getClass()
					.getClassLoader());
			interpreter.set("dsf", OrbisgisCore.getDSF());

			interpreter.setClassLoader(geoview.getViewContext().getClass()
					.getClassLoader());
			interpreter.set("gc", geoview.getViewContext());

			interpreter.eval("setAccessibility(true)");

		} catch (EvalError e) {
			PluginManager.error("Cannot initialize bean shell", e);
		}
	}
}