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
import org.orbisgis.tools.instances.generated.Multiline;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public abstract class AbstractMultilineTool extends Multiline {

	protected GeometryFactory gf = new GeometryFactory();
	protected ArrayList<Coordinate> points = new ArrayList<Coordinate>();
	protected ArrayList<LineString> lines = new ArrayList<LineString>();

	@Override
	public void transitionTo_Done(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		if (((points.size() < 2) && (points.size() > 0))
				|| ((points.size() == 0) && (lines.size() == 0)))
			throw new TransitionException(Messages.getString("MultilineTool.0")); //$NON-NLS-1$
		if (points.size() > 0) {
			addLine();
		}

		MultiLineString mls = gf.createMultiLineString(lines
				.toArray(new LineString[0]));
		if (!mls.isValid()) {
			throw new TransitionException(Messages.getString("MultilineTool.1")); //$NON-NLS-1$
		}

		multilineDone(mls, vc, tm);

		lines.clear();
		transition("init"); //$NON-NLS-1$
	}

	protected abstract void multilineDone(MultiLineString mls, ViewContext vc,
			ToolManager tm) throws TransitionException;

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

	@Override
	public void transitionTo_Line(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		if (points.size() < 2)
			throw new TransitionException(Messages.getString("MultilineTool.0")); //$NON-NLS-1$

		addLine();

		transition("init"); //$NON-NLS-1$
	}

	protected void addLine() throws TransitionException {
		LineString ls = gf.createLineString(points.toArray(new Coordinate[0]));
		if (!ls.isValid()) {
			throw new TransitionException(Messages.getString("MultilineTool.1")); //$NON-NLS-1$
		}
		lines.add(ls);
	}

	@Override
	public void transitionTo_Cancel(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void drawIn_Standby(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
		drawIn_Point(g, vc, tm);
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	@Override
	public void drawIn_Point(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
		Point2D current = tm.getLastRealMousePosition();
		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		tempPoints.add(new Coordinate(current.getX(), current.getY()));
		ArrayList<LineString> tempLines = (ArrayList<LineString>) lines.clone();
		if (tempPoints.size() >= 2) {
			tempLines.add(gf.createLineString(tempPoints
					.toArray(new Coordinate[0])));
		}

		if (tempLines.size() == 0)
			return;

		MultiLineString mls = gf.createMultiLineString(tempLines
				.toArray(new LineString[0]));

		tm.addGeomToDraw(mls);

		if (!mls.isValid()) {
			throw new DrawingException(Messages.getString("MultilineTool.1")); //$NON-NLS-1$
		}
	}

	@Override
	public void drawIn_Line(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
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
