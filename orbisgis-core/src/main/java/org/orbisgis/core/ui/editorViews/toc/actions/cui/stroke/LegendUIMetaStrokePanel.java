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



package org.orbisgis.core.ui.editorViews.toc.actions.cui.stroke;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIPenStrokeType;
import javax.swing.Icon;
import org.orbisgis.core.images.OrbisGISIcon;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIEmptyPanelType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIEmptyPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.type.LegendUIType;
import org.orbisgis.core.renderer.se.StrokeNode;

/**
 *
 * @author maxence
 */
public class LegendUIMetaStrokePanel extends LegendUIAbstractMetaPanel {

	private StrokeNode sNode;

	private LegendUIType initialType;
	private LegendUIComponent initialPanel;
	private LegendUIType[] types;

	public LegendUIMetaStrokePanel(LegendUIController controller, LegendUIComponent parent, StrokeNode strokeNode) {
		super("stroke", controller, parent, 0);

		this.sNode = strokeNode;

		Stroke s = sNode.getStroke();

		types = new LegendUIType[2];

		types[0] = new LegendUIEmptyPanelType("no stroke", controller);
		types[1] = new LegendUIPenStrokeType(controller);


		if (s instanceof PenStroke) {
			initialType = types[1];
			initialPanel = new LegendUIPenStrokePanel(controller, this, (PenStroke) s);
		} else {
			initialType = types[0];
			initialPanel = new LegendUIEmptyPanel("no stroke", controller, this);
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
		Stroke s = ((LegendUIStrokeComponent) comp).getStroke();
		sNode.setStroke(s);
	}

	//public abstract void fillChanged(Fill newFill);
}
