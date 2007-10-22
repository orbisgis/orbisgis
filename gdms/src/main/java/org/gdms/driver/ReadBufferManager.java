/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
