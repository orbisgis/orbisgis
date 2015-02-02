/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.commons.utils;

import org.junit.Test;
import static org.junit.Assert.*;


public class ByteUtilsTest {

        @Test
	public void testShortsBytes() throws Exception {
		short[] shorts = new short[] { 1, 2, 3, 5, 1500, Short.MAX_VALUE };
		byte[] shortBytes = ByteUtils.shortsToBytes(shorts);
		short[] shorts2 = ByteUtils.bytesToShorts(shortBytes);
		assertTrue(shorts.length == shorts2.length);
		for (int i = 0; i < shorts2.length; i++) {
			assertTrue(shorts[i] == shorts2[i]);
		}
	}

        @Test
	public void testFloats2Bytes() throws Exception {
		float[] floats = new float[] { 4f, 2857685.235f, 3f, 356.234f };
		byte[] floatBytes = ByteUtils.floatsToBytes(floats);
		float[] floats2 = ByteUtils.bytesToFloats(floatBytes);
		assertTrue(floats.length == floats2.length);
		for (int i = 0; i < floats2.length; i++) {
			assertTrue(floats[i] == floats2[i]);
		}
	}

        @Test
	public void testInts2Bytes() throws Exception {
		int[] ints = new int[] { 1, 2, 3, 4, 1500 };
		byte[] intBytes = ByteUtils.intsToBytes(ints);
		int[] ints2 = ByteUtils.bytesToInts(intBytes);
		assertTrue(ints.length == ints2.length);
		for (int i = 0; i < ints2.length; i++) {
			assertTrue(ints[i] == ints2[i]);
		}
	}

        @Test
	public void testInt2Byte() throws Exception {
		int i = 1237610;
		byte[] bytes = ByteUtils.intToBytes(i);
		assertTrue(ByteUtils.bytesToInt(bytes) == i);
                
                byte[] bytes2 = ByteUtils.intsToBytes(new int[] { i });
		assertTrue(ByteUtils.bytesToInts(bytes2)[0] == i);
	}
        
        @Test
        public void testBytes2Float() throws Exception {
                byte[] b1 = ByteUtils.intToBytes(1055286886);
                assertTrue(ByteUtils.bytesToFloat(b1) == 0.45f);
        }
}
