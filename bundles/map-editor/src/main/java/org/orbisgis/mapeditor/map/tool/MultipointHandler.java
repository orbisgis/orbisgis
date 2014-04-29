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
package org.orbisgis.mapeditor.map.tool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.TopologyException;
import org.orbisgis.core.geometryUtils.GeometryEdit;

public class MultipointHandler extends AbstractHandler implements Handler {

        private int pointIndex;

        /**
         * Draw multi point selection
         * @param g
         * @param pointIndex
         * @param vertexIndex
         * @param p
         * @param geomIndex
         */
        public MultipointHandler(Geometry g, int pointIndex, int vertexIndex,
                Coordinate p, int geomIndex) {
                super(g, vertexIndex, p, geomIndex);
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
