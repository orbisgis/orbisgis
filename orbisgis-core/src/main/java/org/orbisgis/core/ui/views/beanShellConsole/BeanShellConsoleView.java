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
package org.orbisgis.core.ui.views.beanShellConsole;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.javaManager.JavaManager;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.outputManager.OutputManager;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.view.IView;
import org.orbisgis.core.ui.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.core.ui.views.sqlConsole.ui.ConsolePanel;
import org.orbisgis.pluginManager.CommonClassLoader;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.sif.UIFactory;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShellConsoleView implements IView {

	private ConsolePanel consolePanel;
	private Interpreter interpreter;
	private ByteArrayOutputStream scriptOutput;

	public Component getComponent() {
		return consolePanel;
	}

	private void eval(String text) {
		OutputManager outputManager = (OutputManager) Services
				.getService(OutputManager.class);
		try {
			MapContext mc = ((MapContextManager) Services
					.getService(MapContextManager.class)).getActiveMapContext();

			if (mc != null) {
				interpreter.set("mc", mc);
			} else {
				interpreter.set("mc", null);
			}

			interpreter.eval(text);
			String out = getOutput();
			if (out.length() > 0) {
				outputManager.println("--------BeanShell result ------------",
						Color.GREEN);

				outputManager.println(out, Color.blue);
				outputManager.println("--------------------", Color.GREEN);
			}

		} catch (IllegalArgumentException e) {
			Services.getErrorManager().error(
					"Cannot execute the beanshel script.", e);
		} catch (EvalError e) {
			outputManager.println("--------BeanShell error ------------",
					Color.RED);
			outputManager.println(e.getErrorText(), Color.RED);
			outputManager.println("--------------------", Color.RED);
		}
	}

	public void loadStatus() {
	}

	public void saveStatus() {
	}

	public void delete() {

	}

	public void initialize() {

		interpreter = new Interpreter();
		try {
			interpreter.set("bshEditor", this);

			scriptOutput = new ByteArrayOutputStream();

			PrintStream outStream = new PrintStream(scriptOutput);
			interpreter.setOut(outStream);

			DataManager dm = Services.getService(DataManager.class);

			interpreter.setClassLoader(dm.getDSF().getClass().getClassLoader());
			interpreter.set("dsf", dm.getDSF());

			interpreter.eval("setAccessibility(true)");

		} catch (EvalError e) {
			Services.getErrorManager().error("Cannot initialize bean shell", e);
		}

		consolePanel = new ConsolePanel(false, new ConsoleListener() {

			public void save(String text) throws IOException {
				final SaveFilePanel outfilePanel = new SaveFilePanel(
						"org.orbisgis.core.ui.views.BeanShellConsoleOutFile",
						"Save script");
				outfilePanel.addFilter("bsh", "BeanShell Script (*.bsh)");

				if (UIFactory.showDialog(outfilePanel)) {
					final BufferedWriter out = new BufferedWriter(
							new FileWriter(outfilePanel.getSelectedFile()));
					out.write(text);
					out.close();
				}
			}

			public String open() throws IOException {
				final OpenFilePanel inFilePanel = new OpenFilePanel(
						"org.orbisgis.views.BeanShellConsoleInFile",
						"Open script");
				inFilePanel.addFilter("bsh", "BeanShell Script (*.bsh)");

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

			@Override
			public void change() {
			}

			@Override
			public boolean showControlButtons() {
				return true;
			}

			@Override
			public String doDrop(Transferable t) {
				return null;
			}

		});
		consolePanel.setText("print(\"" + "Hello world !\"" + ");");
		JTextComponent txt = consolePanel.getTextComponent();
		txt.addKeyListener(new CompletionKeyListener(true, txt, interpreter));

		JavaManager javaManager = Services.getService(JavaManager.class);
		ClassLoader classLoader = BeanShellConsoleView.class.getClassLoader();
		if (classLoader instanceof CommonClassLoader) {
			List<File> files = ((CommonClassLoader) classLoader).getAllFiles();
			javaManager.addFilesToClassPath(files);
		}
	}

	private String getOutput() {
		String ret = new String(scriptOutput.toByteArray());
		scriptOutput.reset();
		return ret;
	}

}