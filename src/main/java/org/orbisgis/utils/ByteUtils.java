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

public class ByteUtils {

	public static int bytesToInt(byte[] b) {
		return ((b[3] & 0xff) << 0) + ((b[2] & 0xff) << 8)
				+ ((b[1] & 0xff) << 16) + ((b[0] & 0xff) << 24);
	}

	public static byte[] intToBytes(int v) {
		byte[] b = new byte[4];
		b[0] = (byte) ((v >>> 24) & 0xFF);
		b[1] = (byte) ((v >>> 16) & 0xFF);
		b[2] = (byte) ((v >>> 8) & 0xFF);
		b[3] = (byte) ((v >>> 0) & 0xFF);

		return b;
	}

	public static byte[] shortsToBytes(short[] shorts) {
		byte[] ret = new byte[shorts.length * 2];
		for (int i = 0; i < shorts.length; i++) {
			ret[i * 2] = (byte) ((shorts[i] >>> 0) & 0xFF);
			ret[i * 2 + 1] = (byte) ((shorts[i] >>> 8) & 0xFF);
		}

		return ret;
	}

	public static short[] bytesToShorts(byte[] bytes) {
		short[] ret = new short[bytes.length / 2];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (short) (((bytes[i * 2] & 0xff) << 0) + ((bytes[i * 2 + 1] & 0xff) << 8));
		}

		return ret;
	}

	public static byte[] intsToBytes(int[] ints) {
		byte[] ret = new byte[ints.length * 4];
		for (int i = 0; i < ints.length; i++) {
			ret[i * 4] = (byte) ((ints[i] >>> 0) & 0xFF);
			ret[i * 4 + 1] = (byte) ((ints[i] >>> 8) & 0xFF);
			ret[i * 4 + 2] = (byte) ((ints[i] >>> 16) & 0xFF);
			ret[i * 4 + 3] = (byte) ((ints[i] >>> 24) & 0xFF);
		}
		return ret;
	}

	public static int[] bytesToInts(byte[] bytes) {
		int[] ret = new int[bytes.length / 4];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ((bytes[i * 4] & 0xff) << 0)
					+ ((bytes[i * 4 + 1] & 0xff) << 8)
					+ ((bytes[i * 4 + 2] & 0xff) << 16)
					+ ((bytes[i * 4 + 3] & 0xff) << 24);
		}

		return ret;
	}

	public static byte[] floatsToBytes(float[] floats) {
		byte[] ret = new byte[floats.length * 4];
		for (int i = 0; i < floats.length; i++) {
			int floatAsInt = Float.floatToIntBits(floats[i]);
			ret[i * 4] = (byte) ((floatAsInt >>> 0) & 0xFF);
			ret[i * 4 + 1] = (byte) ((floatAsInt >>> 8) & 0xFF);
			ret[i * 4 + 2] = (byte) ((floatAsInt >>> 16) & 0xFF);
			ret[i * 4 + 3] = (byte) ((floatAsInt >>> 24) & 0xFF);
		}
		return ret;
	}

	public static float[] bytesToFloats(byte[] bytes) {
		float[] ret = new float[bytes.length / 4];
		for (int i = 0; i < ret.length; i++) {
			int floatAsInt = ((bytes[i * 4 + 3] & 0xff) << 24)
					+ ((bytes[i * 4 + 2] & 0xff) << 16)
					+ ((bytes[i * 4 + 1] & 0xff) << 8)
					+ ((bytes[i * 4] & 0xff) << 0);
			ret[i] = Float.intBitsToFloat(floatAsInt);
		}

		return ret;
	}

}
