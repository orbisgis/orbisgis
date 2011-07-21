/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
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
package org.orbisgis.utils;

/**
 * Utility class for dealing with bytes.
 *
 * IMPORTANT: The methods listed here use different endianness.
 *      - All methods that convert a single int/float/... to an array of bytes (and the other way around) take and return
 *        BIG ENDIAN bytes.
 *      - All methods that convert arrays of ints/floats/... to an array of bytes (and the other way around) take and return
 *        LITTLE ENDIAN bytes.
 */
public final class ByteUtils {

        private static final int BYTESIZE = 0xFF;

        /**
         * Converts a byte array into an integer.
         * 
         * Byte order: big endian
         *
         * Only the first four bytes of the argument are used.
         * @param b an array of bytes of length >= 4
         * @return the corresponding integer
         */
        public static int bytesToInt(byte[] b) {
                if (b.length < 4) {
                        throw new IllegalArgumentException("The length of the byte array should be >= 4");
                }
                return ((b[3] & BYTESIZE)) + ((b[2] & BYTESIZE) << 8)
                        + ((b[1] & BYTESIZE) << 16) + ((b[0] & BYTESIZE) << 24);
        }

        /**
         * Converts an integer to a byte array
         * 
         * Byte order: big endian
         * 
         * @param v an integer
         * @return the corresponding byte array
         */
        public static byte[] intToBytes(int v) {
                byte[] b = new byte[4];
                b[0] = (byte) ((v >>> 24) & BYTESIZE);
                b[1] = (byte) ((v >>> 16) & BYTESIZE);
                b[2] = (byte) ((v >>> 8) & BYTESIZE);
                b[3] = (byte) ((v) & BYTESIZE);

                return b;
        }

        /**
         * Converts an array of shorts into an array of bytes
         * 
         * Byte order: little endian
         * 
         * @param shorts an array of short
         * @return the corresponding byte array
         */
        public static byte[] shortsToBytes(short[] shorts) {
                byte[] ret = new byte[shorts.length * 2];
                for (int i = 0; i < shorts.length; i++) {
                        ret[i * 2] = (byte) ((shorts[i]) & BYTESIZE);
                        ret[i * 2 + 1] = (byte) ((shorts[i] >>> 8) & BYTESIZE);
                }

                return ret;
        }

        /**
         * Converts a short into an array of bytes
         * 
         * Byte order: big endian
         * 
         * @param theShort a short
         * @return the corresponding byte array
         */
        public static byte[] shortToBytes(short theShort) {
                byte[] ret = new byte[2];
                ret[0] = (byte) ((theShort >>> 8) & BYTESIZE);
                ret[1] = (byte) ((theShort) & BYTESIZE);
                return ret;
        }

        /**
         * Converts an array of bytes into an array of shorts
         * 
         * Byte order: little endian
         * 
         * @param bytes an array of bytes
         * @return the corresponding array of shorts
         */
        public static short[] bytesToShorts(byte[] bytes) {
                short[] ret = new short[bytes.length / 2];
                for (int i = 0; i < ret.length; i++) {
                        ret[i] = (short) (((bytes[i * 2] & BYTESIZE)) + ((bytes[i * 2 + 1] & BYTESIZE) << 8));
                }

                return ret;
        }

        /**
         * Converts an array of bytes into a short
         * 
         * Byte order: big endian
         * 
         * @param bytes an array of bytes
         * @return the corresponding short
         */
        public static short bytesToShort(byte[] bytes) {
                return (short) ((((bytes[0] & BYTESIZE) << 8)) + (bytes[1] & BYTESIZE));
        }

        /**
         * Converts an array of integers into an array of bytes
         * 
         * Byte order: little endian
         * 
         * @param ints an array of integers
         * @return an array of bytes
         */
        public static byte[] intsToBytes(int[] ints) {
                byte[] ret = new byte[ints.length * 4];
                for (int i = 0; i < ints.length; i++) {
                        ret[i * 4] = (byte) ((ints[i]) & BYTESIZE);
                        ret[i * 4 + 1] = (byte) ((ints[i] >>> 8) & BYTESIZE);
                        ret[i * 4 + 2] = (byte) ((ints[i] >>> 16) & BYTESIZE);
                        ret[i * 4 + 3] = (byte) ((ints[i] >>> 24) & BYTESIZE);
                }
                return ret;
        }

