/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.sql.function;

import org.junit.Test;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.simplify.ST_PrecisionReducer;

import static org.junit.Assert.*;

/**
 *
 * @author Erwan Bocher
 */
public class SimplifyFunctionTest extends FunctionTest {

        @Test
        public void testST_PrecisionReducer() throws Exception {
                int precision = 2;
                ST_PrecisionReducer sT_PrecisionReducer = new ST_PrecisionReducer();
                Value precisionValue = ValueFactory.createValue(precision);

                WKTReader wKTReader = new WKTReader();
                Geometry JTSPrecisePoint2D = wKTReader.read("POINT(102.531 220.41)");

                Value vg = ValueFactory.createValue(JTSPrecisePoint2D);
                Value value = evaluate(sT_PrecisionReducer, vg, precisionValue);
                Coordinate coord = value.getAsGeometry().getCoordinate();

                assertTrue((coord.x == 102.53) && (coord.y == 220.41));

                Geometry JTSPrecisePoint3D = wKTReader.read("POINT(102.531 220.41 8.002)");

                vg = ValueFactory.createValue(JTSPrecisePoint3D);
                value = evaluate(sT_PrecisionReducer, vg, precisionValue);
                coord = value.getAsGeometry().getCoordinate();

                assertTrue((coord.x == 102.53) && (coord.y == 220.41) && (coord.z == 8.002));
        }
}
