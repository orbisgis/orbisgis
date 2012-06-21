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
package org.gdms.sql.function.spatial.geometry.create;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 *
 * @author ebocher
 */
public class ST_MakeEnvelope extends AbstractScalarSpatialFunction {

        /**
         * xmin, ymin : 306240, 2255480
        xmax, ymax : 308740, 2258160
         *
         */
        private final GeometryFactory gf = new GeometryFactory();

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... values) throws FunctionException {

                int srid = -1;
                if (values.length == 5) {
                        srid = values[4].getAsInt();
                }

                double xmin = values[0].getAsDouble();
                double ymin = values[1].getAsDouble();
                double xmax = values[2].getAsDouble();
                double ymax = values[3].getAsDouble();

                Coordinate[] coordinates = new Coordinate[]{
                        new Coordinate(xmin, ymin),
                        new Coordinate(xmax, ymin),
                        new Coordinate(xmax, ymax),
                        new Coordinate(xmin, ymax),
                        new Coordinate(xmin, ymin)
                };
                Polygon geom = gf.createPolygon(gf.createLinearRing(coordinates), null);
                geom.setSRID(srid);

                return ValueFactory.createValue(geom);
        }

        @Override
        public String getName() {
                return "ST_MakeEnvelope";
        }

        @Override
        public String getDescription() {
                return "Creates a rectangular Polygon formed from the given xmin, ymin, xmax, ymax. A SRID can be specified.";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT ST_MakeEnvelope(10,11,10,10[,SRID])";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null), ScalarArgument.DOUBLE, ScalarArgument.DOUBLE,
                        ScalarArgument.DOUBLE, ScalarArgument.DOUBLE),
                                new BasicFunctionSignature(getType(null), ScalarArgument.DOUBLE, ScalarArgument.DOUBLE,
                        ScalarArgument.DOUBLE, ScalarArgument.DOUBLE, ScalarArgument.INT)
                        };
        }
}
