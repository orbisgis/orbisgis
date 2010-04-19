package org.orbisgis.core.ui.plugins.views.beanShellConsole;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.plugins.views.OutputManager;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShellUtils {

	private Interpreter interpreter = new Interpreter();;
	private ByteArrayOutputStream scriptOutput;

	public BeanShellUtils() {
		init();
	}

	private void init() {
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

	}

	public void eval(String text) {
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

}
