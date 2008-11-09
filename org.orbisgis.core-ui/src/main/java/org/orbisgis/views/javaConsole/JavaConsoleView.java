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
package org.orbisgis.views.javaConsole;

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
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;

import javax.swing.text.JTextComponent;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.orbisgis.Services;
import org.orbisgis.javaManager.CompilationException;
import org.orbisgis.javaManager.JavaManager;
import org.orbisgis.javaManager.parser.ParseException;
import org.orbisgis.outputManager.OutputManager;
import org.orbisgis.pluginManager.CommonClassLoader;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.view.IView;
import org.orbisgis.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.views.sqlConsole.ui.ConsolePanel;
import org.sif.UIFactory;

public class JavaConsoleView implements IView {

	private ConsolePanel consolePanel;

	public Component getComponent() {
		return consolePanel;
	}

	private void eval(String text) {
		OutputDiagnosticListener outputDiagnosticListener = new OutputDiagnosticListener();
		OutputManager outputManager = (OutputManager) Services
				.getService(OutputManager.class);
		try {
			JavaManager jm = (JavaManager) Services
					.getService(JavaManager.class);
			jm.execute(text, outputDiagnosticListener);
		} catch (ParseException e) {
			Services.getErrorManager().error("Syntax error in java code", e);
		} catch (IllegalArgumentException e) {
			Services.getErrorManager().error(
					"Cannot execute java code. If a java "
							+ "class is specified check "
							+ "it contains a main method", e);
		} catch (InvocationTargetException e) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(bos));
			String stackTrace = new String(bos.toByteArray());
			outputManager.print("Execution error:" + e.getMessage() + "\n"
					+ stackTrace, Color.red);
			outputManager.makeVisible();
		} catch (IOException e) {
			Services.getErrorManager().error("Cannot execute code", e);
		} catch (CompilationException e) {
			outputManager.print(outputDiagnosticListener.getErrorRepport());
			outputManager.makeVisible();
		}
	}

	public void loadStatus() {
	}

	public void saveStatus() {
	}

	public void delete() {

	}

	public void initialize() {
		consolePanel = new ConsolePanel(false, new ConsoleListener() {

			public void save(String text) throws IOException {
				final SaveFilePanel outfilePanel = new SaveFilePanel(
						"org.orbisgis.views.BSHConsoleOutFile", "Save script");
				outfilePanel.addFilter("bsh", "BeanShell script (*.bsh)");

				if (UIFactory.showDialog(outfilePanel)) {
					final BufferedWriter out = new BufferedWriter(
							new FileWriter(outfilePanel.getSelectedFile()));
					out.write(text);
					out.close();
				}
			}

			public String open() throws IOException {
				final OpenFilePanel inFilePanel = new OpenFilePanel(
						"org.orbisgis.views.BSHConsoleInFile", "Open script");
				inFilePanel.addFilter("bsh", "BeanShell script (*.bsh)");

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
		consolePanel.setText("help();");
		JTextComponent txt = consolePanel.getTextComponent();
		txt.addKeyListener(new CompletionKeyListener(true, txt));

		JavaManager javaManager = Services.getService(JavaManager.class);

		ClassLoader classLoader = JavaConsoleView.class.getClassLoader();
		if (classLoader instanceof CommonClassLoader) {
			List<File> files = ((CommonClassLoader) classLoader).getAllFiles();
			javaManager.addFilesToClassPath(files);
		}
	}

	private class OutputDiagnosticListener implements
			DiagnosticListener<JavaFileObject> {
		private String error = "";

		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
			error += "Compile error (near line " + diagnostic.getLineNumber()
					+ "): " + diagnostic.getMessage(Locale.getDefault()) + "\n";
		}

		public String getErrorRepport() {
			return error;
		}

	}
}