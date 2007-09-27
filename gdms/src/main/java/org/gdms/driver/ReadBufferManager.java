package org.gdms.driver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class ReadBufferManager {

	private int bufferSize;

	private ByteBuffer buffer;

	private FileChannel channel;

	private int windowStart;

	private int positionInFile;

	public ReadBufferManager(FileChannel channel) throws IOException {
		this(channel, 1024 * 32);
	}

	public ReadBufferManager(FileChannel channel, int bufferSize)
			throws IOException {
		this.channel = channel;
		buffer = ByteBuffer.allocate(bufferSize);
		channel.position(0);
		channel.read(buffer);
		buffer.flip();
		windowStart = 0;
		this.bufferSize = bufferSize;
	}

	/**
	 * Moves the window if necessary to contain the desired byte and returns the
	 * position of the byte in the window
	 *
	 * @param bytePos
	 * @throws IOException
	 */
	private int getWindowOffset(int bytePos, int length) throws IOException {
		int desiredMin = bytePos;
		int desiredMax = desiredMin + length - 1;
		if ((desiredMin >= windowStart)
				&& (desiredMax < windowStart + buffer.capacity())) {
			return desiredMin - windowStart;
		} else {
			int bufferCapacity = Math.max(bufferSize, length);
			bufferCapacity = Math.min(bufferCapacity, (int) channel.size());
			windowStart = bytePos;

			channel.position(windowStart);
			if (buffer.capacity() != bufferCapacity) {
				ByteOrder order = buffer.order();
				buffer = ByteBuffer.allocate(bufferCapacity);
				buffer.order(order);
			} else {
				buffer.clear();
			}
			channel.read(buffer);
			buffer.flip();
			return desiredMin - windowStart;
		}
	}

	public byte getByte(int bytePos) throws IOException {
		return buffer.get(getWindowOffset(bytePos, 1));
	}

	public long getLength() throws IOException {
		return channel.size();
	}

	public void order(ByteOrder order) {
		buffer.order(order);
	}

	public int getInt(int bytePos) throws IOException {
		return buffer.getInt(getWindowOffset(bytePos, 4));
	}

	public byte get() throws IOException {
		byte ret = getByte(positionInFile);
		positionInFile += 1;
		return ret;
	}

	public int getInt() throws IOException {
		int ret = getInt(positionInFile);
		positionInFile += 4;
		return ret;
	}

	public void skip(int numBytes) throws IOException {
		positionInFile += numBytes;
	}

	public ByteBuffer get(byte[] buffer) throws IOException {
		int windowOffset = getWindowOffset(positionInFile, buffer.length);
		this.buffer.position(windowOffset);
		positionInFile += buffer.length;
		return this.buffer.get(buffer);
	}

	public ByteBuffer get(int pos, byte[] buffer) throws IOException {
		int windowOffset = getWindowOffset(pos, buffer.length);
		this.buffer.position(windowOffset);
		return this.buffer.get(buffer);
	}

	public void position(int position) {
		this.positionInFile = position;
	}

	public double getDouble() throws IOException {
		double ret = getDouble(positionInFile);
		positionInFile += 8;
		return ret;
	}

	public double getDouble(int bytePos) throws IOException {
		return buffer.getDouble(getWindowOffset(bytePos, 8));
	}

	public boolean isEOF() throws IOException {
		return (buffer.remaining() == 0)
				&& (windowStart + buffer.capacity() == channel.size());
	}
}
