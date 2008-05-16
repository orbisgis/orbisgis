package org.orbisgis.utils;


public class ByteUtils {

	public static int bytesToInt(byte[] b) {
		return ((b[0] << 24) + (b[1] << 16) + (b[2] << 8) + (b[3] << 0));
	}

}
