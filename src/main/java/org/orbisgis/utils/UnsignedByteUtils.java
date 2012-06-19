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

/**
 * Utilities to deal specifically with reading/writing unsigned numbers and bytes.
 * 
 * IMPORTANT: all methods in this class use little endian byte arrays.
 * 
 * @author Antoine Gourlay
 */
public final class UnsignedByteUtils {
        
        /**
         * Private constructor for utility class.
         */
        private UnsignedByteUtils() {
        }
        
        /**
         * Converts an array of 4 bytes representing an unsigned integer to a long containing the integer value.
         * @param b an array of 4 bytes
         * @return the unsigned value as a (signed) long.
         */
        public static long unsignedIntBytesToLong(byte[] b) {
                long l = b[4] & 0xFF;
                for (int i = 3; i > 0; i--) {
                        l <<= 8;
                        l |= b[i] & 0xFF;
                }
                return l;
        }
        
        /**
         * Takes the unsigned value of a signed integer and returns it as a long.
         * @param i an integer
         * @return the unsigned value as a (signed) long.
         */
        public static long unsignedIntToLong(int i) {
                return i & 0xFFFFFFFFL;
        }
        
        /**
         * Converts an array of 2 bytes representing an unsigned short to an int containing the short value.
         * @param b an array of 4 bytes
         * @return the unsigned value as a (signed) integer.
         */
        public static int unsignedShortBytesToInt(byte[] b) {
                int i = b[1] & 0xFF;
                i <<= 8;
                i |= b[0] & 0xFF;
                return i;
        }
        
        /**
         * Takes the unsigned value of a signed short and returns it as a integer.
         * @param s an short
         * @return the unsigned value as a (signed) integer.
         */
        public static int unsignedShortToInt(short s) {
                return s & 0xFFFF;
        }
        
        /**
         * Takes the unsigned value of a signed byte and returns it as a integer.
         * @param b a byte
         * @return the unsigned value as a (signed) integer.
         */
        public static int unsignedByteToInt(byte b) {
                return b & 0xFF;
        }

}
