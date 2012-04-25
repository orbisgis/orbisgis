/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.sql.function.spatial.raster.algebra;

import java.util.Map;

import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.GeoRasterCalculator;
import org.grap.processing.operation.GeoRasterMath;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.RasterValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.raster.AbstractScalarRasterFunction;

/**
 * A function to divide, multiple, substract raster.
 */
public final class ST_RasterAlgebra extends AbstractScalarRasterFunction {

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
                final GeoRaster raster1 = args[0].getAsRaster();
                Value value2 = args[1];
                if (value2 instanceof RasterValue) {
                        final GeoRaster raster2 = value2.getAsRaster();
                        String method = args[2].getAsString();
                        try {
                                Map<String, Integer> methods = GeoRasterCalculator.operators;
                                if (methods.containsKey(method.toLowerCase())) {
                                        final GeoRaster grResult = raster1.doOperation(new GeoRasterCalculator(raster2,
                                                methods.get(method)));
                                        return ValueFactory.createValue(grResult);
                                }
                        } catch (OperationException e) {
                                throw new FunctionException("Cannot do the operation", e);
                        }
                } else {
                        String method = value2.getAsString();
                        double value = args[2].getAsDouble();
                        try {
                                Map<String, Integer> methods = GeoRasterMath.operators;
                                if (methods.containsKey(method.toLowerCase())) {
                                        final GeoRaster grResult = raster1.doOperation(new GeoRasterMath(value, methods.get(method)));
                                        return ValueFactory.createValue(grResult);
                                }
                        } catch (OperationException e) {
                                throw new FunctionException("Cannot do the operation", e);
                        }
                }

                return ValueFactory.createNullValue();
        }

        @Override
        public String getDescription() {
                return "A function to divide, multiple, substract raster.";
        }

        @Override
        public String getName() {
                return "ST_RasterAlgebra";
        }

        @Override
        public String getSqlOrder() {
                return "Select ST_RasterAlgebra(raster1, raster2, 'method') from table;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER, ScalarArgument.RASTER, ScalarArgument.STRING),
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER, ScalarArgument.STRING, ScalarArgument.DOUBLE)
                        };
        }
}
