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
package org.gdms.sql.function.spatial.raster.hydrology;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpSlope;
import org.grap.processing.operation.hydrology.D8OpSlopeInDegrees;
import org.grap.processing.operation.hydrology.D8OpSlopeInRadians;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.raster.AbstractScalarRasterFunction;

/**
 * Compute the slopes using a GRAY16/32 DEM as input table.
 * Default unit is percent, but it is also possible to specify
 * it as: radian, degree, percent.
 */
public final class ST_D8Slope extends AbstractScalarRasterFunction {

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
                final GeoRaster geoRasterSrc = args[0].getAsRaster();
                try {
                        Operation slopesOp;
                        if (2 == args.length) {
                                if (args[1].toString().equalsIgnoreCase("radian")) {
                                        slopesOp = new D8OpSlopeInRadians();
                                } else if (args[1].toString().equalsIgnoreCase("degree")) {
                                        slopesOp = new D8OpSlopeInDegrees();
                                } else if (args[1].toString().equalsIgnoreCase("percent")) {
                                        slopesOp = new D8OpSlope();
                                } else {
                                        throw new FunctionException(
                                                "You have to choose between: radian, degree or percent!");
                                }
                        } else {
                                slopesOp = new D8OpSlope();
                        }
                        // compute the slopes directions
                        return ValueFactory.createValue(geoRasterSrc.doOperation(slopesOp));
                } catch (OperationException e) {
                        throw new FunctionException("Cannot do the operation", e);
                }
        }

        @Override
        public String getDescription() {
                return "Compute the slopes using a GRAY16/32 DEM as input table. "
                        + "Default unit is percent, but it is also possible to specify "
                        + "it as: radian, degree, percent.";
        }

        @Override
        public String getName() {
                return "ST_D8Slope";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_D8Slope(raster[, 'radian'|'degree'|'percent' ]) as raster from mydem;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER),
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER, ScalarArgument.STRING)
                        };
        }
}
