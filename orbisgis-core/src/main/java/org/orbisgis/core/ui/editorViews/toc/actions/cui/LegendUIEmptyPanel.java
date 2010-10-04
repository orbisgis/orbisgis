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

import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.stroke.LegendUIStrokeComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.color.LegendUIColorComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real.LegendUIRealComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.fill.LegendUIFillComponent;
import javax.swing.Icon;
import javax.swing.JLabel;
import org.orbisgis.core.images.OrbisGISIcon;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.string.LegendUIStringComponent;

/**
 *
 * @author maxence
 */
public class LegendUIEmptyPanel extends LegendUIComponent
		implements LegendUIFillComponent, LegendUIColorComponent,
		           LegendUIRealComponent, LegendUIStrokeComponent,
				   LegendUIStringComponent {

	public LegendUIEmptyPanel(String name, LegendUIController controller, LegendUIComponent parent){
		super(name, controller, parent, 0);
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.ERROR;
	}

	@Override
	public Fill getFill() {
		return null;
	}

	@Override
	public ColorParameter getColorParameter() {
		return null;
	}

	@Override
	public RealParameter getRealParameter() {
		return null;
	}

	@Override
	protected void mountComponent() {
		this.add(new JLabel("n/a"));
	}

	@Override
	public Stroke getStroke() {
		return null;
	}

	@Override
	public StringParameter getStringParameter() {
		return null;
	}

}
