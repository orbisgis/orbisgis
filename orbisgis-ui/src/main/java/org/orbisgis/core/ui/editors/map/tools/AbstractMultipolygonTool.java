/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
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
import org.orbisgis.core.ui.editors.map.tools.generated.Multipolygon;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public abstract class AbstractMultipolygonTool extends Multipolygon implements
		InsertionTool {

	private GeometryFactory gf = new GeometryFactory();
	private ArrayList<Coordinate> points = new ArrayList<Coordinate>();
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();

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

	@Override
	public void transitionTo_Line(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points = ToolUtilities.removeDuplicated(points);
		if (points.size() < 3)
			throw new TransitionException(
					I18N
							.getString("orbisgis.core.ui.editors.map.tool.multipolygonTool_0")); //$NON-NLS-1$

		addPolygon(vc);

		transition("init"); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	private void addPolygon(MapContext mapContext) throws TransitionException {
		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		tempPoints.add(newCoordinate(points.get(0).x, points.get(0).y,
				mapContext));
		Coordinate[] coords = tempPoints.toArray(new Coordinate[0]);
		Polygon p = gf.createPolygon(gf.createLinearRing(coords),
				new LinearRing[0]);
		if (!p.isValid()) {
			throw new TransitionException(
					I18N
							.getString("orbisgis.core.ui.editors.map.tool.multipolygonTool_2")); //$NON-NLS-1$
		}
		polygons.add(p);
	}

	private Coordinate newCoordinate(double x, double y, MapContext mapContext) {
		return new Coordinate(x, y, getInitialZ(mapContext));
	}

	@Override
	public double getInitialZ(MapContext mapContext) {
		return Double.NaN;
	}

	@Override
	public void transitionTo_Done(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points = ToolUtilities.removeDuplicated(points);
		if (((points.size() < 3) && (points.size() > 0))
				|| ((points.size() == 0) && (polygons.size() == 0)))
			throw new TransitionException(
					I18N
							.getString("orbisgis.core.ui.editors.map.tool.multipolygonTool_0")); //$NON-NLS-1$
		if (points.size() > 0) {
			addPolygon(vc);
		}
		MultiPolygon mp = gf.createMultiPolygon(polygons
				.toArray(new Polygon[0]));
		if (!mp.isValid()) {
			throw new TransitionException(
					I18N
							.getString("orbisgis.core.ui.editors.map.tool.multipolygonTool_2")); //$NON-NLS-1$
		}
		multipolygonDone(mp, vc, tm);

		polygons.clear();
		transition("init"); //$NON-NLS-1$
	}

	protected abstract void multipolygonDone(MultiPolygon mp, MapContext vc,
			ToolManager tm) throws TransitionException;

	@Override
	public void transitionTo_Cancel(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
		drawIn_Point(g, vc, tm);
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	@Override
	public void drawIn_Point(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
		Point2D current = tm.getLastRealMousePosition();
		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		tempPoints.add(newCoordinate(current.getX(), current.getY(), vc));
		tempPoints.add(newCoordinate(tempPoints.get(0).x, tempPoints.get(0).y,
				vc));
		ArrayList<Polygon> tempPolygons = (ArrayList<Polygon>) polygons.clone();
		if (tempPoints.size() >= 4) {
			tempPolygons.add(gf.createPolygon(gf.createLinearRing(tempPoints
					.toArray(new Coordinate[0])), new LinearRing[0]));
		}

		if (tempPolygons.size() == 0)
			return;

		MultiPolygon mp = gf.createMultiPolygon(tempPolygons
				.toArray(new Polygon[0]));

		tm.addGeomToDraw(mp);

		if (!mp.isValid()) {
			throw new DrawingException(
					I18N
							.getString("orbisgis.core.ui.editors.map.tool.multipolygonTool_2")); //$NON-NLS-1$
		}
	}

	@Override
	public void drawIn_Line(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
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
