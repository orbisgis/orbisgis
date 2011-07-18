/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer,
Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.driver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/***
 * 
 *
 */
public final class ReadWriteBufferManager {

        private int bufferSize;
        private ByteBuffer buffer;
        private FileChannel channel;
        private long windowStart;
        private long positionInFile;
        private boolean bufferModified;
        private int highestModification;

        /**
         * Instantiates a ReadBufferManager to read the specified channel
         *
         * @param channel
         * @throws IOException
         */
        public ReadWriteBufferManager(FileChannel channel) throws IOException {
                this(channel, 1024 * 32);
        }

        /**
         * Instantiates a ReadBufferManager to read the specified channel. The
         * specified bufferSize is the size of the channel content cached in memory
         *
         * @param channel
         * @param bufferSize
         * @throws IOException
         */
        public ReadWriteBufferManager(FileChannel channel, int bufferSize)
                throws IOException {
                this.channel = channel;
                buffer = ByteBuffer.allocate(bufferSize);
                this.bufferSize = bufferSize;
                readIntoBuffer(0, bufferSize);
                windowStart = 0;
        }

        private void readIntoBuffer(long position, int bufferSize) throws IOException {
                channel.position(position);
                int numBytes = this.channel.read(buffer);
                if (numBytes == -1) {
                        numBytes = 0;
                }
                if (numBytes < bufferSize) {
                        byte[] fillBytes = new byte[bufferSize - numBytes];
                        buffer.put(fillBytes);
                }
                buffer.flip();
        }

        /**
         * Moves the window if necessary to contain the desired byte and returns the
         * position of the byte in the window
         *
         * @param bytePos
         * @throws IOException
         */
        private int getWindowOffset(long bytePos, int length) throws IOException {
                long desiredMin = bytePos;
                long desiredMax = desiredMin + length - 1;
                if ((desiredMin >= windowStart)
                        && (desiredMax < windowStart + buffer.capacity())) {
                        long res = desiredMin - windowStart;
                        if (res < Integer.MAX_VALUE) {
                                return (int) res;
                        } else {
                                throw new IOException("this buffer is quite large...");
                        }
                } else {
                        // Write back the buffer
                        if (bufferModified) {
                                flush();
                        }

                        // Calculate buffer capacity
                        int bufferCapacity = Math.max(bufferSize, length);
                        // bufferCapacity = Math.min(bufferCapacity, (int) channel.size());

                        windowStart = bytePos;

                        // Get a buffer of the suitable size
                        if (buffer.capacity() != bufferCapacity) {
                                ByteOrder order = buffer.order();
                                buffer = ByteBuffer.allocate(bufferCapacity);
                                buffer.order(order);
                        } else {
                                buffer.clear();
                        }

                        // Read the buffer
                        readIntoBuffer(windowStart, bufferCapacity);

                        // We won't write back the buffer so far
                        bufferModified = false;
                        highestModification = 0;

                        return (int) (desiredMin - windowStart);
                }
        }

        /**
         * Gets the byte value at the specified position
         *
         * @param bytePos
         * @return
         * @throws IOException
         */
        public byte getByte(long bytePos) throws IOException {
                int windowOffset = getWindowOffset(bytePos, 1);
                return buffer.get(windowOffset);
        }

        /**
         * Gets the size of the channel
         *
         * @return
         * @throws IOException
         */
        public long getLength() throws IOException {
                return channel.size();
        }

        /**
         * Specifies the byte order. One of the constants in {@link ByteBuffer}
         *
         * @param order
         */
        public void order(ByteOrder order) {
                buffer.order(order);
        }

        /**
         * Gets the int value at the specified position
         *
         * @param bytePos
         * @return
         * @throws IOException
         */
        public int getInt(long bytePos) throws IOException {
                int windowOffset = getWindowOffset(bytePos, 4);
                return buffer.getInt(windowOffset);
        }

        /**
         * Gets the long value at the specified position
         *
         * @param bytePos
         * @return
         * @throws IOException
         */
        public long getLong(long bytePos) throws IOException {
                int windowOffset = getWindowOffset(bytePos, 8);
                return buffer.getLong(windowOffset);
        }

        /**
         * Gets the long value at the current position
         *
         * @return
         * @throws IOException
         */
        public long getLong() throws IOException {
                long ret = getLong(positionInFile);
                positionInFile += 8;
                return ret;
        }

