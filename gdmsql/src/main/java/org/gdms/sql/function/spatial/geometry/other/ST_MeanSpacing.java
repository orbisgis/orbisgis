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
package org.gdms.sql.function.spatial.geometry.other;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Calculate mean spacing between a geomA and a geomB.
 */
public final class ST_MeanSpacing extends AbstractScalarSpatialFunction {

        @Override
        public Value evaluate(SQLDataSourceFactory dsf, Value[] args)
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
        public Type getType(Type[] argsTypes) {
                return TypeFactory.createType(Type.DOUBLE);
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.GEOMETRY, ScalarArgument.GEOMETRY)
                        };
        }
}
