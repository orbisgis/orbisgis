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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.TopologyException;
import org.orbisgis.mapeditor.map.geometryUtils.GeometryEdit;

public class MultipointHandler extends AbstractHandler implements Handler {

        private int pointIndex;

        /**
         * Draw multi point selection
         * @param g
         * @param pointIndex
         * @param vertexIndex
         * @param p
         * @param geomPk
         */
        public MultipointHandler(Geometry g, int pointIndex, int vertexIndex,
                Coordinate p, long geomPk) {
                super(g, vertexIndex, p, geomPk);
                this.pointIndex = pointIndex;
        }

        @Override
        public Geometry moveTo(double x, double y)
                throws CannotChangeGeometryException {
                Coordinate p = new Coordinate(x, y);
                MultiPoint mp = (MultiPoint) geometry.clone();
                Point[] points = new Point[mp.getNumGeometries()];
                for (int i = 0; i < points.length; i++) {
                        if (i == pointIndex) {
                                PointHandler handler = new PointHandler(mp.getGeometryN(i), 0, p,
                                        geomIndex);
                                points[i] = (Point) handler.moveJTSTo(x, y);
                        } else {
                                points[i] = (Point) mp.getGeometryN(i);
                        }

                }

                mp = gf.createMultiPoint(points);
                if (!mp.isValid()) {
                        throw new CannotChangeGeometryException(I18N.tr("Invalid MultiPoint"));
                }

                return mp;
        }

        @Override
        public Geometry remove() throws TopologyException {

                MultiPoint mp = (MultiPoint) geometry;

                mp = GeometryEdit.removeVertex(mp, pointIndex);
                
                if (!mp.isValid()) {
                        throw new TopologyException(I18N.tr("Invalid MultiPoint"));
                }

                return mp;
        }
}
