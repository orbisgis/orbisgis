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
package org.gdms.sql.function.spatial.geometry.operators;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Compute a ring buffer around a geometry.
 */
public final class ST_RingBuffer extends AbstractScalarSpatialFunction {

        private static final String CAP_STYLE_SQUARE = "square";
        private static final String CAP_STYLE_BUTT = "butt";
        private static final String CAP_STYLE_ROUND = "round";
        private static final GeometryFactory GF = new GeometryFactory();

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
                if ((args[0].isNull()) || (args[1].isNull())) {
                        return ValueFactory.createNullValue();
                } else {
                        final Geometry geom = args[0].getAsGeometry();
                        final double bufferSize = args[1].getAsDouble();
                        Geometry buffer;
                        int numBuffer = args[2].getAsInt();
                        if (args.length == 4) {
                                final String bufferStyle = args[3].toString();
                                buffer = getBuffers(geom, bufferSize, numBuffer, bufferStyle);
                        } else {
                                buffer = getBuffers(geom, bufferSize, numBuffer,
                                        CAP_STYLE_ROUND);
                        }
                        return ValueFactory.createValue(buffer, args[0].getCRS());
                }
        }

        @Override
        public String getName() {
                return "ST_RingBuffer";
        }

        public Geometry getBuffers(Geometry geom, double bufferDistance,
                int numBuffer, String endCapStyle) {

                ArrayList<Geometry> buffers = new ArrayList<Geometry>();

                Geometry previous = geom;
                double distance = 0;
                for (int i = 0; i < numBuffer; i++) {
                        distance += bufferDistance;
                        Geometry newBuffer = runBuffer(geom, distance, endCapStyle);
                        Geometry geomBufferExternal = newBuffer.difference(previous);
                        buffers.add(geomBufferExternal);
                        previous = newBuffer;
                }
                return GF.createGeometryCollection(buffers.toArray(new Geometry[buffers.size()]));

        }

        private Geometry runBuffer(final Geometry geom, final double bufferSize,
                final String endCapStyle) {
                BufferOp bufOp = null;

                if (endCapStyle.equalsIgnoreCase(CAP_STYLE_SQUARE)) {
                        bufOp = new BufferOp(geom, new BufferParameters(
                                BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
                                BufferParameters.CAP_SQUARE));
                } else if (endCapStyle.equalsIgnoreCase(CAP_STYLE_BUTT)) {
                        bufOp = new BufferOp(geom, new BufferParameters(
                                BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
                                BufferParameters.CAP_FLAT));
                } else {
                        bufOp = new BufferOp(geom, new BufferParameters(
                                BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
                                BufferParameters.CAP_ROUND));
                }

                return bufOp.getResultGeometry(bufferSize);
        }

        @Override
        public String getDescription() {
                return "Compute a ring buffer around a geometry.";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_RingBuffer(the_geom, bufferSize, ringNumbers[, 'butt'|'square'|'round']) from myTable;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.GEOMETRY, ScalarArgument.DOUBLE, ScalarArgument.INT),
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.GEOMETRY, ScalarArgument.DOUBLE, ScalarArgument.INT,
                                ScalarArgument.STRING)
                        };
        }
}
