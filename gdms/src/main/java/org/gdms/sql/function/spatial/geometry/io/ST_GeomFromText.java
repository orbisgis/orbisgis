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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.sql.function.spatial.geometry.io;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Convert a WKT string value into a geometry value
 */
public final class ST_GeomFromText extends AbstractScalarSpatialFunction {

    private static WKTReader reader = new WKTReader();

    @Override
    public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
        if (args[0].isNull()) {
            return ValueFactory.createNullValue();
        } else {
            final Geometry geom;
            try {
                geom = reader.read(args[0].toString());
            } catch (ParseException e) {
                throw new FunctionException("Cannot parse the WKT.", e);
            }

            if (args.length > 1 && !args[1].isNull()) {
                try {
                    String crsStr = args[1].toString();
                    CoordinateReferenceSystem crs = DataSourceFactory.getCRSFactory().getCRS("EPSG:" + crsStr);
                    return ValueFactory.createValue(geom, crs);
                } catch (CRSException ex) {
                    throw new FunctionException("No such authority code", ex);
                }

            } else {
                return ValueFactory.createValue(geom);
            }
        }
    }

    @Override
    public String getName() {
        return "ST_GeomFromText";
    }

    @Override
    public int getType(int[] types) {
        return Type.GEOMETRY;
    }

    @Override
    public String getDescription() {
        return "Convert a WKT string value into a geometry value";
    }

    @Override
    public String getSqlOrder() {
        return "select ST_GeomFromText(myField) from myTable;";
    }

    @Override
    public FunctionSignature[] getFunctionSignatures() {
        return new FunctionSignature[]{
            new BasicFunctionSignature(getType(null),
            ScalarArgument.STRING),
            new BasicFunctionSignature(getType(null),
            ScalarArgument.STRING, ScalarArgument.STRING),
            new BasicFunctionSignature(getType(null),
            ScalarArgument.STRING, ScalarArgument.INT)
        };
    }
}
