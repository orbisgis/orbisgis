/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.core.ui.editors.map.tools;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.AbstractButton;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.core.ui.editors.map.tool.NoSuchTransitionException;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Tool to move the map extent
 * 
 */
public class PanTool extends AbstractDragTool {

	AbstractButton button;

        @Override
	public AbstractButton getButton() {
		return button;
	}

        @Override
	public void setButton(AbstractButton button) {
		this.button = button;
	}

	@Override
	public void transitionTo_MouseReleased(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
                // get the current point
		double[] v = tm.getValues();

                // get the start point (of the dragging move)
                double[] firstPoint = getFirstPoint();

                // diff
		double dx = firstPoint[0] - v[0];
		double dy = firstPoint[1] - v[1];

                // move the envelope
		Envelope extent = tm.getMapTransform().getExtent();
		tm.getMapTransform().setExtent(
				new Envelope(extent.getMinX() + dx, extent.getMaxX() + dx,
						extent.getMinY() + dy, extent.getMaxY() + dy));

                // we're done, this will get us back to StandBy
		transition("finished"); //$NON-NLS-1$
	}

	@Override
	public void drawIn_MouseDown(Graphics g, MapContext vc, ToolManager tm) {
                // this is what is displayed when dragging

                // current point position
                double[] firstPoint = getFirstPoint();

                // get the corresponding point in the map
		Point p = tm.getMapTransform().fromMapPoint(
				new Point2D.Double(firstPoint[0], firstPoint[1]));
		int height = tm.getMapTransform().getHeight();
		int width = tm.getMapTransform().getWidth();

                // draw the new map...
		g.clearRect(0, 0, width, height);
		g.drawImage(tm.getMapTransform().getImage(), tm.getLastMouseX() - p.x,
				tm.getLastMouseY() - p.y, null);
	}

        @Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.layerCountGreaterThan(vc, 0);
	}

        @Override
	public String getName() {
		return I18N.getString("orbisgis.core.ui.editors.map.tool.pan_tooltip");
	}
}
