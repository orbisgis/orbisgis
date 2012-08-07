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
package org.orbisgis.core.ui.editors.map.tool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import org.gdms.geometryUtils.GeometryException;
import org.gdms.geometryUtils.GeometryTypeUtil;

public class MultilineHandler extends AbstractHandler implements Handler {

        private int linestringIndex;

        public MultilineHandler(Geometry g, int linestringIndex, int vertexIndex,
                Coordinate p, int geomIndex) {
                super(g, vertexIndex, p, geomIndex);
                this.linestringIndex = linestringIndex;
        }

        /**
         * @see org.orbisgis.plugins.core.ui.editors.map.tool.estouro.theme.Handler#moveTo(double,
         *      double)
         */
        public Geometry moveTo(double x, double y)
                throws CannotChangeGeometryException {
                Coordinate p = new Coordinate(x, y);
                MultiLineString mls = (MultiLineString) geometry.clone();
                LineString[] lineString = new LineString[mls.getNumGeometries()];
                for (int i = 0; i < lineString.length; i++) {
                        if (i == linestringIndex) {
                                PointHandler handler = new PointHandler((LineString) mls.getGeometryN(i), GeometryTypeUtil.LINESTRING_GEOMETRY_TYPE,
                                        vertexIndex, p, geomIndex);
                                lineString[i] = (LineString) handler.moveJTSTo(x, y);
                        } else {
                                lineString[i] = (LineString) mls.getGeometryN(i);
                        }

                }

                mls = gf.createMultiLineString(lineString);
                if (!mls.isValid()) {
                        throw new CannotChangeGeometryException(THE_GEOMETRY_IS_NOT_VALID);
                }

                return mls;
        }

        /**
         * @see org.orbisgis.plugins.core.ui.editors.map.tool.estouro.theme.Handler#remove()
         */
        public Geometry remove() throws GeometryException {

                MultiLineString mls = (MultiLineString) geometry;
                LineString[] linestrings = new LineString[mls.getNumGeometries()];
                int vIndex = vertexIndex;
                for (int i = 0; i < linestrings.length; i++) {
                        if (i == linestringIndex) {
                                PointHandler handler = new PointHandler((LineString) mls.getGeometryN(i), GeometryTypeUtil.LINESTRING_GEOMETRY_TYPE, vIndex,
                                        null, geomIndex);
                                linestrings[i] = (LineString) handler.removeVertex();
                        } else {
                                linestrings[i] = (LineString) mls.getGeometryN(i);
                        }
                }

                mls = gf.createMultiLineString(linestrings);
                if (!mls.isValid()) {
                        throw new GeometryException(THE_GEOMETRY_IS_NOT_VALID);
                }

                return mls;
        }
}
