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

import com.vividsolutions.jts.geom.*;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.DrawingException;
import org.orbisgis.view.map.tool.FinishedAutomatonException;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.map.tools.generated.Multipolygon;

public abstract class AbstractMultipolygonTool extends Multipolygon implements
        InsertionTool {

    private GeometryFactory gf = new GeometryFactory();


    private List<Coordinate> points = new ArrayList<Coordinate>();


    private List<Polygon> polygons = new ArrayList<Polygon>();


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
        if (points.size() < 3) {
            throw new TransitionException(
                    i18n.tr("Polygons must have more than two points"));
        }
        addPolygon(vc);

        transition(Code.INIT);
    }

    private void addPolygon(MapContext mapContext) throws TransitionException {
        List<Coordinate> tempPoints = new ArrayList<Coordinate>(points);
        tempPoints.add(newCoordinate(points.get(0).x, points.get(0).y,
                                     mapContext));
        Coordinate[] coords = tempPoints.toArray(new Coordinate[tempPoints.size()]);
        Polygon p = gf.createPolygon(gf.createLinearRing(coords),
                                     new LinearRing[0]);
        if (!p.isValid()) {
            throw new TransitionException(
                    i18n.tr("Invalid multipolygon"));
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
        if (((points.size() < 3) && (!points.isEmpty()))
                || ((points.isEmpty()) && (polygons.isEmpty()))) {
            throw new TransitionException(
                    i18n.tr("Polygons must have more than two points"));
        }
        if (!points.isEmpty()) {
            addPolygon(vc);
        }
        MultiPolygon mp = gf.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
        if (!mp.isValid()) {
            throw new TransitionException(
                    i18n.tr("Invalid multipolygon"));
        }
        multipolygonDone(mp, vc, tm);

        polygons.clear();
        transition(Code.INIT);
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


    @SuppressWarnings("unchecked")
    @Override
    public void drawIn_Point(Graphics g, MapContext vc, ToolManager tm)
            throws DrawingException {
        Point2D current = tm.getLastRealMousePosition();
        List<Coordinate> tempPoints = new ArrayList<Coordinate>(points);
        tempPoints.add(newCoordinate(current.getX(), current.getY(), vc));
        tempPoints.add(newCoordinate(tempPoints.get(0).x, tempPoints.get(0).y,
                                     vc));
        List<Polygon> tempPolygons = new ArrayList<Polygon>(polygons);
        if (tempPoints.size() >= 4) {
            tempPolygons.add(gf.createPolygon(gf.createLinearRing(tempPoints.toArray(new Coordinate[tempPoints.size()])), new LinearRing[0]));
        }

        if (tempPolygons.isEmpty()) {
            return;
        }

        MultiPolygon mp = gf.createMultiPolygon(tempPolygons.toArray(new Polygon[tempPolygons.size()]));

        tm.addGeomToDraw(mp);

        if (!mp.isValid()) {
            throw new DrawingException(
                    i18n.tr("Invalid multipolygon"));
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
