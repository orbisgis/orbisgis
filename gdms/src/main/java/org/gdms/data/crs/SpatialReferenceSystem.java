/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY, Adelin PIAU
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.gdms.data.crs;


import org.gdms.data.DataSourceFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;
import org.gdms.data.values.GeometryValue;
import org.gdms.data.values.ValueFactory;
import org.jproj.BasicCoordinateTransform;
import org.jproj.CoordinateReferenceSystem;
import org.jproj.CoordinateTransform;
import org.jproj.ProjCoordinate;

/**
 * 
 * Transform a geometry from crs code to another one.
 *
 */
public class SpatialReferenceSystem {

        private CoordinateTransform coordTransform;

        public SpatialReferenceSystem(DataSourceFactory dsf, int sourceCRS, int targetCRS) {
                init(dsf.getCrsFactory().createFromName("EPSG:" + sourceCRS), dsf.getCrsFactory().createFromName("EPSG:" + sourceCRS));
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
                
                return ValueFactory.createValue(g);
        }

        public GeometryTransformer getGeometryTransformer() {
                GeometryTransformer gt = null;
                gt = new GeometryTransformer() {

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
