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
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.TopologyException;
import org.orbisgis.mapeditor.map.geometryUtils.GeometryEdit;

public class PointHandler extends AbstractHandler implements Handler {

    public PointHandler(Geometry g, int vertexIndex,
                Coordinate p, long geomPk) {
                super(g, vertexIndex, p, geomPk);
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
                throws TopologyException {
                if (geometry instanceof MultiPoint) {
                        return GeometryEdit.removeVertex((MultiPoint) geometry, vertexIndex);
                } else if (geometry instanceof LineString) {
                        return GeometryEdit.removeVertex((LineString) geometry, vertexIndex);
                }

                throw new RuntimeException();
        }

        @Override
        public Geometry remove() throws TopologyException {
                if (geometry instanceof Point) {
                        throw new TopologyException(
                                I18N.tr("Cannot remove a vertex from a point geometry")); //$NON-NLS-1$
                } else if (geometry instanceof LineString || geometry instanceof MultiPoint) {
                        com.vividsolutions.jts.geom.Geometry g = removeVertex();
                        if (!g.isValid()) {
                                throw new TopologyException(
                                        I18N.tr("The geometry is not valid"));
                        }
                        return g;
                }
                throw new RuntimeException();
        }
}
