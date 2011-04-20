/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 *  
 * 
 */
package org.gdms.sql.function.spatial.geometry.create;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.geometryUtils.GeometryException;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractSpatialFunction;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.gdms.geometryUtils.GeometryEdit;

public class ST_AddVertex extends AbstractSpatialFunction {

        /**
         * Allow capability to add a point into a geometry

         * Take into account multipoints, polygon and linestring
         */
        public Value evaluate(DataSourceFactory dsf, Value[] args) throws FunctionException {

                Geometry geom = args[0].getAsGeometry();
                final Geometry geom1 = args[1].getAsGeometry();
                if (geom.getDimension() > 0) {
                        if (geom1.getDimension() == 0) {
                                int numGeom = geom1.getNumGeometries();
                                for (int i = 0; i < numGeom; i++) {
                                        try {
                                                Point point = (Point) geom1.getGeometryN(i);
                                                geom = GeometryEdit.insertVertex(geom, point);
                                        } catch (GeometryException ex) {
                                                Logger.getLogger(ST_AddVertex.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                }
                                return ValueFactory.createValue(geom);
                        }
                }

                return ValueFactory.createNullValue();

        }

        public String getName() {
                return "ST_AddVertex";
        }

        public Arguments[] getFunctionArguments() {
                return new Arguments[]{new Arguments(Argument.GEOMETRY,
                                Argument.GEOMETRY)};
        }

        public boolean isAggregate() {
                return false;
        }

        public String getDescription() {
                return "Add a set of vertex on a polygon or linestring. ";
        }

        public String getSqlOrder() {
                return "select ST_AddVertex(geometry, multipoints) from myTable;";
        }
}
