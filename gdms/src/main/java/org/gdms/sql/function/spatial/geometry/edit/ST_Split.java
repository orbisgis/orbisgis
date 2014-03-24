/**
 * The GDMS library (Generic Datasource Management System) is a middleware
 * dedicated to the management of various kinds of data-sources such as spatial
 * vectorial data or alphanumeric. Based on the JTS library and conform to the
 * OGC simple feature access specifications, it provides a complete and robust
 * API to manipulate in a SQL way remote DBMS (PostgreSQL, H2...) or flat files
 * (.shp, .csv...).
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
 * or contact directly: info@orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.edit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.geometryUtils.GeometryEdit;
import org.gdms.geometryUtils.GeometryTypeUtil;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * This function split a line by a line a line by a point a polygon by a line
 *
 * @author Erwan Bocher
 */
public class ST_Split extends AbstractScalarSpatialFunction {

    public static final double DEFAULT_TOLERANCE = 10E-6;
    
    @Override
    public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
        Geometry result = null;
        double tolerance =DEFAULT_TOLERANCE;
        if(args.length==3){
              tolerance = args[2].getAsDouble();
        }
        Geometry geomA = args[0].getAsGeometry();
        Geometry geomB = args[1].getAsGeometry();
      
        //We split a polygon with a linestring
        if (GeometryTypeUtil.isPolygon(geomA)) {
            result = GeometryEdit.splitPolygonWithLine((Polygon) geomA, (LineString) geomB);
        } //We split a linestring
        else if (GeometryTypeUtil.isLineString(geomA)) {
            //with another linestring
            if (GeometryTypeUtil.isLineString(geomB)) {
                result = GeometryEdit.splitLineStringWithLine((LineString) geomA, (LineString) geomB);
            } //with a point
            else if (GeometryTypeUtil.isPoint(geomB)) {
                result = GeometryEdit.splitLineWithPoint((LineString) geomA, (Point) geomB, tolerance);
            }
        } else if (GeometryTypeUtil.isMultiLineString(geomA)) {
            if (GeometryTypeUtil.isLineString(geomB)) {
                result = GeometryEdit.splitMultiLineStringWithLine((MultiLineString) geomA, (LineString) geomB);
            } else if (GeometryTypeUtil.isPoint(geomB)) {
                result = GeometryEdit.splitMultiLineStringWithPoint((MultiLineString) geomA, (Point) geomB, tolerance);
            }
        }
        if (result != null) {
            return ValueFactory.createValue(result);
        }
        return ValueFactory.createNullValue();
    }

    @Override
    public String getDescription() {
        return "<html>Split a geometry with another geometry\n."
                + "Supported operations are : \n"
                + "<ul><li>Split a polygon with a linestring</li>"
                + "<li>Split a multilinestring with a linestring or a point</li>"
                + "<li>Split a linestring with a linestring or a point</li>"
                + "Note : if the second geometry is a point\n"
                + "the user can specify a tolerance to snap\n"
                + "the point to the geometry. The default tolerance is : \n"
                + "10^(-6) meters.</ul></html>";
    }

    @Override
    public String getName() {
        return "ST_Split";
    }

    @Override
    public String getSqlOrder() {
        return "SELECT ST_Split('LINESTRING(0 0, 10 0)'::LINESTRING, 'POINT(5 0)'::POINT, [tolerance :: DOUBLE]);";
    }

    @Override
    public FunctionSignature[] getFunctionSignatures() {
        return new FunctionSignature[]{
            new BasicFunctionSignature(Type.MULTIPOLYGON, ScalarArgument.POLYGON, ScalarArgument.LINESTRING),
            new BasicFunctionSignature(Type.MULTILINESTRING, ScalarArgument.LINESTRING, ScalarArgument.LINESTRING),
            new BasicFunctionSignature(Type.LINESTRING, ScalarArgument.LINESTRING, ScalarArgument.LINESTRING),
            new BasicFunctionSignature(Type.MULTILINESTRING, ScalarArgument.LINESTRING, ScalarArgument.POINT),
            new BasicFunctionSignature(Type.MULTILINESTRING, ScalarArgument.LINESTRING, ScalarArgument.POINT, ScalarArgument.DOUBLE),
            new BasicFunctionSignature(Type.MULTILINESTRING, ScalarArgument.MULTILINESTRING, ScalarArgument.LINESTRING),
            new BasicFunctionSignature(Type.MULTILINESTRING, ScalarArgument.MULTILINESTRING, ScalarArgument.POINT),
            new BasicFunctionSignature(Type.MULTILINESTRING, ScalarArgument.MULTILINESTRING, ScalarArgument.POINT, ScalarArgument.DOUBLE)
        };
    }
}
