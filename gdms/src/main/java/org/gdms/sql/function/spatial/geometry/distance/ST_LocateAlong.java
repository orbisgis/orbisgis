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
package org.gdms.sql.function.spatial.geometry.distance;

import java.util.HashSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.geometryUtils.GeometryTypeUtil;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Compute points along a line.
 */
public final class ST_LocateAlong extends AbstractScalarSpatialFunction {

        GeometryFactory gf = new GeometryFactory();

        @Override
        public Value evaluate(DataSourceFactory dsf, Value[] args) throws FunctionException {
                final Geometry geom = args[0].getAsGeometry();
                final double segmentLengthFraction = args[1].getAsDouble();
                final double offsetDistance = args[2].getAsDouble();
                if (geom.getDimension() == 0) {
                        throw new FunctionException("Only surface or line are supported ");
                } else {
                        Geometry result = computePointAlongOffSet(geom, segmentLengthFraction, offsetDistance);
                        if (result != null) {
                                return ValueFactory.createValue(result, args[0].getCRS());
                        } else {
                                return ValueFactory.createNullValue();
                        }

                }

        }

        /**
         * Compute point along a line using  a length fraction and a offset 
         * @param geom
         * @return 
         */
        public Geometry computePointAlongOffSet(Geometry geom, double segmentLengthFraction, double offsetDistance) {
                int numGeom = geom.getNumGeometries();
                HashSet<Coordinate> acc = new HashSet<Coordinate>();
                for (int i = 0; i < numGeom; i++) {                                                      
                        Geometry subGeom = geom.getGeometryN(i);
                        if (GeometryTypeUtil.isPolygon(subGeom)) {
                                Polygon polygon = (Polygon) subGeom;
                                //Dot not take into account hole.  
                                HashSet<Coordinate> result = compute(polygon.getExteriorRing().getCoordinates(), segmentLengthFraction, offsetDistance);
                                acc.addAll(result);
                        } else if (GeometryTypeUtil.isLineString(subGeom)) {
                                HashSet<Coordinate> result = compute(subGeom.getCoordinates(), segmentLengthFraction, offsetDistance);
                                acc.addAll(result);
                        }

                }
                return gf.createMultiPoint(acc.toArray(new Coordinate[acc.size()]));
        }

        private HashSet<Coordinate> compute(Coordinate[] coords, double segmentLengthFraction, double offsetDistance) {
                HashSet<Coordinate> coordOffSet = new HashSet<Coordinate>();
                for (int j = 0; j < coords.length - 1; j++) {
                        LineSegment seg = new LineSegment(coords[j], coords[j + 1]);
                        Coordinate coord = seg.pointAlongOffset(segmentLengthFraction, offsetDistance);
                        if (coord != null) {
                                coordOffSet.add(coord);
                        }
                }

                return coordOffSet;
        }

        @Override
        public String getName() {
                return "ST_LocateAlong";
        }

        @Override
        public String getDescription() {
                return "Return a collection of points along a line that match the specified segment length fraction and offset distance.\n"
                        + "A positive offset will be to the left, and a negative one to the right.\n"
                        + "Note : For surface elements only exterior ring are supported.";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_LocateAlong(the_geom, segmentLengthFraction,  offsetDistance) from myTable;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.GEOMETRY, ScalarArgument.DOUBLE, ScalarArgument.DOUBLE)
                        };
        }
}
