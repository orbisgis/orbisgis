/*
s * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.function.spatial.geometry.convert;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractSpatialFunction;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;

public class ST_Holes extends AbstractSpatialFunction {

        GeometryFactory gf = new GeometryFactory();

        public Value evaluate(DataSourceFactory dsf, Value[] args) throws FunctionException {
                if (args[0].isNull()) {
                        return ValueFactory.createNullValue();
                } else {
                        final Geometry geom = args[0].getAsGeometry();
                        int nb = geom.getNumGeometries();

                        for (int i = 0; i < nb; i++) {
                                Geometry subgeom = geom.getGeometryN(i);
                                if (subgeom instanceof Polygon) {
                                        Polygon poly = (Polygon) subgeom;
                                        ArrayList<Geometry> holes = new ArrayList<Geometry>();
                                        if (poly.getNumInteriorRing() > 0) {
                                                for (int j = 0; j < poly.getNumInteriorRing(); j++) {
                                                        Polygon result = gf.createPolygon(gf.createLinearRing(poly.getInteriorRingN(j).getCoordinates()), null);
                                                        holes.add(result);
                                                }
                                                return ValueFactory.createValue(gf.createGeometryCollection(holes.toArray(new Geometry[holes.size()])));

                                        }
                                }
                        }
                        return ValueFactory.createNullValue();
                }
        }

        public String getName() {
                return "ST_Holes";
        }

        public Arguments[] getFunctionArguments() {
                return new Arguments[]{new Arguments(Argument.GEOMETRY)};
        }

        public boolean isAggregate() {
                return false;
        }

        public String getDescription() {
                return "Return all holes as a geometry collection.";
        }

        public String getSqlOrder() {
                return "select ST_Holes(the_geom) from myTable;";
        }
}
