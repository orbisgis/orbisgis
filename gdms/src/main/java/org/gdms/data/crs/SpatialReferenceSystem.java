/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.crs;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;
import org.jproj.BasicCoordinateTransform;
import org.jproj.CoordinateReferenceSystem;
import org.jproj.CoordinateTransform;
import org.jproj.ProjCoordinate;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.GeometryValue;
import org.gdms.data.values.ValueFactory;

/**
 * 
 * Transform a geometry from crs code to another one.
 *
 */
public class SpatialReferenceSystem {

        private CoordinateTransform coordTransform;

        public SpatialReferenceSystem(DataSourceFactory dsf, int sourceCRS, int targetCRS) {
                init(dsf.getCrsFactory().createFromName("EPSG:" + sourceCRS), dsf.getCrsFactory().createFromName("EPSG:" + targetCRS));
        }

        public SpatialReferenceSystem(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) {
                init(sourceCRS, targetCRS);
        }

        private void init(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) {
                if ((sourceCRS != null) && (targetCRS != null)) {
                        coordTransform = new BasicCoordinateTransform(sourceCRS, targetCRS);
                } else {
                        throw new IllegalArgumentException("Source and target CRS cannot be null.");
                }

        }

        public CoordinateTransform getCoordinateTransform() {
                return coordTransform;
        }

        public GeometryValue transform(GeometryValue geom) {
                Geometry g = getGeometryTransformer().transform(geom.getAsGeometry());
                
                return ValueFactory.createValue(g, coordTransform.getTargetCRS());
        }

        public GeometryTransformer getGeometryTransformer() {
                GeometryTransformer gt = new GeometryTransformer() {

                        @Override
                        protected CoordinateSequence transformCoordinates(
                                CoordinateSequence cs, Geometry geom) {
                                Coordinate[] cc = geom.getCoordinates();
                                CoordinateSequence newcs = new CoordinateArraySequence(cc);
                                for (int i = 0; i < cc.length; i++) {
                                        Coordinate c = cc[i];
                                        ProjCoordinate co = new ProjCoordinate(c.x, c.y, c.z);
                                        ProjCoordinate tg = new ProjCoordinate();
                                        coordTransform.transform(co, tg);
                                        newcs.setOrdinate(i, 0, tg.x);
                                        newcs.setOrdinate(i, 1, tg.y);
                                        newcs.setOrdinate(i, 2, tg.z);
                                }
                                return newcs;
                        }
                };

                return gt;
        }
}
