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

package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
class EditSolidFillPanel extends JPanel implements AbstractEditFillPanel {

	private SolidFill fill;
	private MetaRealParameterPanel opacity;

	private MetaColorParameterPanel colorPanel;

	public EditSolidFillPanel(SolidFill f){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// First, be sure fill is not null
		this.fill = f;

		colorPanel = new MetaColorParameterPanel(this.fill.getColor()) {
			@Override
			public void colorChanged(ColorParameter newColor) {
				fill.setColor(newColor);
			}
		};
		this.add(colorPanel);

		opacity = new MetaRealParameterPanel(fill.getOpacity()) {
			@Override
			public void realChanged(RealParameter newReal) {
				fill.setOpacity(newReal);
			}
		};

		this.add(opacity);

	}

	@Override
	public Fill getFill(){
		return fill;
	}
}
