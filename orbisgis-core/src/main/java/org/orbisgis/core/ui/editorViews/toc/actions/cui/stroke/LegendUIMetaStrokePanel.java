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

import javax.swing.Icon;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.stroke.GraphicStroke;

/**
 *
 * @author maxence
 */
public class LegendUIMetaStrokePanel extends LegendUIAbstractMetaPanel {

	private StrokeNode sNode;
	private LegendUIComponent comp;

	private final Class[] classes = {PenStroke.class, GraphicStroke.class};

	public LegendUIMetaStrokePanel(LegendUIController controller, LegendUIComponent parent, StrokeNode strokeNode, boolean isNullable) {
		super("stroke", controller, parent, 0, isNullable);

		this.sNode = strokeNode;

		comp = null;
		if (sNode.getStroke() != null) {
			comp = getCompForClass(sNode.getStroke().getClass());
		}
	}

	@Override
	protected final LegendUIComponent getCompForClass(Class newClass) {
		if (newClass == PenStroke.class) {
			PenStroke pStroke;
			if (sNode.getStroke() instanceof PenStroke) {
				pStroke = (PenStroke) sNode.getStroke();
			} else {
				pStroke = new PenStroke();
			}

			return new LegendUIPenStrokePanel(controller, parent, pStroke, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};
		} else if (newClass == GraphicStroke.class){
			GraphicStroke gStroke;

			if (sNode.getStroke() instanceof GraphicStroke){
				gStroke = (GraphicStroke) sNode.getStroke();
			} else {
				gStroke = new GraphicStroke();
			}

			return new LegendUIGraphicStrokePanel(controller, parent, gStroke, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Not supported yet.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			};

		} else {
			return null;
		}
	}

	@Override
	public void init() {
		init(classes, comp);
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PALETTE;
	}

	@Override
	protected void switchTo(LegendUIComponent comp) {
		if (comp != null) {
			Stroke s = ((LegendUIStrokeComponent) comp).getStroke();
			sNode.setStroke(s);
		} else {
			sNode.setStroke(null);
		}
	}

	@Override
	public Class getEditedClass() {
		return sNode.getStroke().getClass();
	}
}
