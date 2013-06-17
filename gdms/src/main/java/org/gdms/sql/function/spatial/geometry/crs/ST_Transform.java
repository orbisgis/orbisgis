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
package org.gdms.sql.function.spatial.geometry.crs;

import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.crs.SpatialReferenceSystem;
import org.gdms.data.types.Type;
import org.gdms.data.values.GeometryValue;
import org.gdms.data.values.Value;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Project a geometry from one CRS to another. 
 * Only authority codes are allowed, like "EPSG:4326" or "IGNF:WGS84".
 */
public final class ST_Transform extends AbstractScalarSpatialFunction {

    private SpatialReferenceSystem spatialReferenceSystem;
    private boolean doNotTransform = false;
    private boolean isVisited = false;

    @Override
    public Value evaluate(DataSourceFactory dsf, Value... values) throws FunctionException {
        GeometryValue geomVal = (GeometryValue) values[0];
        try {
            if (spatialReferenceSystem == null || isVisited != true) {
                CoordinateReferenceSystem inputCRS = geomVal.getCRS();
                CoordinateReferenceSystem targetCRS;
                if (inputCRS == null) {
                    throw new FunctionException("The input crs cannot be null");
                } else {
                    if (values[1].getType() == Type.INT) {
                        int outPutEPSG = values[1].getAsInt();
                        if (outPutEPSG == -1) {
                            throw new FunctionException(" -1 is an invalid target SRID");
                        } else {
                            targetCRS = DataSourceFactory.getCRSFactory().getCRS("epsg:" + outPutEPSG);
                        }
                    } else {
                        targetCRS = DataSourceFactory.getCRSFactory().getCRS(values[1].getAsString());
                    }

                    if (inputCRS.equals(targetCRS)) {
                        doNotTransform = true;
                    } else {
                        spatialReferenceSystem = new SpatialReferenceSystem((GeodeticCRS) inputCRS, (GeodeticCRS) targetCRS);
                    }
                }
                isVisited = true;
            }
            if (doNotTransform == false) {
                return spatialReferenceSystem.transform(geomVal.getAsGeometry());
            }

            return geomVal;

        } catch (CRSException ex) {
            throw new FunctionException("No such authority code", ex);
        }
    }

    @Override
    public String getDescription() {
        return "Project a geometry from one CRS to another.  EPSG, IGNF and ESRI code allowed."
                + " The default source CRS is the internal one of the input geometry.";
    }

    @Override
    public String getName() {
        return "ST_Transform";
    }

    @Override
    public String getSqlOrder() {
        return "SELECT ST_Transform(the_geom,  targetCRSCode | 'EPSG:4326') from myTable";
    }

    @Override
    public FunctionSignature[] getFunctionSignatures() {
        return new FunctionSignature[]{
            new BasicFunctionSignature(Type.GEOMETRY, ScalarArgument.GEOMETRY,
            ScalarArgument.STRING),
            new BasicFunctionSignature(Type.GEOMETRY, ScalarArgument.GEOMETRY,
            ScalarArgument.INT)
        };
    }
}
