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
package org.gdms.sql.function.spatial.geometry.distance;

import com.vividsolutions.jts.geom.Coordinate;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;
import java.util.HashSet;
import org.gdms.geometryUtils.GeometryTypeUtil;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionSignature;

/**
 * Compute points along a line.
 */
public final class ST_PointAlongLine extends AbstractScalarSpatialFunction {

        GeometryFactory gf = new GeometryFactory();

        @Override
        public Value evaluate(SQLDataSourceFactory dsf, Value[] args) throws FunctionException {
                final Geometry geom = args[0].getAsGeometry();
                final double segmentLengthFraction = args[1].getAsDouble();
                final double offsetDistance = args[2].getAsDouble();
                if (geom.getDimension() == 0) {
                        throw new FunctionException("Only surface or line are supported ");
                } else {
                        Geometry result = computePointAlongOffSet(geom, segmentLengthFraction, offsetDistance);
                        if (result != null) {
                                return ValueFactory.createValue(result);
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
                if (numGeom > 1) {
                        HashSet<Coordinate> acc = new HashSet<Coordinate>();
                        for (int i = 0; i < numGeom; i++) {
                                //Dot not take into account hole.                                
                                Geometry subGeom = geom.getGeometryN(i);
                                if (GeometryTypeUtil.isPolygon(subGeom)) {
                                        Polygon polygon = (Polygon) subGeom;
                                        HashSet<Coordinate> result = compute(polygon.getExteriorRing().getCoordinates(), segmentLengthFraction, offsetDistance);
                                        acc.addAll(result);
                                } else if (GeometryTypeUtil.isLineString(subGeom)) {
                                        HashSet<Coordinate> result = compute(subGeom.getCoordinates(), segmentLengthFraction, offsetDistance);
                                        acc.addAll(result);
                                }

                        }
                        return gf.createMultiPoint(acc.toArray(new Coordinate[acc.size()]));
                } else {
                        if (GeometryTypeUtil.isPolygon(geom)) {
                                Polygon polygon = (Polygon) geom;
                                HashSet<Coordinate> result = compute(polygon.getExteriorRing().getCoordinates(), segmentLengthFraction, offsetDistance);
                                return gf.createMultiPoint(result.toArray(new Coordinate[result.size()]));
                        } else if (GeometryTypeUtil.isLineString(geom)) {
                                HashSet<Coordinate> result = compute(geom.getCoordinates(), segmentLengthFraction, offsetDistance);
                                return gf.createMultiPoint(result.toArray(new Coordinate[result.size()]));
                        } else {
                                return null;
                        }

                }
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
                return "ST_PointAlongLine";
        }

        @Override
        public String getDescription() {
                return "Compute points along a geometry.";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_PointAlongLine(the_geom, segmentLengthFraction,  offsetDistance) from myTable;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.GEOMETRY, ScalarArgument.DOUBLE, ScalarArgument.DOUBLE)
                        };
        }
}
