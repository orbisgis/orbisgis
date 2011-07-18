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
package org.gdms.sql.function.spatial.mixed;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.values.RasterValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Computes the envelope of the parameter and returns a geometry
 */
public final class ST_Envelope extends AbstractScalarSpatialFunction {

        private static final GeometryFactory GF = new GeometryFactory();

        @Override
        public Value evaluate(SQLDataSourceFactory dsf, Value[] args) throws FunctionException {
                Envelope grEnv;
                if (args[0] instanceof RasterValue) {
                        grEnv = args[0].getAsRaster().getMetadata().getEnvelope();
                } else {
                        grEnv = args[0].getAsGeometry().getEnvelopeInternal();
                }
                return ValueFactory.createValue(toGeometry(grEnv));
        }

        private static Geometry toGeometry(final Envelope envelope) {
                if ((0 == envelope.getWidth()) || (0 == envelope.getHeight())) {
                        if (0 == envelope.getWidth() + envelope.getHeight()) {
                                return GF.createPoint(new Coordinate(envelope.getMinX(),
                                        envelope.getMinY()));
                        }
                        return GF.createLineString(new Coordinate[]{
                                        new Coordinate(envelope.getMinX(), envelope.getMinY()),
                                        new Coordinate(envelope.getMaxX(), envelope.getMaxY())});
                }

                return GF.createPolygon(GF.createLinearRing(new Coordinate[]{
                                new Coordinate(envelope.getMinX(), envelope.getMinY()),
                                new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                                new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                                new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                                new Coordinate(envelope.getMinX(), envelope.getMinY())}), null);
        }

        @Override
        public String getDescription() {
                return "Computes the envelope of the parameter and returns a geometry";
        }

        @Override
        public String getName() {
                return "ST_Envelope";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_Envelope (raster) as raster from mytif; ---OR--- select ST_Envelope (the_geom) from mytable;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.GEOMETRY),
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER)
                        };
        }
}
