/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.view.map.tool.DrawingException;
import org.orbisgis.view.map.tool.FinishedAutomatonException;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.map.tools.generated.Multiline;

public abstract class AbstractMultilineTool extends Multiline implements
		InsertionTool {
	protected GeometryFactory gf = new GeometryFactory();
	protected List<Coordinate> points = new ArrayList<Coordinate>();
	protected List<LineString> lines = new ArrayList<LineString>();

	@Override
	public void transitionTo_Done(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points = ToolUtilities.removeDuplicated(points);
		if (((points.size() < 2) && (!points.isEmpty()))
				|| ((points.isEmpty()) && (lines.isEmpty()))) {
                        throw new TransitionException(i18n.tr("Lines must have at least two points"));
                }
		if (!points.isEmpty()) {
			addLine();
		}

		MultiLineString mls = gf.createMultiLineString(lines
                .toArray(new LineString[lines.size()]));
		if (!mls.isValid()) {
			throw new TransitionException(i18n.tr("Invalid multiline"));
		}

		multilineDone(mls, vc, tm);

		lines.clear();
		transition(Code.INIT);
	}

	protected abstract void multilineDone(MultiLineString mls, MapContext vc,
			ToolManager tm) throws TransitionException;

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

	private Coordinate newCoordinate(double x, double y, MapContext mapContext) {
		return new Coordinate(x, y, getInitialZ(mapContext));
	}

	@Override
	public void transitionTo_Line(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points = ToolUtilities.removeDuplicated(points);
		if (points.size() < 2) {
                        throw new TransitionException(i18n.tr("Lines must have at least two points"));
                }

		addLine();

		transition(Code.INIT);
	}

	protected void addLine() throws TransitionException {
		LineString ls = gf.createLineString(points.toArray(new Coordinate[points.size()]));
		if (!ls.isValid()) {
			throw new TransitionException(i18n.tr("Invalid multiline"));
		}
		lines.add(ls);
	}

	@Override
	public double getInitialZ(MapContext mapContext) {
		return Double.NaN;
	}

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
		List<Coordinate> tempPoints = new ArrayList<Coordinate>(points);
		tempPoints.add(newCoordinate(current.getX(), current.getY(), vc));
		List<LineString> tempLines = new ArrayList<LineString>(lines);
		if (tempPoints.size() >= 2) {
			tempLines.add(gf.createLineString(tempPoints
                    .toArray(new Coordinate[tempPoints.size()])));
		}

		if (tempLines.isEmpty()) {
                        return;
                }

		MultiLineString mls = gf.createMultiLineString(tempLines
                .toArray(new LineString[tempLines.size()]));

		tm.addGeomToDraw(mls);

		if (!mls.isValid()) {
			throw new DrawingException(i18n.tr("Invalid multiline"));
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
