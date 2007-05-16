package org.gdms.sql.indexes.hashMap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class DiskPositionIterator implements PositionIterator {
	private FileChannel channel;
	private ByteBuffer buffer;
	private boolean next;

	/**
	 * Crea un nuevo DiskPositionIterator.
	 *
	 * @param channel DOCUMENT ME!
	 * @param positionCount DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public DiskPositionIterator(FileChannel channel, int positionCount,
		Object value) throws IOException {
		this.buffer = ByteBuffer.allocate(DiskIndex.RECORD_SIZE);
		this.channel = channel;

		int pos = Math.abs(value.hashCode());
		pos = (pos % positionCount);
		channel.position(DiskIndex.byteNumber(pos));
		buffer.clear();
		channel.read(buffer);
		buffer.flip();

		if (buffer.getInt() == -1) {
			next = false;
		} else {
			next = true;
		}
	}

	/**
	 * @see org.gdms.sql.indexes.hashMap.PositionIterator#hasNext()
	 */
	public boolean hasNext() {
		return next;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException
	 * @throws RuntimeException DOCUMENT ME!
	 *
	 * @see org.gdms.sql.indexes.hashMap.PositionIterator#next()
	 */
	public int next() throws IOException {
		if (!next) {
			throw new RuntimeException("No more results");
		}

		int returnValue = buffer.getInt(0);
		int nextPosition = buffer.getInt(4);

		if (nextPosition == -1) {
			next = false;
			buffer = null;
		} else {
			channel.position(DiskIndex.byteNumber(nextPosition));
			buffer.clear();
			channel.read(buffer);
			buffer.flip();
		}

		return returnValue;
	}
}
