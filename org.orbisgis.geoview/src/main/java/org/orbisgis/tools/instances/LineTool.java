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
package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.generated.Line;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class LineTool extends Line {

	private ArrayList<Coordinate> points = new ArrayList<Coordinate>();

	@Override
	public void transitionTo_Standby() throws FinishedAutomatonException,
			TransitionException {
		points.clear();
	}

	@Override
	public void transitionTo_Point() throws FinishedAutomatonException,
			TransitionException {
		points.add(new Coordinate(tm.getValues()[0], tm.getValues()[1]));
	}

	@Override
	public void transitionTo_Done() throws FinishedAutomatonException,
			TransitionException {
		if (points.size() < 2)
			throw new TransitionException(Messages.getString("MultilineTool.0")); //$NON-NLS-1$
		try {
			LineString ls = new GeometryFactory().createLineString(points
					.toArray(new Coordinate[0]));
			com.vividsolutions.jts.geom.Geometry g = ls;
			if (ec.getActiveThemeGeometryType() == Primitive.MULTILINE_GEOMETRY_TYPE) {
				g = new GeometryFactory()
						.createMultiLineString(new LineString[] { ls });
			}
			if (!g.isValid()) {
				throw new TransitionException(Messages.getString("LineTool.0")); //$NON-NLS-1$
			}
			ec.newGeometry(g);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}

		transition("init"); //$NON-NLS-1$
	}

	@Override
	public void transitionTo_Cancel() throws FinishedAutomatonException,
			TransitionException {

	}

	@Override
	public void drawIn_Standby(Graphics g) throws DrawingException {

	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	@Override
	public void drawIn_Point(Graphics g) throws DrawingException {
		Point2D current = tm.getLastRealMousePosition();

		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		tempPoints.add(new Coordinate(current.getX(), current.getY()));
		LineString ls = new GeometryFactory().createLineString(tempPoints
				.toArray(new Coordinate[0]));

		tm.addGeomToDraw(ls);

		if (!ls.isValid()) {
			throw new DrawingException(Messages.getString("LineTool.0")); //$NON-NLS-1$
		}
	}

	@Override
	public void drawIn_Done(Graphics g) throws DrawingException {

	}

	@Override
	public void drawIn_Cancel(Graphics g) throws DrawingException {

	}

	public boolean isEnabled() {
		return ((ec.getActiveThemeGeometryType() == Primitive.LINE_GEOMETRY_TYPE) || (ec
				.getActiveThemeGeometryType() == Primitive.MULTILINE_GEOMETRY_TYPE))
				&& ec.isActiveThemeWritable();
	}

	public boolean isVisible() {
		return true;
	}

}
