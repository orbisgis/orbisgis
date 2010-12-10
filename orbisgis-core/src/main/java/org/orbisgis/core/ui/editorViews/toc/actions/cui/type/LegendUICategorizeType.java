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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.type;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.string.Categorize2String;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.LegendUICategorizePanel;

/**
 *
 * @author maxence
 */
/*
public class LegendUICategorizeType extends LegendUIType {

	Class<? extends Categorize> type;

	public LegendUICategorizeType(String name, LegendUIController controller, Class<? extends Categorize> type) {
		super(name, controller);
		this.type = type;
	}


	@Override
	public LegendUIComponent getUIComponent(LegendUIComponent parent) {
		Categorize c = null;
		DataSource ds = controller.getEditedFeatureTypeStyle().getLayer().getDataSource();

		try {
			if (type.getSimpleName().equals("Categorize2Color")) {
				c = new Categorize2Color(new ColorLiteral(), new ColorLiteral(Color.PINK), new RealAttribute(null, ds));
			} else if (type.getSimpleName().equals("Categorize2Real")) {
				c = new Categorize2Real(new RealLiteral(), new RealLiteral(), new RealAttribute(null, ds));
			} else if (type.getSimpleName().equals("Categorize2String")) {
				c = new Categorize2String(new StringLiteral("Class1"), new StringLiteral("Fallback"), new RealAttribute(null, ds));
			}

			return new LegendUICategorizePanel(name, controller, parent, c);

		} catch (DriverException ex) {
			Logger.getLogger(LegendUICategorizeType.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
}
*/