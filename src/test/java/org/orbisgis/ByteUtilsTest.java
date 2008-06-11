package org.orbisgis;

import junit.framework.TestCase;

import org.orbisgis.utils.ByteUtils;

public class ByteUtilsTest extends TestCase {

	public void testShortsBytes() throws Exception {
		short[] shorts = new short[] { 1, 2, 3, 5, 1500, Short.MAX_VALUE };
		byte[] shortBytes = ByteUtils.shortsToBytes(shorts);
		short[] shorts2 = ByteUtils.bytesToShorts(shortBytes);
		assertTrue(shorts.length == shorts2.length);
		for (int i = 0; i < shorts2.length; i++) {
			assertTrue(shorts[i] == shorts2[i]);
		}
	}

	public void testFloats2Bytes() throws Exception {
		float[] floats = new float[] { 4f, 2857685.235f, 3f, 356.234f };
		byte[] floatBytes = ByteUtils.floatsToBytes(floats);
		float[] floats2 = ByteUtils.bytesToFloats(floatBytes);
		assertTrue(floats.length == floats2.length);
		for (int i = 0; i < floats2.length; i++) {
			assertTrue(floats[i] == floats2[i]);
		}
	}

	public void testInts2Bytes() throws Exception {
		int[] ints = new int[] { 1, 2, 3, 4, 1500 };
		byte[] intBytes = ByteUtils.intsToBytes(ints);
		int[] ints2 = ByteUtils.bytesToInts(intBytes);
		assertTrue(ints.length == ints2.length);
		for (int i = 0; i < ints2.length; i++) {
			assertTrue(ints[i] == ints2[i]);
		}
	}

	public void testInt2Byte() throws Exception {
		int i = 1237610;
		byte[] bytes = ByteUtils.intToBytes(i);
		assertTrue(ByteUtils.bytesToInt(bytes) == i);
	}
}
