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
package org.orbisgis.editors.map.tools;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

import org.orbisgis.editors.map.tool.DrawingException;
import org.orbisgis.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.editors.map.tools.generated.Multipolygon;
import org.orbisgis.layerModel.MapContext;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public abstract class AbstractMultipolygonTool extends Multipolygon {

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
		points.add(new Coordinate(tm.getValues()[0], tm.getValues()[1]));
	}

	@Override
	public void transitionTo_Line(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		if (points.size() < 3)
			throw new TransitionException(Messages
					.getString("MultipolygonTool.0")); //$NON-NLS-1$

		addPolygon();

		transition("init"); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	private void addPolygon() throws TransitionException {
		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		tempPoints.add(new Coordinate(points.get(0).x, points.get(0).y));
		Coordinate[] coords = tempPoints.toArray(new Coordinate[0]);
		Polygon p = gf.createPolygon(gf.createLinearRing(coords),
				new LinearRing[0]);
		if (!p.isValid()) {
			throw new TransitionException(Messages
					.getString("MultipolygonTool.2")); //$NON-NLS-1$
		}
		polygons.add(p);
	}

	@Override
	public void transitionTo_Done(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		if (((points.size() < 3) && (points.size() > 0))
				|| ((points.size() == 0) && (polygons.size() == 0)))
			throw new TransitionException(Messages
					.getString("MultipolygonTool.0")); //$NON-NLS-1$
		if (points.size() > 0) {
			addPolygon();
		}
		MultiPolygon mp = gf.createMultiPolygon(polygons
				.toArray(new Polygon[0]));
		if (!mp.isValid()) {
			throw new TransitionException(Messages
					.getString("MultipolygonTool.2")); //$NON-NLS-1$
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
		tempPoints.add(new Coordinate(current.getX(), current.getY()));
		tempPoints
				.add(new Coordinate(tempPoints.get(0).x, tempPoints.get(0).y));
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
			throw new DrawingException(Messages.getString("MultipolygonTool.2")); //$NON-NLS-1$
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

	public URL getMouseCursor() {
		return null;
	}

}
