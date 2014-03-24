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
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.other;

import com.vividsolutions.jts.geom.Geometry;

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
 * Calculate mean spacing between a geomA and a geomB.
 */
public final class ST_MeanSpacing extends AbstractScalarSpatialFunction {

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args)
                throws FunctionException {
                if (args[0].isNull() || args[1].isNull()) {
                        return ValueFactory.createNullValue();
                } else {
                        final Geometry geomGrid = args[0].getAsGeometry();
                        final Geometry geomBuild = args[1].getAsGeometry();

                        final Geometry noBuildSpace = geomGrid.difference(geomBuild);
                        final double s = noBuildSpace.getArea();
                        final double p = noBuildSpace.getLength();

                        final double result = 0.25 * p - 0.5
                                * Math.sqrt(0.25 * p * p - 4 * s);
                        return ValueFactory.createValue(result);
                }
        }

        @Override
        public String getDescription() {
                return "Calculate mean spacing between a geomA and a geomB.";
        }

        @Override
        public String getName() {
                return "ST_MEANSPACING";
        }

        @Override
        public String getSqlOrder() {
                return "select STO_MEANSPACING(a.the_geom,intersection(a.the_geom,b.the_geom)) from grid as a, build as b where intersects(a.the_geom,b.the_geom);";
        }

        @Override
        public int getType(int[] argsTypes) {
                return Type.DOUBLE;
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.GEOMETRY, ScalarArgument.GEOMETRY)
                        };
        }
}
