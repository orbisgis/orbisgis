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
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.gdms.data.SQLDataSourceFactory;
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
public class ST_MakeEllipse extends AbstractScalarSpatialFunction {

    /**
     * Create an ellipse 
     */
    @Override
    public Value evaluate(SQLDataSourceFactory dsf, Value... values) throws FunctionException {

        if (values.length == 3) {
            Point point = values[0].getAsGeometry().getInteriorPoint();
            double centerX = point.getX();
            double centerY = point.getY();
            double width = values[1].getAsDouble();
            double height = values[2].getAsDouble();
            return createPolygonEllipse(centerX, centerY, width, height, 0);
        } else if (values.length == 4) {
            double centerX = values[0].getAsDouble();
            double centerY = values[1].getAsDouble();
            double width = values[2].getAsDouble();
            double height = values[3].getAsDouble();
            return createPolygonEllipse(centerX, centerY, width, height, 0);
        } else {
            double centerX = values[0].getAsDouble();
            double centerY = values[1].getAsDouble();
            double width = values[2].getAsDouble();
            double height = values[3].getAsDouble();
            double rotation = values[4].getAsDouble();
            return createPolygonEllipse(centerX, centerY, width, height, rotation);
        }



    }

    public Value createPolygonEllipse(double centerX, double centerY, double semi_minor_axis, double semi_major_axis, double orientRad) {
        if (semi_minor_axis > 0 && semi_major_axis > 0) {
            Coordinate centre = new Coordinate(centerX, centerY);
            GeometricShapeFactory gsf = new GeometricShapeFactory();
            gsf.setCentre(centre);
            gsf.setWidth(semi_major_axis);
            gsf.setHeight(semi_minor_axis);
            gsf.setRotation(orientRad);
            Polygon ellipse = gsf.createCircle();
            return ValueFactory.createValue(ellipse);
        } else {
            return ValueFactory.createNullValue();
        }
    }

    @Override
    public String getName() {
        return "ST_MakeEllipse";
    }

    @Override
    public String getDescription() {
        return "Creates an ellipse  Polygon formed from the given x, y or a point with semi-major and minor axis length parameters.";
    }

    @Override
    public String getSqlOrder() {
        return "SELECT ST_MakeEllipse(the_point,100,10) or ST_MakeEllipse(0,1,100,10 [,rotation])";
    }

    @Override
    public FunctionSignature[] getFunctionSignatures() {
        return new FunctionSignature[]{
                    new BasicFunctionSignature(getType(null), ScalarArgument.POINT,
                    ScalarArgument.DOUBLE, ScalarArgument.DOUBLE),
                    new BasicFunctionSignature(getType(null), ScalarArgument.DOUBLE, ScalarArgument.DOUBLE,
                    ScalarArgument.DOUBLE, ScalarArgument.DOUBLE),
                    new BasicFunctionSignature(getType(null), ScalarArgument.DOUBLE, ScalarArgument.DOUBLE,
                    ScalarArgument.DOUBLE, ScalarArgument.DOUBLE, ScalarArgument.DOUBLE)
                };
    }
}
