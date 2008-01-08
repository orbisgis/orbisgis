package org.orbisgis.geoview.views.beanshell;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.renderer.style.BasicStyle;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;

public class BeanshellPanel extends JPanel {
	private static final String EOL = System.getProperty("line.separator");

	public BeanshellPanel(final GeoView2D geoview) {
		super(new BorderLayout());

		try {
			final JConsole console = new JConsole();
			console.print("Datasource factory refere to as dsf" + EOL);
			add(console, BorderLayout.CENTER);

			final Interpreter interpreter = new Interpreter(console);
			interpreter.setClassLoader(OrbisgisCore.getDSF().getClass()
					.getClassLoader());
			interpreter.set("dsf", OrbisgisCore.getDSF());

			interpreter.setClassLoader(geoview.getViewContext().getClass()
					.getClassLoader());
			interpreter.set("gc", geoview.getViewContext());
			console.print("GeoView context is available as gc" + EOL);

			interpreter.set("style", new BasicStyle());

			interpreter.eval("setAccessibility(true)");

			new Thread(interpreter).start();
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}