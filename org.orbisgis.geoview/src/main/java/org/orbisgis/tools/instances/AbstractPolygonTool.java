/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.generated.Polygon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public abstract class AbstractPolygonTool extends Polygon {

	private GeometryFactory gf = new GeometryFactory();

	private ArrayList<Coordinate> points = new ArrayList<Coordinate>();

	@Override
	public void transitionTo_Standby(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points.clear();
	}

	@Override
	public void transitionTo_Point(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points.add(new Coordinate(tm.getValues()[0], tm.getValues()[1]));
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	@Override
	public void transitionTo_Done(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		if (points.size() < 3)
			throw new TransitionException(Messages
					.getString("MultipolygonTool.0")); //$NON-NLS-1$
		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		tempPoints.add(new Coordinate(points.get(0).x, points.get(0).y));
		com.vividsolutions.jts.geom.Polygon pol = gf.createPolygon(gf
				.createLinearRing(tempPoints.toArray(new Coordinate[0])),
				new LinearRing[0]);

		if (!pol.isValid()) {
			throw new TransitionException(Messages.getString("PolygonTool.1")); //$NON-NLS-1$
		}
		polygonDone(pol, vc, tm);

		transition("init"); //$NON-NLS-1$
	}

	protected abstract void polygonDone(com.vividsolutions.jts.geom.Polygon g,
			ViewContext vc, ToolManager tm) throws TransitionException;

	@Override
	public void transitionTo_Cancel(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void drawIn_Standby(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	@Override
	public void drawIn_Point(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
		if (points.size() >= 2) {
			ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
					.clone();
			Point2D current = tm.getLastRealMousePosition();
			tempPoints.add(new Coordinate(current.getX(), current.getY()));
			tempPoints.add(new Coordinate(tempPoints.get(0).x, tempPoints
					.get(0).y));
			com.vividsolutions.jts.geom.Polygon pol = gf.createPolygon(gf
					.createLinearRing(tempPoints.toArray(new Coordinate[0])),
					new LinearRing[0]);

			tm.addGeomToDraw(pol);

			if (!pol.isValid()) {
				throw new DrawingException(Messages.getString("PolygonTool.1")); //$NON-NLS-1$
			}

		} else if (points.size() >= 1) {
			ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
					.clone();
			Point2D current = tm.getLastRealMousePosition();
			tempPoints.add(new Coordinate(current.getX(), current.getY()));
			tempPoints.add(new Coordinate(tempPoints.get(0).x, tempPoints
					.get(0).y));
			com.vividsolutions.jts.geom.LineString line = gf
					.createLineString(tempPoints.toArray(new Coordinate[0]));

			tm.addGeomToDraw(line);

			if (!line.isValid()) {
				throw new DrawingException(Messages.getString("PolygonTool.1")); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void drawIn_Done(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
	}

	@Override
	public void drawIn_Cancel(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
	}

	public URL getMouseCursor() {
		return null;
	}

}
