/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.raster.hydrology;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.spatial.raster.AbstractScalarRasterFunction;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpConstrainedAccumulation;

/**
 * This function compute a constrained grid accumulation based on two grids:
 * a direction's grid and an integer grid that represents
 * some anthropic constraints such as hedgerows or roads.
 */
public final class ST_D8ConstrainedAccumulation extends AbstractScalarRasterFunction {

        @Override
        public Value evaluate(DataSourceFactory dsf, Value[] args) throws FunctionException {
                GeoRaster grD8Direction = args[0].getAsRaster();
                GeoRaster grConstrained = args[1].getAsRaster();

                try {
                        final Operation opConstrainedAccumulation = new D8OpConstrainedAccumulation(
                                grConstrained);
                        return ValueFactory.createValue(grD8Direction.doOperation(opConstrainedAccumulation));
                } catch (OperationException e) {
                        throw new FunctionException("Cannot do the operation", e);
                } catch (UnsupportedOperationException e) {
                        throw new FunctionException("Cannot set nodata value", e);
                }
        }

        // TODO To be completed
        @Override
        public String getDescription() {
                return "This function compute a constrained grid accumulation based on two grids: "
                        + "a direction's grid and an integer grid that represents"
                        + "some anthropic constraints such as hedgerows or roads.";
        }

        @Override
        public String getName() {
                return "ST_D8ConstrainedAccumulation";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_D8ConstrainedAccumulation(d.raster, a.raster) from directions d, constrainedgrid a;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER, ScalarArgument.RASTER)
                        };
        }
}
