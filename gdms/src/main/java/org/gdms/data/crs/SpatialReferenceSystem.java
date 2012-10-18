/**
 * The GDMS library (Generic Datasource Management System) is a middleware
 * dedicated to the management of various kinds of data-sources such as spatial
 * vectorial data or alphanumeric. Based on the JTS library and conform to the
 * OGC simple feature access specifications, it provides a complete and robust
 * API to manipulate in a SQL way remote DBMS (PostgreSQL, H2...) or flat files
 * (.shp, .csv...).
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
 * or contact directly: info@orbisgis.org
 */
package org.gdms.data.crs;

import com.vividsolutions.jts.geom.Geometry;
import java.io.*;
import org.gdms.data.values.GeometryValue;
import org.gdms.data.values.ValueFactory;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * Transform a geometry from crs code to another one.
 *
 */
public class SpatialReferenceSystem {

        private MathTransform mathTransform;
        private CoordinateReferenceSystem targetCRS;

        public SpatialReferenceSystem(String sourceCRS, String targetCRS) throws NoSuchAuthorityCodeException, FactoryException {
                init(CRS.decode(sourceCRS), CRS.decode(targetCRS));
        }

        public SpatialReferenceSystem(int sourceCRS, int targetCRS) throws NoSuchAuthorityCodeException, FactoryException {
                init(CRS.decode("EPSG:" + sourceCRS), CRS.decode("EPSG:" + targetCRS));
        }

        public SpatialReferenceSystem(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) {
                init(sourceCRS, targetCRS);
        }

        private void init(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) {
                this.targetCRS = targetCRS;
                if ((sourceCRS != null) && (targetCRS != null)) {
                        try {
                                mathTransform = CRS.findMathTransform(sourceCRS, targetCRS,
                                        true);
                        } catch (FactoryException ex) {
                                throw new IllegalArgumentException("Cannot find transformation.", ex);
                        }
                } else {
                        throw new IllegalArgumentException("Source and target CRS cannot be null.");
                }

        }

        public MathTransform getMathTransform() {
                return mathTransform;
        }

        public GeometryValue transform(GeometryValue geom) throws MismatchedDimensionException, TransformException {
                Geometry g = JTS.transform(geom.getAsGeometry(), mathTransform);
                return ValueFactory.createValue(g, targetCRS);
        }

        /**
         * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT
         * String (PRJ).
         *
         * @param stream
         * @return a {@link CoordinateReferenceSystem}
         * @throws UnsupportedParameterException if a PROJ.4 parameter is not
         * supported
         * @throws IOException
         * @throws InvalidValueException if a parameter value is invalid
         */
        public static CoordinateReferenceSystem createFromPrj(InputStream stream) throws IOException, FactoryException {
                BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                StringBuilder b = new StringBuilder();
                while (r.ready()) {
                        b.append(r.readLine());
                }
                return CRS.parseWKT(b.toString());
        }

        /**
         * Creates a {@link CoordinateReferenceSystem} defined by an OGC WKT
         * String (PRJ).
         *
         * @param file
         * @return a {@link CoordinateReferenceSystem}
         * @throws UnsupportedParameterException if a PROJ.4 parameter is not
         * supported
         * @throws IOException if there is a problem reading the file
         * @throws InvalidValueException if a parameter value is invalid
         */
        public static CoordinateReferenceSystem createFromPrj(File file) throws IOException, FactoryException {
                InputStream i = null;
                CoordinateReferenceSystem crs;
                try {
                        i = new FileInputStream(file);
                        crs = createFromPrj(i);
                } finally {
                        if (i != null) {
                                i.close();
                        }
                }

                return crs;
        }
        
        /**
         * 
         * @param wkt
         * @return
         * @throws FactoryException 
         */
        public static CoordinateReferenceSystem parse(String wkt) throws FactoryException{
                return CRS.parseWKT(wkt);               
        }
}