        /**
         * Gets the byte value at the current position
         *
         * @return
         * @throws IOException
         */
        public byte get() throws IOException {
                byte ret = getByte(positionInFile);
                positionInFile += 1;
                return ret;
        }

        /**
         * Gets the int value at the current position
         *
         * @return
         * @throws IOException
         */
        public int getInt() throws IOException {
                int ret = getInt(positionInFile);
                positionInFile += 4;
                return ret;
        }

        /**
         * skips the specified number of bytes from the current position in the
         * channel
         *
         * @param numBytes
         * @throws IOException
         */
        public void skip(int numBytes) throws IOException {
                positionInFile += numBytes;
        }

        /**
         * Gets the byte[] value at the current position
         *
         * @param buffer
         * @return
         * @throws IOException
         */
        public ByteBuffer get(byte[] buffer) throws IOException {
                int windowOffset = getWindowOffset(positionInFile, buffer.length);
                this.buffer.position(windowOffset);
                positionInFile += buffer.length;
                return this.buffer.get(buffer);
        }

        /**
         * Gets the byte[] value at the specified position
         *
         * @param pos
         * @param buffer
         * @return
         * @throws IOException
         */
        public ByteBuffer get(int pos, byte[] buffer) throws IOException {
                int windowOffset = getWindowOffset(pos, buffer.length);
                this.buffer.position(windowOffset);
                return this.buffer.get(buffer);
        }

        /**
         * Moves the current position to the specified one
         *
         * @param position
         */
        public void position(long position) {
                this.positionInFile = position;
        }

        /**
         * Gets the double value at the specified position
         *
         * @return
         * @throws IOException
         */
        public double getDouble() throws IOException {
                double ret = getDouble(positionInFile);
                positionInFile += 8;
                return ret;
        }

        /**
         * Gets the double value at the specified position
         *
         * @param bytePos
         * @return
         * @throws IOException
         */
        public double getDouble(long bytePos) throws IOException {
                int windowOffset = getWindowOffset(bytePos, 8);
                return buffer.getDouble(windowOffset);
        }

        /**
         * If the current position is at the end of the channel
         *
         * @return
         * @throws IOException
         */
        public boolean isEOF() throws IOException {
                return (buffer.remaining() == 0)
                        && (windowStart + buffer.capacity() == channel.size());
        }

        public long getPosition() {
                return positionInFile;
        }

        public void putInt(int value) throws IOException {
                int windowOffset = getWindowOffset(getPosition(), 4);
                buffer.putInt(windowOffset, value);
                positionInFile += 4;
                modificationDone();
        }

        public void putDouble(double value) throws IOException {
                int windowOffset = getWindowOffset(getPosition(), 8);
                buffer.putDouble(windowOffset, value);
                positionInFile += 8;
                modificationDone();
        }

        public void putLong(long value) throws IOException {
                int windowOffset = getWindowOffset(getPosition(), 8);
                buffer.putLong(windowOffset, value);
                positionInFile += 8;
                modificationDone();
        }

        public void put(byte value) throws IOException {
                int windowOffset = getWindowOffset(getPosition(), 1);
                buffer.put(windowOffset, value);
                positionInFile += 1;
                modificationDone();
        }

        public void put(byte[] bytes) throws IOException {
                int windowOffset = getWindowOffset(getPosition(), bytes.length);
                buffer.position(windowOffset);
                buffer.put(bytes);
                positionInFile += bytes.length;
                modificationDone();
        }

        private void modificationDone() throws IOException {
                bufferModified = true;
                long res = positionInFile - windowStart;
                if (res < Integer.MAX_VALUE) {
                        int bufferModification = (int) res;
                        if (bufferModification > highestModification) {
                                highestModification = bufferModification;
                        }
                } else {
                        throw new IOException("how can this buffer be so large ?");
                }
        }

        public void flush() throws IOException {
                buffer.position(0);
                buffer.limit(highestModification);
                channel.position(windowStart);
                channel.write(buffer);
                buffer.clear();
        }
        
        public void close() throws IOException {
                flush();
                channel.force(false);
        }

        public long getEOFPosition() throws IOException {
                long fileSize = channel.size();
                long highestModificationInFile = windowStart + highestModification;

                return Math.max(fileSize, highestModificationInFile);
        }
}
