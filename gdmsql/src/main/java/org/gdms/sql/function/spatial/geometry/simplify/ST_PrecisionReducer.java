/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * Copyright (C) 2011 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
package org.gdms.sql.function.spatial.geometry.simplify;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.gdms.data.values.Value;
import org.gdms.sql.function.FunctionException;
import com.vividsolutions.jts.precision.SimpleGeometryPrecisionReducer;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * A function to reduce the geometry precision
 * @author ebocher
 */
public class ST_PrecisionReducer extends AbstractScalarSpatialFunction {

        @Override
        public Value evaluate(SQLDataSourceFactory dsf, Value... values) throws FunctionException {
                final int nbDec = values[1].getAsInt();
                if (nbDec < 0) {
                        throw new FunctionException("Decimal_places has to be >= 0.");
                }
                PrecisionModel pm = new PrecisionModel(scaleFactorForDecimalPlaces(nbDec));
                Geometry geom = SimpleGeometryPrecisionReducer.reduce(values[0].getAsGeometry(), pm);
                return ValueFactory.createValue(geom);
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
         * @param decimalPlaces
         * @return the scale factor
         */
        public static double scaleFactorForDecimalPlaces(int decimalPlaces) {
                return Math.pow(10.0, decimalPlaces);
        }
}
