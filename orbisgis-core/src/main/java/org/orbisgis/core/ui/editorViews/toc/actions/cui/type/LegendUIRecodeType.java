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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.LegendUIRecodePanel;

/**
 *
 * @author maxence
 */
/*
public class LegendUIRecodeType extends LegendUIType {

	Class<? extends Recode> type;

	public LegendUIRecodeType(String name, LegendUIController controller, Class<? extends Recode> type) {
		super(name, controller);
		this.type = type;
	}

	@Override
	public LegendUIComponent getUIComponent(LegendUIComponent parent) {
		Recode r = null;
		DataSource ds = controller.getEditedFeatureTypeStyle().getLayer().getDataSource();

		try {
			if (type.getSimpleName().equals("Recode2Color")) {
				r = new Recode2Color(new ColorLiteral(), new StringAttribute(null, ds));
			} else if (type.getSimpleName().equals("Recode2Real")) {
				r = new Recode2Real(new RealLiteral(-1), new StringAttribute(null, ds));
			} else if (type.getSimpleName().equals("Recode2String")) {
				r = new Recode2String(new StringLiteral("n/a"), new StringAttribute(null, ds));
			}

			return new LegendUIRecodePanel(name, controller, parent, r);

		} catch (DriverException ex) {
			Logger.getLogger(LegendUICategorizeType.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
}
*/
