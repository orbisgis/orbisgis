/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Antoine Gourlay
 */
public class UnsignedByteUtilsTest {

        @Test
        public void testUnsignedByte() {
                byte a = 112;
                byte b = -4; // unsigned: 252
                
                // no sign problem
                assertEquals(112, UnsignedByteUtils.unsignedByteToInt(a));
                
                // sign problem
                assertEquals(252, UnsignedByteUtils.unsignedByteToInt(b));
        }
        
        @Test
        public void testUnsignedShort() {
                byte[] a = new byte[2];
                a[0] = (byte) 0xFF;
                a[1] = 0x7E;
                // signed = unsigned = 32511
                
                assertEquals(32511, UnsignedByteUtils.unsignedShortBytesToInt(a));
                assertEquals(32511, UnsignedByteUtils.unsignedShortToInt((short)32511));
                
                byte[] b = new byte[2];
                b[0] = (byte) 0xEA;
                b[1] = (byte) 0x85;
                // signed = -31254
                // unsigned = 34282
                
                assertEquals(34282, UnsignedByteUtils.unsignedShortBytesToInt(b));
                assertEquals(34282, UnsignedByteUtils.unsignedShortToInt((short)-31254));
        }
        
        public void testUnsignedInt() {
                byte[] a = new byte[4];
                a[3] = 0x4;
                a[2] = (byte) 0xA6;
                a[1] = 0x2F;
                a[0] = (byte) 0x81;
                // signed = unsigned = 78 000 001
                
                assertEquals(78000001, UnsignedByteUtils.unsignedIntBytesToLong(a));
                assertEquals(78000001, UnsignedByteUtils.unsignedIntToLong(78000001));
                
                byte[] b = new byte[4];
                b[3] = (byte) 0xAC;
                b[2] = (byte) 0x8D;
                b[1] = (byte) 0xAE;
                b[0] = (byte) 0x9A;
                // signed = -1 400 000 870
                // unsigned = 2 894 966 426
                
                assertEquals(2894966426l, UnsignedByteUtils.unsignedIntBytesToLong(a));
                assertEquals(2894966426l, UnsignedByteUtils.unsignedIntToLong(-1400000870));
        }
}
