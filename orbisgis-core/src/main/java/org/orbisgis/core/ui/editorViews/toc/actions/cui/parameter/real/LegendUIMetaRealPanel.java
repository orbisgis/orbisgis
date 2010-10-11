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



package org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIPropertyNameType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUICategorizeType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIRealLiteralType;
import javax.swing.Icon;
import org.orbisgis.core.images.OrbisGISIcon;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIEmptyPanelType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIEmptyPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.LegendUICategorizePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.LegendUIPropertyNamePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.LegendUIRecodePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIAlgebricalType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIRecodeType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIType;

/**
 *
 * @author maxence
 */
public abstract class LegendUIMetaRealPanel extends LegendUIAbstractMetaPanel {

	private RealParameter real;

	private LegendUIType initialType;
	private LegendUIComponent initialPanel;
	private LegendUIType[] types;

	public LegendUIMetaRealPanel(String name, LegendUIController controller, LegendUIComponent parent, RealParameter r) {
		super(name, controller, parent, 0);

		this.real = r;

		types = new LegendUIType[6];

		types[0] = new LegendUIEmptyPanelType("no " + name, controller);
		types[1] = new LegendUIRealLiteralType("Constant " + name, controller);
		types[2] = new LegendUIPropertyNameType("Attribute " + name, controller, RealAttribute.class);
		types[3] = new LegendUICategorizeType("Categorized " + name, controller, Categorize2Real.class);
		types[4] = new LegendUIRecodeType("UniqueValue map " + name, controller, Recode2Real.class);
		types[5] = new LegendUIAlgebricalType("Alg. " + name, controller);


		if (this.real == null){
			initialType = types[0];
			initialPanel = new LegendUIEmptyPanel("no " + name, controller, this);
 		} else if (this.real instanceof RealLiteral) {
			initialType = types[1];
			initialPanel = new LegendUIRealLiteralPanel("Constant " + name, controller, this, (RealLiteral) real);
		} else if (this.real instanceof RealAttribute) {
			initialType = types[2];
			initialPanel = new LegendUIPropertyNamePanel("Attribute " + name, controller, this, (RealAttribute) real);
		} else if (this.real instanceof Categorize2Real) {
			initialType = types[3];
			initialPanel = new LegendUICategorizePanel("Categorized " + name, controller, this, (Categorize) this.real);
		} else if (this.real instanceof Recode2Real) {
			initialType = types[4];
			initialPanel = new LegendUIRecodePanel("UniqueValue map " + name, controller, this, (Recode) this.real);
		} else {
			initialType = types[5];
			initialPanel = new LegendUIAlgebricalPanel("Alg. " + name, controller, this, this.real){

				@Override
				public void realChanged(RealParameter newReal) {
					switchTo(initialType, initialPanel);
				}
			};
		}

	}

	@Override
	public void init(){
		init(types, initialType, initialPanel);
	}

	/**
	 * use with caution !
	 * @param type
	 * @param comp
	 */
	@Override
	public void switchTo(LegendUIType type, LegendUIComponent comp) {
		this.real = ((LegendUIRealComponent)comp).getRealParameter();
		this.realChanged(real);
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PALETTE;
	}

	public abstract void realChanged(RealParameter newReal);

}
