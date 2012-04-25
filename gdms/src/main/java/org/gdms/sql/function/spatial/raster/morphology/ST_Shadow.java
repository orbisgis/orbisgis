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
package org.gdms.sql.function.spatial.raster.morphology;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.others.Orientations;
import org.grap.processing.operation.others.Shadows;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.raster.AbstractScalarRasterFunction;

/**
 * Return a raster shadow according to an azimuth. 1 = EAST, 3 = SOUTH ... 8 = NORTH EAST
 */
public final class ST_Shadow extends AbstractScalarRasterFunction {

        public static final Map<Integer, Orientations> ORIENTATIONS;

        static {
                Map<Integer, Orientations> ors = new HashMap<Integer, Orientations>(8);
                ors.put(7, Orientations.NORTH);
                ors.put(8, Orientations.NORTHEAST);
                ors.put(1, Orientations.EAST);
                ors.put(2, Orientations.SOUTHEAST);
                ors.put(3, Orientations.SOUTH);
                ors.put(4, Orientations.SOUTHWEST);
                ors.put(5, Orientations.WEST);
                ors.put(6, Orientations.NORTHWEST);
                ORIENTATIONS = Collections.unmodifiableMap(ors);
        }

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {

                GeoRaster geoRasterSrc = args[0].getAsRaster();
                int orientationInt = args[1].getAsInt();
                final Orientations orientation = ORIENTATIONS.get(orientationInt);
                if (null != orientation) {
                        GeoRaster result;
                        try {
                                result = geoRasterSrc.doOperation(new Shadows(orientation));
                                return ValueFactory.createValue(result);
                        } catch (OperationException e) {
                                throw new FunctionException("Cannot compute the shadow", e);
                        }
                }

                return ValueFactory.createNullValue();
        }

        @Override
        public String getDescription() {
                return "Return a raster shadow according to an azimuth. 1 = EAST, 3 = SOUTH ... 8 = NORTH EAST";
        }

        @Override
        public String getName() {
                return "ST_Shadow()";
        }

        @Override
        public String getSqlOrder() {
                return "Select ST_Shadow(raster, int ) from myRaster";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER, ScalarArgument.INT)
                        };
        }
}
