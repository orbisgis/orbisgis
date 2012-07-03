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
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.DrawingException;
import org.orbisgis.view.map.tool.FinishedAutomatonException;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.map.tools.generated.Multiline;

public abstract class AbstractMultilineTool extends Multiline implements
		InsertionTool {
	protected GeometryFactory gf = new GeometryFactory();
	protected ArrayList<Coordinate> points = new ArrayList<Coordinate>();
	protected ArrayList<LineString> lines = new ArrayList<LineString>();

	@Override
	public void transitionTo_Done(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		points = ToolUtilities.removeDuplicated(points);
		if (((points.size() < 2) && (points.size() > 0))
				|| ((points.isEmpty()) && (lines.isEmpty()))) {
                        throw new TransitionException(I18N.tr("Lines must have at least two points"));
                }
		if (points.size() > 0) {
			addLine();
		}

		MultiLineString mls = gf.createMultiLineString(lines
				.toArray(new LineString[0]));
		if (!mls.isValid()) {
			throw new TransitionException(I18N.tr("Invalid multiline"));
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
                        throw new TransitionException(I18N.tr("Lines must have at least two points"));
                }

		addLine();

		transition(Code.INIT);
	}

	protected void addLine() throws TransitionException {
		LineString ls = gf.createLineString(points.toArray(new Coordinate[0]));
		if (!ls.isValid()) {
			throw new TransitionException(I18N.tr("Invalid multiline"));
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
		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		tempPoints.add(newCoordinate(current.getX(), current.getY(), vc));
		ArrayList<LineString> tempLines = (ArrayList<LineString>) lines.clone();
		if (tempPoints.size() >= 2) {
			tempLines.add(gf.createLineString(tempPoints
					.toArray(new Coordinate[0])));
		}

		if (tempLines.isEmpty()) {
                        return;
                }

		MultiLineString mls = gf.createMultiLineString(tempLines
				.toArray(new LineString[0]));

		tm.addGeomToDraw(mls);

		if (!mls.isValid()) {
			throw new DrawingException(I18N.tr("Invalid multiline"));
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
