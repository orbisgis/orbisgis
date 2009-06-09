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
package org.orbisgis.core.ui.editors.map.tools;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.editors.map.tools.generated.ZoomIn;

public abstract class AbstractRectangleTool extends ZoomIn {

	protected double[] firstPoint;

	/**
	 * @see org.estouro.tools.generated.ZoomIn#transitionTo_RectangleDone()
	 */
	@Override
	public void transitionTo_RectangleDone(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		double[] v = tm.getValues();

		double minx = Math.min(firstPoint[0], v[0]);
		double miny = Math.min(firstPoint[1], v[1]);

		Rectangle2D rect = new Rectangle2D.Double(minx, miny, Math.abs(v[0]
				- firstPoint[0]), Math.abs(v[1] - firstPoint[1]));

		double tolerance = tm.getTolerance();
		double maxx = rect.getMaxX();
		double maxy = rect.getMaxY();
		if ((maxx - minx < tolerance) && maxy - miny < tolerance) {
			double centerX = rect.getCenterX();
			double centerY = rect.getCenterY();
			rect = buildRectangleOnPoint(tm, centerX, centerY);
			rectangleDone(rect, true, vc, tm);
		} else {
			rectangleDone(rect, false, vc, tm);
		}

		transition("init"); //$NON-NLS-1$
	}

	protected Rectangle2D buildRectangleOnPoint(ToolManager tm, double x,
			double y) {
		double minx;
		double miny;
		double maxx;
		double maxy;
		double tolerance = tm.getTolerance();
		minx = x - tolerance;
		miny = y - tolerance;
		maxx = x + tolerance;
		maxy = y + tolerance;
		return new Rectangle2D.Double(minx, miny, maxx - minx, maxy - miny);
	}

	protected abstract void rectangleDone(Rectangle2D rect,
			boolean smallerThanTolerance, MapContext vc, ToolManager tm)
			throws TransitionException;

	/**
	 * @see org.estouro.tools.generated.Rectangle#transitionTo_Standby()
	 */
	@Override
	public void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#transitionTo_OnePointLeft()
	 */
	@Override
	public void transitionTo_OnePointLeft(MapContext vc, ToolManager tm)
			throws TransitionException {
		firstPoint = tm.getValues();
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#transitionTo_Cancel()
	 */
	@Override
	public void transitionTo_Cancel(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#drawIn_Standby(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#drawIn_OnePointLeft(java.awt.Graphics)
	 */
	@Override
	public void drawIn_OnePointLeft(Graphics g, MapContext vc, ToolManager tm) {
		Point p = tm.getMapTransform().fromMapPoint(
				new Point2D.Double(firstPoint[0], firstPoint[1]));
		int minx = Math.min(p.x, tm.getLastMouseX());
		int miny = Math.min(p.y, tm.getLastMouseY());
		int width = Math.abs(p.x - tm.getLastMouseX());
		int height = Math.abs(p.y - tm.getLastMouseY());
		g.drawRect(minx, miny, width, height);
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#drawIn_RectangleDone(java.awt.Graphics)
	 */
	@Override
	public void drawIn_RectangleDone(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#drawIn_Cancel(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Cancel(Graphics g, MapContext vc, ToolManager tm) {
	}

	@Override
	public Point getHotSpotOffset() {
		return new Point(0, 0);
	}

}
