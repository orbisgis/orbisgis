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
