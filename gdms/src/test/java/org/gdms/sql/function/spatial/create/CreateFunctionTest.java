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
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Gwendall PETIT
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
package org.gdms.sql.function.spatial.create;

import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.io.WKTReader;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.geometry.create.ST_MakeEnvelope;
import org.gdms.sql.function.spatial.geometry.create.ST_RemoveDuplicateCoordinate;

public class CreateFunctionTest extends FunctionTest {

        /**
         * Test the make envelope function
         * @throws Exception
         */
        public void testST_MakeEnvelope() throws Exception {

                ST_MakeEnvelope st_MakeEnvelope = new ST_MakeEnvelope();
                Envelope env = JTSPolygon2D.getEnvelopeInternal();
                Value[] values = new Value[]{ValueFactory.createValue(env.getMinX()), ValueFactory.createValue(env.getMinY()), ValueFactory.createValue(env.getMaxX()), ValueFactory.createValue(env.getMaxY())};
                Value result = evaluate(st_MakeEnvelope, values);
                assertTrue(env.getMinX() == result.getAsGeometry().getEnvelopeInternal().getMinX());
                assertTrue(env.getMinY() == result.getAsGeometry().getEnvelopeInternal().getMinY());
                assertTrue(env.getMaxX() == result.getAsGeometry().getEnvelopeInternal().getMaxX());
                assertTrue(env.getMaxY() == result.getAsGeometry().getEnvelopeInternal().getMaxY());
        }

        /**
         * Remove repeated coordinates.
         * @throws Exception
         */
        public void testRemoveDuplicateCoordinate() throws Exception {
                ST_RemoveDuplicateCoordinate sT_RemoveRepeatedPoints = new ST_RemoveDuplicateCoordinate();
                WKTReader wktr = new WKTReader();
                Value[] values = new Value[]{ValueFactory.createValue(wktr.read("LINESTRING(0 0, 1 0, 1 0, 2 10, 0 0 )"))};
                Value result = evaluate(sT_RemoveRepeatedPoints, values);
                assertTrue(JTSMultiPoint2D.getNumGeometries() != result.getAsGeometry().getNumGeometries());
                assertTrue(!CoordinateArrays.hasRepeatedPoints(result.getAsGeometry().getCoordinates()));

                values = new Value[]{ValueFactory.createValue(wktr.read("POLYGON((0 0, 1 0, 1 0, 2 10, 0 0 ))"))};
                result = evaluate(sT_RemoveRepeatedPoints, values);
                assertTrue(JTSMultiPoint2D.getNumGeometries() != result.getAsGeometry().getNumGeometries());
                assertTrue(!CoordinateArrays.hasRepeatedPoints(result.getAsGeometry().getCoordinates()));

                values = new Value[]{ValueFactory.createValue(wktr.read("POLYGON (( 155 186, 155 282, 276 282, 276 282, 276 186, 155 186 ), ( 198 253, 198 253, 198 218, 198 218, 244 222, 239 243, 198 253 ))"))};
                result = evaluate(sT_RemoveRepeatedPoints, values);
                assertTrue(JTSMultiPoint2D.getNumGeometries() != result.getAsGeometry().getNumGeometries());
                assertTrue(!CoordinateArrays.hasRepeatedPoints(result.getAsGeometry().getCoordinates()));

        }
}
