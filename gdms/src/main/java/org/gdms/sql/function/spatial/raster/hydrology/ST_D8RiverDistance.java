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
package org.gdms.sql.function.spatial.raster.hydrology;

import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpRiverDistance;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.raster.AbstractScalarRasterFunction;

/**
 * Calculate the maximum length to the river using a GRAY16/32 DEM slopes
 * directions and a GRAY16/32 DEM slopes accumulations as input tables
 */
public final class ST_D8RiverDistance extends AbstractScalarRasterFunction {

        @Override
        public Value evaluate(DataSourceFactory dsf, Value[] args) throws FunctionException {
                final GeoRaster grD8Direction = args[0].getAsRaster();
                final GeoRaster grD8Accumulation = args[1].getAsRaster();
                int riverThreshold = args[2].getAsInt();

                try {
                        final Operation riverDistance = new D8OpRiverDistance(
                                grD8Accumulation, riverThreshold);
                        return ValueFactory.createValue(grD8Direction.doOperation(riverDistance));
                } catch (OperationException e) {
                        throw new FunctionException("Cannot do the operation", e);
                } catch (IOException e) {
                        throw new FunctionException("Accumulation grid is not readable", e);
                }
        }

        @Override
        public String getDescription() {
                return "Calculate the maximum length to the river using a GRAY16/32 DEM slopes "
                        + "directions and a GRAY16/32 DEM slopes accumulations as input tables";
        }

        @Override
        public String getName() {
                return "ST_D8RiverDistance";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_D8RiverDistance(d.raster, a.raster, RiverThreshold) as raster from direction d, accumulation a;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER, ScalarArgument.RASTER, ScalarArgument.INT)
                        };
        }
}
