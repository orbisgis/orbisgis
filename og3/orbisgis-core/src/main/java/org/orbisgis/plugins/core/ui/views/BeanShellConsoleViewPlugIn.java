package org.orbisgis.plugins.core.ui.views;

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Observable;

import javax.swing.JMenuItem;
import javax.swing.text.JTextComponent;

import org.orbisgis.plugins.core.DataManager;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.editors.map.MapContextManager;
import org.orbisgis.plugins.core.ui.views.beanShellConsole.CompletionKeyListener;
import org.orbisgis.plugins.core.ui.views.sqlConsole.ConsoleListener;
import org.orbisgis.plugins.core.ui.views.sqlConsole.ConsolePanel;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.sif.OpenFilePanel;
import org.orbisgis.plugins.sif.SaveFilePanel;
import org.orbisgis.plugins.sif.UIFactory;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShellConsoleViewPlugIn extends ViewPlugIn {

	private ConsolePanel panel;
	private Interpreter interpreter;
	private ByteArrayOutputStream scriptOutput;
	private JMenuItem menuItem;

	public BeanShellConsoleViewPlugIn() {

	}

	public void initialize(PlugInContext context) throws Exception {
		Interpreter interpreter = new Interpreter();
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

		panel = new ConsolePanel(false, new ConsoleListener() {

			public void save(String text) throws IOException {
				final SaveFilePanel outfilePanel = new SaveFilePanel(
						"org.orbisgis.plugins.core.ui.views.BeanShellConsoleOutFile",
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
		txt.addKeyListener(new CompletionKeyListener(true, txt, interpreter));
		// TODO (pyf): why I comment this?
		/*
		 * JavaManager javaManager = Services.getService(JavaManager.class);
		 * //TODO : class loaded from old class (before plugins model)
		 * ClassLoader classLoader =
		 * BeanShellConsoleViewPlugIn.class.getClassLoader(); if (classLoader
		 * instanceof CommonClassLoader) { List<File> files =
		 * ((CommonClassLoader) classLoader).getAllFiles();
		 * javaManager.addFilesToClassPath(files); }
		 */
		// setComponent(panel,"Memory", getIcon("utilities-system-monitor.png"),
		// context);
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.BEANSHELL, true,
				getIcon(Names.BEANSHELL_ICON), null, panel, null, null,
				context.getWorkbenchContext());
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
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
							"org.orbisgis.plugins.core.ui.views.beanShellConsole.commands");

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

	private String getOutput() {
		String ret = new String(scriptOutput.toByteArray());
		scriptOutput.reset();
		return ret;
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
