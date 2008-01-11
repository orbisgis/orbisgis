/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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