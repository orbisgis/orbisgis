/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */

/*
package org.orbisgis.view.toc.actions.cui.parameter.real;

import javax.swing.Icon;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterFactory;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.components.TextInput;
import org.orbisgis.view.icons.OrbisGISIcon;
*/
/**
 *
 * @author Maxence Laurent
 */

/*
 public abstract class LegendUIAlgebricalPanel extends LegendUIComponent implements LegendUIRealComponent {


	TextInput input;
	RealParameter r;

	public LegendUIAlgebricalPanel(String name, final LegendUIController controller, LegendUIComponent parent, RealParameter realParameter) {
		super(name, controller, parent, 0, true);
		this.r = realParameter;

		System.out.println ("RealParameter = " + this.r);

		input = new TextInput("alg.", realParameter.toString(), 40, true) {

			@Override
			protected void valueChanged(String s) {
				r = RealParameterFactory.createFromString(s, controller.getEditedFeatureTypeStyle().getLayer().getSpatialDataSource());
				if (r != null){
					realChanged(r);
				}
			}
		};
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.getIcon("palette");
	}

	@Override
	protected void mountComponent() {
		editor.add(input);
	}

	@Override
	public RealParameter getRealParameter() {
		return r;
	}


	public abstract void realChanged(RealParameter newReal);

}
*/