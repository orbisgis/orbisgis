package org.gdms.driver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class WriteBufferManager {

	private static final int BUFFER_SIZE = 1024 * 128;

	private FileChannel channel;

	private ByteBuffer buffer;

	public WriteBufferManager(FileChannel channel) throws IOException {
		this.channel = channel;
		buffer = ByteBuffer.allocate(BUFFER_SIZE);
	}

	public void put(byte b) throws IOException {
		prepareToAddBytes(1);
		buffer.put(b);
	}

	private void prepareToAddBytes(int numBytes) throws IOException {
		if (buffer.remaining() < numBytes) {
			buffer.flip();
			channel.write(buffer);

			int bufferCapacity = Math.max(BUFFER_SIZE, numBytes);
			if (bufferCapacity != buffer.capacity()) {
				ByteOrder order = buffer.order();
				buffer = ByteBuffer.allocate(bufferCapacity);
				buffer.order(order);
			} else {
				buffer.clear();
			}
		}
	}

	public void put(byte[] bs) throws IOException {
		prepareToAddBytes(bs.length);
		buffer.put(bs);
	}

	public void flush() throws IOException {
		buffer.flip();
		channel.write(buffer);
	}

	public void order(ByteOrder order) {
		this.buffer.order(order);
	}

	public void putInt(int value) throws IOException {
		prepareToAddBytes(4);
		buffer.putInt(value);
	}

	public void putDouble(double d) throws IOException {
		prepareToAddBytes(8);
		buffer.putDouble(d);
	}

}
