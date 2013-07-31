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
package org.orbisgis.view.map.tools;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.DrawingException;
import org.orbisgis.view.map.tool.FinishedAutomatonException;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.map.tools.generated.Multipoint;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;

/**
 * Multi points common methods
 */
public abstract class AbstractMultipointTool extends Multipoint implements
		InsertionTool {

	private List<Coordinate> point = new ArrayList<Coordinate>();

	@Override
	public void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void transitionTo_Point(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		Coordinate c = newCoordinate(tm.getValues()[0], tm.getValues()[1], vc);
		point.add(c);
	}

	private Coordinate newCoordinate(double x, double y, MapContext mapContext) {
		return new Coordinate(x, y, getInitialZ(mapContext));
	}

	@Override
	public double getInitialZ(MapContext mapContext) {
		return 0;
	}

	@Override
	public void transitionTo_Done(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		point = ToolUtilities.removeDuplicated(point);
		MultiPoint g = ToolManager.toolsGeometryFactory.createMultiPoint(point
                .toArray(new Coordinate[point.size()]));
		multipointDone(g, vc, tm);

		point = new ArrayList<Coordinate>();
		transition(Code.INIT);
	}

	protected abstract void multipointDone(MultiPoint mp, MapContext vc,
			ToolManager tm) throws TransitionException;

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
		Geometry geom = ToolManager.toolsGeometryFactory.createMultiPoint(point
                .toArray(new Coordinate[point.size()]));
		tm.addGeomToDraw(geom);
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
