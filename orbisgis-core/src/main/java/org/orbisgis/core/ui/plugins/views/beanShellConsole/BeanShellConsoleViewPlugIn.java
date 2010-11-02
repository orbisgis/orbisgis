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
package org.orbisgis.core.ui.plugins.views.beanShellConsole;

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JMenuItem;
import javax.swing.text.JTextComponent;

import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.OpenFilePanel;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.views.OutputManager;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshCompletionKeyListener;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshConsoleListener;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.ui.BshConsolePanel;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShellConsoleViewPlugIn extends ViewPlugIn {

	private BshConsolePanel panel;
	private Interpreter interpreter = new Interpreter();;
	private ByteArrayOutputStream scriptOutput;
	private JMenuItem menuItem;

	public BeanShellConsoleViewPlugIn() {

	}

	public void initialize(PlugInContext context) throws Exception {

		try {
			interpreter.set("bshEditor", this);

			scriptOutput = new ByteArrayOutputStream();

			PrintStream outStream = new PrintStream(scriptOutput);
			interpreter.setOut(outStream);

			DataManager dm = Services.getService(DataManager.class);

			interpreter.setClassLoader(dm.getDataSourceFactory().getClass()
					.getClassLoader());
			interpreter.set("dsf", dm.getDataSourceFactory());

			interpreter.eval("setAccessibility(true)");

		} catch (EvalError e) {
			Services.getErrorManager().error("Cannot initialize bean shell", e);
		}

		panel = new BshConsolePanel(new BshConsoleListener() {

			public void save(String text) throws IOException {
				final SaveFilePanel outfilePanel = new SaveFilePanel(
						"org.orbisgis.cores.BeanShellConsoleOutFile",
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
						"org.orbisgis.plugins.views.BeanShellConsoleInFile",
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
		panel.setText("print(\"" + "Hello world !\"" + ");");
		JTextComponent txt = panel.getTextComponent();
		txt.addKeyListener(new BshCompletionKeyListener(true, txt));

		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.BEANSHELL, true,
				OrbisGISIcon.BEANSHELL_ICON, null, panel, context);
	}

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	private void eval(String text) {
		OutputManager outputManager = (OutputManager) Services
				.getService(OutputManager.class);
		try {
			MapContext mc = ((MapContextManager) Services
					.getService(MapContextManager.class)).getActiveMapContext();

			interpreter
					.getNameSpace()
					.importCommands(
							"org.orbisgis.core.ui.plugins.views.beanShellConsole.commands");

			if (mc != null) {
				interpreter.set("mc", mc);
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

	private String getOutput() {
		String ret = new String(scriptOutput.toByteArray());
		scriptOutput.reset();
		return ret;
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
		return "BeanShell View";
	}
}
