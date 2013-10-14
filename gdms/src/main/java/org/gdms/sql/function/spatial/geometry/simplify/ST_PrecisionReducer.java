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
package org.gdms.sql.function.spatial.geometry.simplify;

import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * A function to reduce the geometry precision
 *
 * @author Erwan Bocher
 */
public class ST_PrecisionReducer extends AbstractScalarSpatialFunction {

    private GeometryPrecisionReducer gpr = null;

    @Override
    public Value evaluate(DataSourceFactory dsf, Value... values) throws FunctionException {
        if (gpr == null) {
            final int nbDec = values[1].getAsInt();
            if (nbDec < 0) {
                throw new FunctionException("Decimal_places has to be >= 0.");
            }
            PrecisionModel pm = new PrecisionModel(scaleFactorForDecimalPlaces(nbDec));
            gpr = new GeometryPrecisionReducer(pm);
        }
        return ValueFactory.createValue(gpr.reduce(values[0].getAsGeometry()), values[0].getCRS());
    }

    @Override
    public String getName() {
        return "ST_PrecisionReducer";
    }

    @Override
    public String getDescription() {
        return "A function to reduce the geometry precision. Decimal_Place is the number of decimals to keep.";
    }

    @Override
    public String getSqlOrder() {
        return "SELECT ST_PrecisionReducer(GEOMETRY,DECIMAL_PLACES) from myTable";
    }

    @Override
    public FunctionSignature[] getFunctionSignatures() {
        return new FunctionSignature[]{
            new BasicFunctionSignature(getType(null),
            ScalarArgument.GEOMETRY, ScalarArgument.INT)
        };
    }

    /**
     * Computes the scale factor for a given number of decimal places.
     *
     * @param decimalPlaces
     * @return the scale factor
     */
    public static double scaleFactorForDecimalPlaces(int decimalPlaces) {
        return Math.pow(10.0, decimalPlaces);
    }
}
