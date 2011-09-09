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
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.DrawingException;
import org.orbisgis.core.ui.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.editors.map.tools.generated.Polygon;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public abstract class AbstractPolygonTool extends Polygon implements
		InsertionTool {

	private GeometryFactory gf = new GeometryFactory();

	private ArrayList<Coordinate> points = new ArrayList<Coordinate>();

	@Override
	public void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points.clear();
	}

	@Override
	public void transitionTo_Point(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points.add(newCoordinate(tm.getValues()[0], tm.getValues()[1], vc));
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	@Override
	public void transitionTo_Done(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points = ToolUtilities.removeDuplicated(points);
		if (points.size() < 3) {
			throw new TransitionException(
					I18N
							.getString("orbisgis.core.ui.editors.map.tool.multipolygonTool_0")); //$NON-NLS-1$
		}
		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		double firstX = points.get(0).x;
		double firstY = points.get(0).y;
		tempPoints.add(newCoordinate(firstX, firstY, vc));
		Coordinate[] polygonCoordinates = tempPoints.toArray(new Coordinate[0]);
		com.vividsolutions.jts.geom.Polygon pol = gf.createPolygon(gf
				.createLinearRing(polygonCoordinates), new LinearRing[0]);

		if (!pol.isValid()) {
			throw new TransitionException(
					I18N
							.getString("orbisgis.core.ui.editors.map.tool.polygonTool_1")); //$NON-NLS-1$
		}
		polygonDone(pol, vc, tm);

		transition("init"); //$NON-NLS-1$
	}

	private Coordinate newCoordinate(double x, double y, MapContext mapContext) {
		return new Coordinate(x, y, getInitialZ(mapContext));
	}

	@Override
	public double getInitialZ(MapContext mapContext) {
		return 0;
	}

	protected abstract void polygonDone(com.vividsolutions.jts.geom.Polygon g,
			MapContext vc, ToolManager tm) throws TransitionException;

	@Override
	public void transitionTo_Cancel(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
	}

	@Override
	public void drawIn_Point(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
		Geometry geom = getCurrentPolygon(vc, tm);

		if (geom != null) {
			tm.addGeomToDraw(geom);

			if (!geom.isValid()) {
				throw new DrawingException(
						I18N
								.getString("orbisgis.core.ui.editors.map.tool.polygonTool_1")); //$NON-NLS-1$
			}
		}
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	protected Geometry getCurrentPolygon(MapContext vc, ToolManager tm) {
		Geometry geom;
		if (points.size() >= 2) {
			ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
					.clone();
			Point2D current = tm.getLastRealMousePosition();
			tempPoints.add(newCoordinate(current.getX(), current.getY(), vc));
			tempPoints.add(newCoordinate(tempPoints.get(0).x,
					tempPoints.get(0).y, vc));
			geom = gf.createPolygon(gf.createLinearRing(tempPoints
					.toArray(new Coordinate[0])), new LinearRing[0]);

		} else if (points.size() >= 1) {
			ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
					.clone();
			Point2D current = tm.getLastRealMousePosition();
			tempPoints.add(newCoordinate(current.getX(), current.getY(), vc));
			tempPoints.add(newCoordinate(tempPoints.get(0).x,
					tempPoints.get(0).y, vc));
			geom = gf.createLineString(tempPoints.toArray(new Coordinate[0]));

		} else {
			geom = null;
		}
		return geom;
	}

	@Override
	public void drawIn_Done(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
	}

	@Override
	public void drawIn_Cancel(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
	}

}
