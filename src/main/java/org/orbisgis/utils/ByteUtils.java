package org.orbisgis.utils;

public class ByteUtils {

	public static int bytesToInt(byte[] b) {
		return ((b[0] << 24) + (b[1] << 16) + (b[2] << 8) + (b[3] << 0));
	}

	public static byte[] intToBytes(int v) {
		byte[] b = new byte[4];
		b[0] = (byte) ((v >>> 24) & 0xFF);
		b[1] = (byte) ((v >>> 16) & 0xFF);
		b[2] = (byte) ((v >>> 8) & 0xFF);
		b[3] = (byte) ((v >>> 0) & 0xFF);

		return b;
	}

}
