/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.mapeditor.map.tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.tool.DrawingException;
import org.orbisgis.mapeditor.map.tool.FinishedAutomatonException;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.orbisgis.mapeditor.map.tools.generated.Polygon;

public abstract class AbstractPolygonTool extends Polygon implements
        InsertionTool {

    private GeometryFactory gf = new GeometryFactory();


    private List<Coordinate> points = new ArrayList<Coordinate>();


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
    public void transitionTo_Done(MapContext vc, ToolManager tm)
            throws FinishedAutomatonException, TransitionException {
        points = ToolUtilities.removeDuplicated(points);
        if (points.size() < 3) {
            throw new TransitionException(
                    i18n.tr("Polygons must have more than two points"));
        }
        List<Coordinate> tempPoints = new ArrayList<Coordinate>(points);
        double firstX = points.get(0).x;
        double firstY = points.get(0).y;
        tempPoints.add(newCoordinate(firstX, firstY, vc));
        Coordinate[] polygonCoordinates = tempPoints.toArray(new Coordinate[tempPoints.size()]);
        com.vividsolutions.jts.geom.Polygon pol = gf.createPolygon(gf.createLinearRing(polygonCoordinates), new LinearRing[0]);

        if (!pol.isValid()) {
            throw new TransitionException(
                    i18n.tr("Invalid polygon"));
        }
        polygonDone(pol, vc, tm);

        transition(Code.INIT);
    }


    private Coordinate newCoordinate(double x, double y, MapContext mapContext) throws TransitionException {
        return new Coordinate(x, y, getInitialZ(mapContext));
    }


    @Override
    public double getInitialZ(MapContext mapContext) throws TransitionException {
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
        try {
            Geometry geom = getCurrentPolygon(vc, tm);

            if (geom != null) {
                tm.addGeomToDraw(geom);

                if (!geom.isValid()) {
                    throw new DrawingException(i18n.tr("Invalid polygon"));
                }
            }
        } catch (TransitionException ex) {
            throw new DrawingException(ex.getLocalizedMessage(), ex);
        }
    }

    protected Geometry getCurrentPolygon(MapContext vc, ToolManager tm) throws TransitionException {
        Geometry geom;
        if (points.size() >= 2) {
            List<Coordinate> tempPoints = new ArrayList<Coordinate>(points);
            Point2D current = tm.getLastRealMousePosition();
            tempPoints.add(newCoordinate(current.getX(), current.getY(), vc));
            tempPoints.add(newCoordinate(tempPoints.get(0).x,
                                         tempPoints.get(0).y, vc));
            geom = gf.createPolygon(gf.createLinearRing(tempPoints.toArray(new Coordinate[tempPoints.size()])), new LinearRing[0]);

        } else if (points.size() >= 1) {
            List<Coordinate> tempPoints = new ArrayList<Coordinate>(points);
            Point2D current = tm.getLastRealMousePosition();
            tempPoints.add(newCoordinate(current.getX(), current.getY(), vc));
            tempPoints.add(newCoordinate(tempPoints.get(0).x,
                                         tempPoints.get(0).y, vc));
            geom = gf.createLineString(tempPoints.toArray(new Coordinate[tempPoints.size()]));

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