        /**
         * Converts an array of bytes into an array of integers
         * 
         * Byte order: little endian
         * 
         * @param bytes an array of bytes
         * @return the corresponding array of integers
         */
        public static int[] bytesToInts(byte[] bytes) {
                int[] ret = new int[bytes.length / 4];
                for (int i = 0; i < ret.length; i++) {
                        ret[i] = ((bytes[i * 4] & BYTESIZE))
                                + ((bytes[i * 4 + 1] & BYTESIZE) << 8)
                                + ((bytes[i * 4 + 2] & BYTESIZE) << 16)
                                + ((bytes[i * 4 + 3] & BYTESIZE) << 24);
                }

                return ret;
        }

        /**
         * Converts an array of floats into an array of bytes
         * 
         * Byte order: little endian
         * 
         * @param floats an array of floats
         * @return the corresponding array of bytes
         */
        public static byte[] floatsToBytes(float[] floats) {
                byte[] ret = new byte[floats.length * 4];
                for (int i = 0; i < floats.length; i++) {
                        int floatAsInt = Float.floatToIntBits(floats[i]);
                        ret[i * 4] = (byte) ((floatAsInt) & BYTESIZE);
                        ret[i * 4 + 1] = (byte) ((floatAsInt >>> 8) & BYTESIZE);
                        ret[i * 4 + 2] = (byte) ((floatAsInt >>> 16) & BYTESIZE);
                        ret[i * 4 + 3] = (byte) ((floatAsInt >>> 24) & BYTESIZE);
                }
                return ret;
        }

        /**
         * Converts a float into an array of bytes
         * 
         * Byte order: big endian
         * 
         * @param theFloat a float
         * @return the corresponding array of bytes
         */
        public static byte[] floatToBytes(float theFloat) {
                byte[] ret = new byte[4];

                int floatAsInt = Float.floatToIntBits(theFloat);
                ret[0] = (byte) ((floatAsInt >>> 24) & BYTESIZE);
                ret[1] = (byte) ((floatAsInt >>> 16) & BYTESIZE);
                ret[2] = (byte) ((floatAsInt >>> 8) & BYTESIZE);
                ret[3] = (byte) ((floatAsInt) & BYTESIZE);

                return ret;
        }

        /**
         * Converts an array of bytes into an array of floats
         * 
         * Byte order: little endian
         * 
         * @param bytes an array of bytes
         * @return the corresponding array of floats
         */
        public static float[] bytesToFloats(byte[] bytes) {
                float[] ret = new float[bytes.length / 4];
                for (int i = 0; i < ret.length; i++) {
                        int floatAsInt = ((bytes[i * 4 + 3] & BYTESIZE) << 24)
                                + ((bytes[i * 4 + 2] & BYTESIZE) << 16)
                                + ((bytes[i * 4 + 1] & BYTESIZE) << 8)
                                + ((bytes[i * 4] & BYTESIZE));
                        ret[i] = Float.intBitsToFloat(floatAsInt);
                }

                return ret;
        }

        /**
         * Converts an array of bytes into a float
         * 
         * Byte order: big endian
         * 
         * @param bytes an array of bytes
         * @return the corresponding float
         */
        public static float bytesToFloat(byte[] bytes) {
                int floatAsInt = ((bytes[0] & BYTESIZE) << 24)
                        + ((bytes[1] & BYTESIZE) << 16)
                        + ((bytes[2] & BYTESIZE) << 8)
                        + ((bytes[3] & BYTESIZE));
                return Float.intBitsToFloat(floatAsInt);
        }

        /**
         * Converts an array of longs into an array of bytes
         * 
         * Byte order: little endian
         * 
         * @param longs an array of longs
         * @return the corresponding array of bytes
         */
        public static byte[] longsToBytes(long[] longs) {
                byte[] ret = new byte[longs.length * 8];
                for (int i = 0; i < longs.length; i++) {
                        ret[i * 8] = (byte) (longs[i]);
                        ret[i * 8 + 1] = (byte) (longs[i] >>> 8);
                        ret[i * 8 + 2] = (byte) (longs[i] >>> 16);
                        ret[i * 8 + 3] = (byte) (longs[i] >>> 24);
                        ret[i * 8 + 4] = (byte) (longs[i] >>> 32);
                        ret[i * 8 + 5] = (byte) (longs[i] >>> 40);
                        ret[i * 8 + 6] = (byte) (longs[i] >>> 48);
                        ret[i * 8 + 7] = (byte) (longs[i] >>> 56);
                }

                return ret;
        }

