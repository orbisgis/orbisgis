/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
 *
 * Copyright (C) 2011 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Gwendall PETIT
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
 * info _at_ orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.create;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractSpatialFunction;

/**
 *
 * @author ebocher
 */
public class ST_MakeEnvelope extends AbstractSpatialFunction {

    /**
     * xmin, ymin : 306240, 2255480
    xmax, ymax : 308740, 2258160
     *
     */
    GeometryFactory gf = new GeometryFactory();

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
    public boolean isAggregate() {
        return false;
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
    public Arguments[] getFunctionArguments() {
        return new Arguments[]{new Arguments(Argument.NUMERIC,
                    Argument.NUMERIC, Argument.NUMERIC, Argument.NUMERIC), new Arguments(Argument.NUMERIC,
                    Argument.NUMERIC, Argument.NUMERIC, Argument.NUMERIC, Argument.INT)};
    }
}
