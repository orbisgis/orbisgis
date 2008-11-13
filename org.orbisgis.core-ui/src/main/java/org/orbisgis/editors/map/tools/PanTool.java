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
package org.orbisgis.editors.map.tools;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import org.orbisgis.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.editors.map.tool.NoSuchTransitionException;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.editors.map.tools.generated.Pan;
import org.orbisgis.layerModel.MapContext;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Tool to move the map extent
 *
 */
public class PanTool extends Pan {

	private double[] firstPoint;

	/**
	 * @see org.estouro.tools.generated.Pan#transitionTo_Standby()
	 */
	@Override
	public void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.Pan#transitionTo_OnePointLeft()
	 */
	@Override
	public void transitionTo_OnePointLeft(MapContext vc, ToolManager tm)
			throws TransitionException {
		firstPoint = tm.getValues();
	}

	/**
	 * @throws FinishedAutomatonException
	 * @throws NoSuchTransitionException
	 * @see org.estouro.tools.generated.Pan#transitionTo_RectangleDone()
	 */
	@Override
	public void transitionTo_RectangleDone(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		double[] v = tm.getValues();
		double dx = firstPoint[0] - v[0];
		double dy = firstPoint[1] - v[1];

		Envelope extent = tm.getMapTransform().getExtent();
		tm.getMapTransform().setExtent(
				new Envelope(extent.getMinX() + dx, extent.getMaxX() + dx,
						extent.getMinY() + dy, extent.getMaxY() + dy));

		transition("init"); //$NON-NLS-1$
	}

	/**
	 * @see org.estouro.tools.generated.Pan#transitionTo_Cancel()
	 */
	@Override
	public void transitionTo_Cancel(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.Pan#drawIn_Standby(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.Pan#drawIn_OnePointLeft(java.awt.Graphics)
	 */
	@Override
	public void drawIn_OnePointLeft(Graphics g, MapContext vc, ToolManager tm) {
		Point p = tm.getMapTransform().fromMapPoint(
				new Point2D.Double(firstPoint[0], firstPoint[1]));
		int height = tm.getMapTransform().getHeight();
		int width = tm.getMapTransform().getWidth();
		g.clearRect(0, 0, width, height);
		g.drawImage(tm.getMapTransform().getImage(), tm.getLastMouseX() - p.x,
				tm.getLastMouseY() - p.y, null);
	}

	/**
	 * @see org.estouro.tools.generated.Pan#drawIn_RectangleDone(java.awt.Graphics)
	 */
	@Override
	public void drawIn_RectangleDone(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.Pan#drawIn_Cancel(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Cancel(Graphics g, MapContext vc, ToolManager tm) {
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.layerCountGreaterThan(vc, 0);
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

}