        /**
         * Converts a long into an array of bytes
         * 
         * Byte order: big endian
         * 
         * @param theLong a long
         * @return the corresponding array of bytes
         */
        public static byte[] longToBytes(long theLong) {
                byte[] ret = new byte[8];

                ret[0] = (byte) (theLong >>> 56);
                ret[1] = (byte) (theLong >>> 48);
                ret[2] = (byte) (theLong >>> 40);
                ret[3] = (byte) (theLong >>> 32);
                ret[4] = (byte) (theLong >>> 24);
                ret[5] = (byte) (theLong >>> 16);
                ret[6] = (byte) (theLong >>> 8);
                ret[7] = (byte) (theLong);

                return ret;
        }

        /**
         * Converts an array of bytes into an array of longs
         * 
         * Byte order: little endian
         * 
         * @param bytes an array of bytes
         * @return the corresponding array of longs
         */
        public static long[] bytesToLongs(byte[] bytes) {
                long[] ret = new long[bytes.length / 8];
                for (int i = 0; i < ret.length; i++) {
                        ret[i] = (((long) bytes[i * 8 + 7] << 56)
                                + ((long) (bytes[i * 8 + 6] & BYTESIZE) << 48)
                                + ((long) (bytes[i * 8 + 5] & BYTESIZE) << 40)
                                + ((long) (bytes[i * 8 + 4] & BYTESIZE) << 32)
                                + ((long) (bytes[i * 8 + 3] & BYTESIZE) << 24)
                                + ((bytes[i * 8 + 2] & BYTESIZE) << 16)
                                + ((bytes[i * 8 + 1] & BYTESIZE) << 8)
                                + ((bytes[i * 8] & BYTESIZE)));
                }

                return ret;
        }

        /**
         * Converts an array of bytes into a long
         * 
         * Byte order: big endian
         * 
         * @param bytes an array of bytes
         * @return the corresponding long
         */
        public static long bytesToLong(byte[] bytes) {
                return (((long) bytes[0] << 56)
                                + ((long) (bytes[1] & BYTESIZE) << 48)
                                + ((long) (bytes[2] & BYTESIZE) << 40)
                                + ((long) (bytes[3] & BYTESIZE) << 32)
                                + ((long) (bytes[4] & BYTESIZE) << 24)
                                + ((bytes[5] & BYTESIZE) << 16)
                                + ((bytes[6] & BYTESIZE) << 8)
                                + ((bytes[7] & BYTESIZE)));
        }

        /**
         * Converts an array of doubles into an array of bytes
         * 
         * Byte order: little endian
         * 
         * @param doubles an array of doubles
         * @return the corresponding array of bytes
         */
        public static byte[] doublesToBytes(double[] doubles) {
                long[] longs = new long[doubles.length];
                for (int i = 0; i < doubles.length; i++) {
                        longs[i] = Double.doubleToLongBits(doubles[i]);
                }
                return longsToBytes(longs);
        }

        /**
         * Converts a double into an array of bytes
         * 
         * Byte order: big endian
         * 
         * @param theDouble a double
         * @return the corresponding array of bytes
         */
        public static byte[] doubleToBytes(double theDouble) {
                return longToBytes(Double.doubleToLongBits(theDouble));
        }

        /**
         * Converts an array of bytes into an array of doubles
         * 
         * Byte order: little endian
         * 
         * @param bytes an array of bytes
         * @return the corresponding array of doubles
         */
        public static double[] bytesToDoubles(byte[] bytes) {
                long[] inter = bytesToLongs(bytes);
                double[] ret = new double[bytes.length / 8];
                for (int i = 0; i < inter.length; i++) {
                        ret[i] = Double.longBitsToDouble(inter[i]);
                }

                return ret;
        }

        /**
         * Converts an array of bytes into a double
         * 
         * Byte order: big endian
         * 
         * @param bytes an array of double
         * @return the corresponding float
         */
        public static double byteToDouble(byte[] bytes) {
                return Double.longBitsToDouble(bytesToLong(bytes));
        }

        private ByteUtils() {
        }
}
