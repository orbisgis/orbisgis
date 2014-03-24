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
package org.gdms.sql.function.spatial.geometry.distance;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LengthIndexedLine;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.AbstractScalarFunction;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;

/**
 * This function is used to project a point on a linestring or multilinestring
 * @author Erwan Bocher
 */
public class ST_ProjectTo extends AbstractScalarFunction {

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... values) throws FunctionException {
                Geometry geom = values[1].getAsGeometry();
                LengthIndexedLine ll = new LengthIndexedLine(geom);
                double index = ll.project(values[0].getAsGeometry().getCoordinate());
                Coordinate p = ll.extractPoint(index);
                Point result = geom.getFactory().createPoint(p);
                if (result != null) {
                        return ValueFactory.createValue(result);
                }
                return values[0];
        }

        @Override
        public int getType(int[] argsTypes) {
                return Type.POINT;
        }

        @Override
        public String getDescription() {
                return "Projet a point along a linestring. If the point projected is out of line "
                        + "the first or last point on the line will be returned otherwise"
                        + " the input point.";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null), ScalarArgument.POINT, ScalarArgument.LINESTRING),
                                new BasicFunctionSignature(getType(null), ScalarArgument.POINT, ScalarArgument.MULTILINESTRING)
                        };
        }

        @Override
        public String getName() {
                return "ST_ProjectTo";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_ProjectTo(point, geometry) from myTable;";
        }
}
