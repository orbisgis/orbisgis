/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
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
