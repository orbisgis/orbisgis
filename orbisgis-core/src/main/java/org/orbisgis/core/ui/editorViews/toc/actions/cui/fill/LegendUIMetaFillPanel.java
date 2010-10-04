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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.fill;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUISolidFillType;
import javax.swing.Icon;
import org.orbisgis.core.images.OrbisGISIcon;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.fill.DensityFill;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIEmptyPanelType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIEmptyPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIDensityFillType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIType;

/**
 * Meta-Panel for fill edition
 * This panel will provide the ability to select fill type
 *
 * @author maxence
 */
public class LegendUIMetaFillPanel extends LegendUIAbstractMetaPanel {

	private FillNode fNode;

	private LegendUIType initialType;
	private LegendUIComponent initialPanel;
	private LegendUIType[] types;

	public LegendUIMetaFillPanel(LegendUIController controller, LegendUIComponent parent, FillNode fillNode) {
		super("fill", controller, parent, 0);

		this.fNode = fillNode;

		Fill f = fNode.getFill();

		types = new LegendUIType[3];

		types[0] = new LegendUIEmptyPanelType("no fill", controller);
		types[1] = new LegendUISolidFillType(controller);
		types[2] = new LegendUIDensityFillType(controller);


		if (f instanceof SolidFill) {
			initialType = types[1];
			initialPanel = new LegendUISolidFillPanel(controller, this, (SolidFill) f);
		} else if (f instanceof DensityFill) {
			initialType = types[2];
			initialPanel = new LegendUIDensityFillPanel(controller, this, (DensityFill) f);
		} else {
			initialType = types[0];
			initialPanel = new LegendUIEmptyPanel("no fill", controller, this);
		}
	}

	@Override
	public void init(){
		init(types, initialType, initialPanel);
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PALETTE;
	}

	@Override
	protected void switchTo(LegendUIType type, LegendUIComponent comp) {
		Fill f = ((LegendUIFillComponent) comp).getFill();
		//this.fillChanged(f);
		fNode.setFill(f);
	}

	//public abstract void fillChanged(Fill newFill);
}
