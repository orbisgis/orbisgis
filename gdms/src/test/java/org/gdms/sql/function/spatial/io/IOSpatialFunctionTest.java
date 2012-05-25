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
package org.gdms.sql.function.spatial.io;

import org.junit.Test;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.io.ST_AsWKT;
import org.gdms.sql.function.spatial.geometry.io.ST_GeomFromText;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTWriter;

import static org.junit.Assert.*;

import org.gdms.data.values.Value;

public class IOSpatialFunctionTest extends FunctionTest {

        @Test
        public void testAsWKT() throws Exception {
                String str = testSpatialFunction(new ST_AsWKT(), JTSMultiPolygon2D, 1).getAsString();
                assertEquals(str, new WKTWriter().write(JTSMultiPolygon2D));
                Point p3d = new GeometryFactory().createPoint(new Coordinate(3, 3, 3));
                str = testSpatialFunction(new ST_AsWKT(), p3d, 1).getAsString();
                assertEquals(str, new WKTWriter(3).write(p3d));
        }

        @Test
        public void testGeomFromText() throws Exception {
                String wkt = new WKTWriter().write(JTSMultiPolygon2D);
                Geometry g = testSpatialFunction(new ST_GeomFromText(), new int[]{Type.STRING, Type.STRING}, 2,
                        ValueFactory.createValue(wkt)).getAsGeometry();
                assertEquals(g, JTSMultiPolygon2D);
                Point p3d = new GeometryFactory().createPoint(new Coordinate(3, 3, 3));
                wkt = new WKTWriter(3).write(p3d);
                g = testSpatialFunction(new ST_GeomFromText(), new int[]{Type.STRING, Type.STRING}, 2,
                        ValueFactory.createValue(wkt)).getAsGeometry();
                assertEquals(g, p3d);
        }
        
        @Test
        public void testGeomFromTextWithCRS() throws Exception {
                String wkt = new WKTWriter().write(JTSMultiPolygon2D);
                ST_GeomFromText st = new ST_GeomFromText();
                Value ret = st.evaluate(dsf, new Value[] { ValueFactory.createValue(wkt),
                                 ValueFactory.createValue(4326)});
                
                assertNotNull(ret.getCRS());
                assertEquals("WGS84", ret.getCRS().getDatum().getName());
                
                ret = st.evaluate(dsf, new Value[] { ValueFactory.createValue(wkt),
                                 ValueFactory.createValue("4326")});
                
                assertNotNull(ret.getCRS());
                assertEquals("WGS84", ret.getCRS().getDatum().getName());
                
                ret = st.evaluate(dsf, new Value[] { ValueFactory.createValue(wkt),
                                 ValueFactory.createValue("EPSG:4326")});
                
                assertNotNull(ret.getCRS());
                assertEquals("WGS84", ret.getCRS().getDatum().getName());
        }
}
