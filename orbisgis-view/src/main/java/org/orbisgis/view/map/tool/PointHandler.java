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
package org.orbisgis.view.map.tool;



import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import org.gdms.geometryUtils.GeometryEdit;
import org.gdms.geometryUtils.GeometryException;
import org.gdms.geometryUtils.GeometryTypeUtil;

public class PointHandler extends AbstractHandler implements Handler {

        private String geometryType;

        public PointHandler(Geometry g, String geometryType, int vertexIndex,
                Coordinate p, int geomIndex) {
                super(g, vertexIndex, p, geomIndex);
                this.geometryType = geometryType;
        }

        public com.vividsolutions.jts.geom.Geometry moveJTSTo(final double x,
                final double y) throws CannotChangeGeometryException {
                Geometry ret = (Geometry) geometry.clone();
                ret.apply(new CoordinateSequenceFilter() {

                        private boolean done = false;

                        public boolean isGeometryChanged() {
                                return true;
                        }

                        public boolean isDone() {
                                return done;
                        }

                        public void filter(CoordinateSequence seq, int i) {
                                if (i == vertexIndex) {
                                        seq.setOrdinate(i, 0, x);
                                        seq.setOrdinate(i, 1, y);
                                        done = true;
                                }
                        }
                });

                return ret;
        }

        public Geometry moveTo(double x, double y)
                throws CannotChangeGeometryException {
                com.vividsolutions.jts.geom.Geometry ret = moveJTSTo(x, y);
                if (!ret.isValid()) {
                        throw new CannotChangeGeometryException(I18N.tr("The geometry is not valid"));
                }
                return ret;
        }

        public com.vividsolutions.jts.geom.Geometry removeVertex()
                throws GeometryException {
                if (GeometryTypeUtil.isMultiPoint(geometry)) {
                        return GeometryEdit.removeVertex((MultiPoint) geometry, vertexIndex);
                } else if (GeometryTypeUtil.isLineString(geometry)) {
                        return GeometryEdit.removeVertex((LineString) geometry, vertexIndex);
                }

                throw new RuntimeException();
        }

        /**
         * @see org.orbisgis.plugins.core.ui.editors.map.tool.estouro.theme.Handler#remove()
         */
        @Override
        public Geometry remove() throws GeometryException {
                if (GeometryTypeUtil.isPoint(geometry)) {
                        throw new GeometryException(
                                I18N.tr("Cannot remove a vertex from a point geometry")); //$NON-NLS-1$
                } else if ((GeometryTypeUtil.isLineString(geometry))
                        || (GeometryTypeUtil.isMultiPoint(geometry))) {
                        com.vividsolutions.jts.geom.Geometry g = removeVertex();
                        if (!g.isValid()) {
                                throw new GeometryException(
                                        I18N.tr("The geometry is not valid"));
                        }
                        return g;
                }
                throw new RuntimeException();
        }
}
