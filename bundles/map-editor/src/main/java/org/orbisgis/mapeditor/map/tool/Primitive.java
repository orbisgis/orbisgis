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
package org.orbisgis.mapeditor.map.tool;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.*;

/**
 * A wrapper around GDBMS Geometry in order to provide the handler related
 * methods
 * 
 */
public class Primitive {

        private Geometry geometry;
        private long geomPK;

        /**
         * Creates a new Primitive
         *
         * @param g
         *            Geometry to be wrapped
         * @param geomPK
         *            index of the geometry in the Theme it was read
         */
        public Primitive(Geometry g, long geomPK) {
                this.geometry = g;
                this.geomPK = geomPK;
        }

        public Handler[] getHandlers() {
                Coordinate[] hndPoints;
                Handler[] ret;
                ArrayList<Handler> retArray;
                if (geometry instanceof Point || geometry instanceof LineString) {
                        hndPoints = geometry.getCoordinates();
                        ret = new Handler[hndPoints.length];
                        for (int i = 0; i < hndPoints.length; i++) {
                                ret[i] = new PointHandler(geometry,
                                        i, hndPoints[i], geomPK);
                        }
                        return ret;
                } else if (geometry instanceof MultiPoint) {
                        retArray = new ArrayList<Handler>();
                        for (int g = 0; g < geometry.getNumGeometries(); g++) {
                                hndPoints = geometry.getGeometryN(g).getCoordinates();
                                for (int i = 0; i < hndPoints.length; i++) {
                                        retArray.add(new MultipointHandler(geometry, g, i,
                                                hndPoints[i], geomPK));
                                }
                        }
                        return retArray.toArray(new Handler[retArray.size()]);
                } else if (geometry instanceof MultiLineString) {
                        retArray = new ArrayList<Handler>();
                        for (int g = 0; g < geometry.getNumGeometries(); g++) {
                                hndPoints = geometry.getGeometryN(g).getCoordinates();
                                for (int i = 0; i < hndPoints.length; i++) {
                                        retArray.add(new MultilineHandler(geometry, g, i,
                                                hndPoints[i], geomPK));
                                }
                        }
                        return retArray.toArray(new Handler[retArray.size()]);
                } else if (geometry instanceof Polygon) {
                        retArray = new ArrayList<Handler>();
                        for (int g = 0; g < geometry.getNumGeometries(); g++) {
                                hndPoints = geometry.getGeometryN(g).getCoordinates();
                                for (int i = 0; i < hndPoints.length; i++) {
                                        retArray.add(new PolygonHandler(geometry, g - 1, i,
                                                hndPoints[i], geomPK));
                                }
                        }
                        return retArray.toArray(new Handler[retArray.size()]);
                } else if (geometry instanceof MultiPolygon) {
                        retArray = new ArrayList<Handler>();
                        for (int g = 0; g < geometry.getNumGeometries(); g++) {
                                Geometry pol = geometry.getGeometryN(g);
                                for (int r = 0; r < pol.getNumGeometries(); r++) {
                                        hndPoints = pol.getGeometryN(r).getCoordinates();
                                        for (int i = 0; i < hndPoints.length; i++) {
                                                retArray.add(new MultiPolygonHandler(geometry, g,
                                                        r - 1, i, hndPoints[i], geomPK));
                                        }
                                }
                        }
                        return retArray.toArray(new Handler[retArray.size()]);
                }

                throw new UnsupportedOperationException("for geometry type : " + geometry.getGeometryType()); //$NON-NLS-1$
        }
}
