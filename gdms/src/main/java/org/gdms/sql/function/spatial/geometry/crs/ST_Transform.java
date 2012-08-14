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
package org.gdms.sql.function.spatial.geometry.crs;

import org.jproj.CRSFactory;
import org.jproj.CoordinateReferenceSystem;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.crs.SpatialReferenceSystem;
import org.gdms.data.values.GeometryValue;
import org.gdms.data.values.Value;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Re-project a geometry from a CRS to new CRS. Only authority codes are allowed, like "EPSG:4326"
 * or "IGNF:WGS84".
 */
public final class ST_Transform extends AbstractScalarSpatialFunction {

        private SpatialReferenceSystem spatialReferenceSystem;

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... values) throws FunctionException {
                GeometryValue geomVal = (GeometryValue) values[0];
                if (spatialReferenceSystem == null) {
                        CoordinateReferenceSystem inputCRS;
                        CoordinateReferenceSystem targetCRS;
                        
                        final CRSFactory crsFactory = dsf.getCrsFactory();
                        if (values.length == 3) {
                                inputCRS = crsFactory.createFromName(values[1].getAsString());
                                targetCRS = crsFactory.createFromName(values[2].getAsString());
                        } else {
                                inputCRS = geomVal.getCRS();
                                targetCRS = crsFactory.createFromName(values[1].getAsString());
                        }
                        
                        spatialReferenceSystem = new SpatialReferenceSystem(inputCRS, targetCRS);
                }
                return spatialReferenceSystem.transform(geomVal);
        }

        @Override
        public String getDescription() {
                return "Re-project a geometry from a CRS to a new CRS.  EPSG, IGNF and ESRI code allowed."
                        + " The default source CRS is the internal one of the input geometry.";
        }

        @Override
        public String getName() {
                return "ST_TRANSFORM";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT ST_TRANSFORM(the_geom, [sourceCRSCode, ] targetCRSCode) from myTable";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null), ScalarArgument.GEOMETRY,
                                ScalarArgument.STRING),
                                new BasicFunctionSignature(getType(null), ScalarArgument.GEOMETRY,
                                ScalarArgument.STRING, ScalarArgument.STRING)
                        };
        }
}
