/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
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
package org.gdms.data.indexes.rtree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.io.ParseException;

import org.gdms.data.indexes.tree.IndexVisitor;
import org.gdms.data.indexes.tree.Tree;
import org.gdms.driver.ReadWriteBufferManager;

public final class DiskRTree implements Tree<Envelope> {

        private static final byte LEAF = 2;
        private static final byte INTERIOR_NODE = 1;
        private int nodeBlockSize = 256;
        private RandomAccessFile fis;
        private ReadWriteBufferManager buffer;
        private long rootAddress;
        private int n;
        private int numElements = 0;
        private SortedSet<Long> emptyBlocks;
        private RTreeNode root;
        private Map<Long, RTreeNode> cache;
        private boolean inMemory;
        private long addressesSequence = 0;
        private boolean updateRowNumbers;

        public DiskRTree(int n, int nodeBlockSize) throws IOException {
                this(n, nodeBlockSize, true);
        }

        public DiskRTree(int n, int nodeBlockSize, boolean updateRowNumbers)
                throws IOException {
                this.n = n;
                this.nodeBlockSize = nodeBlockSize;
                inMemory = true;
                cache = new HashMap<Long, RTreeNode>();
                root = createLeaf(0, -1);
                this.updateRowNumbers = updateRowNumbers;
        }

        public DiskRTree() throws IOException {
                this(255, 1024, false);
        }

        @Override
        public void newIndex(File file) throws IOException {
                if (file.exists()) {
                        throw new IOException("File already exists");
                } else {
                        inMemory = false;
                        fis = new RandomAccessFile(file, "rw");
                        buffer = new ReadWriteBufferManager(fis.getChannel(),
                                64 * nodeBlockSize);
                        emptyBlocks = new TreeSet<Long>();
                        rootAddress = 28;
                        writeHeader(-1);
                        cache = new HashMap<Long, RTreeNode>();
                        root = createLeaf(0, -1);
                }
        }

        public RTreeLeaf createLeaf(long baseAddress, long parentAddress)
                throws IOException {
                long position = getEmptyBlock(baseAddress);
                RTreeLeaf ret = new RTreeLeaf(this, position, -1);
                if (!inMemory) {
                        writeNodeAt(position, ret);
                }
                cache.put(position, ret);
                return ret;
        }

        public RTreeInteriorNode createInteriorNode(long baseAddress, long parentAddress)
                throws IOException {
                long position = getEmptyBlock(baseAddress);
                RTreeInteriorNode ret = new RTreeInteriorNode(this, position, parentAddress);
                if (!inMemory) {
                        writeNodeAt(position, ret);
                }
                cache.put(position, ret);
                return ret;
        }

        public AbstractRTreeNode createInteriorNode(long baseAddress, long parentAddress,
                RTreeNode leftChild, RTreeNode rightChild) throws IOException {
                long position = getEmptyBlock(baseAddress);
                AbstractRTreeNode ret = new RTreeInteriorNode(this, position, parentAddress,
                        leftChild, rightChild);
                if (!inMemory) {
                        writeNodeAt(position, ret);
                }
                cache.put(position, ret);
                return ret;
        }

        @Override
        public void openIndex(File file) throws IOException {
                if (!file.exists()) {
                        throw new IOException("The file " + file + " does not exist");
                } else {
                        inMemory = false;
                        fis = new RandomAccessFile(file, "rw");
                        FileChannel channel = fis.getChannel();
                        buffer = new ReadWriteBufferManager(channel, 64 * nodeBlockSize);
                        readHeader();
                        cache = new HashMap<Long, RTreeNode>();
                        root = readNodeAt(rootAddress);
                }
        }

        private long getEmptyBlock(long baseAddress) throws IOException {
                if (inMemory) {
                        addressesSequence++;
                        return addressesSequence;
                } else {
                        SortedSet<Long> nearestEmptyBlock = emptyBlocks.tailSet(baseAddress);
                        if (!nearestEmptyBlock.isEmpty()) {
                                long ret = nearestEmptyBlock.first();
                                emptyBlocks.remove(ret);
                                return ret;
                        } else {
                                // Create a new empty node at the end of the file
                                return buffer.getEOFPosition();
                        }
                }
        }

        /**
         * @param inputPosition
         * @param inputNodeBytes
         * @param inputBlockEnd
         * the latest available byte in this node
         * @throws IOException
         */
        private void writeNodeBytes(long position, byte[] nodeBytes, long blockEnd)
                throws IOException {
                long inputPosition = position;
                long inputBlockEnd = blockEnd;
                byte[] inputNodeBytes = nodeBytes;

                while (true) {
                        long nodeBytesStartPosition = inputPosition + 12;
                        byte[] remaining = new byte[0];
                        byte[] bytesInThisBlock = inputNodeBytes;
                        if (nodeBytesStartPosition + inputNodeBytes.length > inputBlockEnd) {
                                long size = inputBlockEnd - nodeBytesStartPosition + 1;
                                if (size < Integer.MAX_VALUE) {
                                        bytesInThisBlock = new byte[(int) size];
                                        System.arraycopy(inputNodeBytes, 0, bytesInThisBlock, 0,
                                                bytesInThisBlock.length);
                                        remaining = new byte[inputNodeBytes.length - bytesInThisBlock.length];
                                        System.arraycopy(inputNodeBytes, bytesInThisBlock.length, remaining, 0,
                                                inputNodeBytes.length - bytesInThisBlock.length);
                                } else {
                                        throw new IOException("this array is quite long, I think.");
                                }
                        }

                        buffer.position(inputPosition);
                        // Write the direction of the extension node. -1 is written now but it's
                        // fixed at the end of the method
                        buffer.putLong(-1);

                        // Write the size of the node bytes in this block
                        buffer.putInt(bytesInThisBlock.length);

                        // Write the bytes
                        buffer.put(bytesInThisBlock);

                        // Fill the rest of the block
                        long sizebis = inputBlockEnd - buffer.getPosition() + 1;
                        if (sizebis < Integer.MAX_VALUE) {
                                byte[] fillBytes = new byte[(int) sizebis];
                                buffer.put(fillBytes);

                                // Loop to write the extension nodes
                                // This was refactored to prevent Stack overflows when writing many bytes
                                // into small blocks
                                if (remaining.length > 0) {
                                        long extensionBlock = getEmptyBlock(inputPosition);
                                        buffer.position(inputPosition);
                                        buffer.putLong(extensionBlock);
                                        inputPosition = extensionBlock;
                                        inputNodeBytes = remaining;
                                        inputBlockEnd = extensionBlock + nodeBlockSize - 1;
                                } else {
                                        break;
                                }
                        } else {
                                throw new IOException("This array is way too long");
                        }
                }
        }

        private byte[] readNodeBytes(long position) throws IOException {
                long inputPosition = position;
                List<byte[]> allByteArrays = new ArrayList<byte[]>();
                int concatSize = 0;

                while (true) {
                        buffer.position(inputPosition);
                        // Read the address of the extension node
                        long nextNode = buffer.getLong();
                        // Read the size of the node bytes in this block
                        int blockBytesLength = buffer.getInt();
                        concatSize += blockBytesLength;
                        // Read all the bytes
                        byte[] thisBlockBytes = new byte[blockBytesLength];
                        buffer.get(thisBlockBytes);
                        allByteArrays.add(thisBlockBytes);

                        if (nextNode == -1) {
                                // no extension node
                                break;
                        } else {
                                inputPosition = nextNode;
                        }
                }

                byte[] concatBytes = new byte[concatSize];
                int desPos = 0;
                for (byte[] b : allByteArrays) {
                        System.arraycopy(b, 0, concatBytes, desPos, b.length);
                        desPos += b.length;
                }
                return concatBytes;
        }

        public void writeNodeAt(long position, RTreeNode node) throws IOException {
                buffer.position(position);
                if (node instanceof RTreeLeaf) {
                        RTreeLeaf leaf = (RTreeLeaf) node;
                        buffer.put(LEAF);
                        buffer.putLong(leaf.getParentAddress());
                        byte[] nodeBytes = node.getBytes();
                        writeNodeBytes(buffer.getPosition(), nodeBytes, position + nodeBlockSize
                                - 1);

                } else if (node instanceof RTreeInteriorNode) {
                        AbstractRTreeNode interiorNode = (AbstractRTreeNode) node;
                        buffer.put(INTERIOR_NODE);
                        buffer.putLong(interiorNode.getParentAddress());
                        byte[] nodeBytes = interiorNode.getBytes();
                        writeNodeBytes(buffer.getPosition(), nodeBytes, position + nodeBlockSize
                                - 1);
                } else {
                        throw new IllegalArgumentException("Unrecognized node type " + node.getClass().getName());
                }

        }

        /**
         * @param parentAddress
         * @return
         * @throws IOException
         */
        RTreeNode readNodeAt(long nodeAddress) throws IOException {
                RTreeNode node = cache.get(nodeAddress);
                if (node == null) {
                        buffer.position(nodeAddress);
                        byte blockType = buffer.get();

                        // Read the direction of the parent and neighbours
                        long parentAddress = buffer.getLong();
                        // Read the bytes of the node
                        byte[] nodeBytes = readNodeBytes(buffer.getPosition());

                        switch (blockType) {
                                case LEAF:
                                        // Return the instance
                                        try {
                                                node = RTreeLeaf.createLeafFromBytes(this, nodeAddress,
                                                        parentAddress, n, nodeBytes);
                                        } catch (ParseException e) {
                                                throw new IOException("Cannot understand the "
                                                        + "geometry bytes", e);
                                        }
                                        break;
                                case INTERIOR_NODE:
                                        // Return the instance
                                        node = RTreeInteriorNode.createInteriorNodeFromBytes(this,
                                                nodeAddress, parentAddress, n, nodeBytes);
                                        break;
                                default:
                                        throw new IOException("Cannot understand block type:"
                                                + blockType);
                        }

                        cache.put(nodeAddress, node);
                }

                return node;
        }

        void readHeader() throws IOException {
                nodeBlockSize = buffer.getInt();
                numElements = buffer.getInt();
                n = buffer.getInt();
                long emptyBlockDir = buffer.getLong();
                rootAddress = buffer.getLong();
                if (emptyBlockDir != -1) {
                        readEmptyBlockList(emptyBlockDir);
                }
        }

        private void readEmptyBlockList(long emptyBlockAddress) throws IOException {
                emptyBlocks = new TreeSet<Long>();
                byte[] bytes = readNodeBytes(emptyBlockAddress);
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                DataInputStream dis = new DataInputStream(bis);

                long emptyBlock = dis.readLong();
                emptyBlocks.add(emptyBlock);

                dis.close();
        }

        private void writeEmptyBlockList(long emptyBlockAddress) throws IOException {
                // Remove the empty blocks at the end of the file
                if (emptyBlocks.size() > 0) {
                        long lastBlock = emptyBlocks.last();
                        if (lastBlock + nodeBlockSize >= buffer.getEOFPosition()) {
                                // It's the last
                                while (emptyBlocks.contains(lastBlock)) {
                                        emptyBlocks.remove(lastBlock);
                                        lastBlock -= nodeBlockSize;
                                }
                        }
                }

                // write the remaining blocks
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                for (Long emptyBlock : emptyBlocks) {
                        dos.writeLong(emptyBlock);
                }
                dos.close();
                writeNodeBytes(emptyBlockAddress, bos.toByteArray(), emptyBlockAddress
                        + nodeBlockSize - 1);
        }

        void writeHeader(long emptyBlockAddress) throws IOException {
                buffer.position(0);
                buffer.putInt(nodeBlockSize);
                buffer.putInt(numElements);
                buffer.putInt(n);
                buffer.putLong(emptyBlockAddress);
                buffer.putLong(rootAddress);
        }

        @Override
        public void save() throws IOException {
                if (inMemory) {
                        throw new UnsupportedOperationException("Memory "
                                + "indexes cannot be saved");
                } else {
                        long emptyBlockAddress = getEmptyBlock(0);
                        writeEmptyBlockList(emptyBlockAddress);
                        writeHeader(emptyBlockAddress);
                        root.save();
                        buffer.flush();

                        // We throw all the tree we have in memory
                        root = readNodeAt(rootAddress);
                }
        }

        @Override
        public void close() throws IOException {
                if (inMemory) {
                        throw new UnsupportedOperationException("Memory "
                                + "indexes cannot be closed");
                } else {
                        save();
                        // Free resources
                        buffer.close();
                        fis.close();
                }
        }

        @Override
        public void checkTree() throws IOException {
                root.checkTree();

                if (size() != root.getAllValues().length) {
                        throw new IllegalStateException("size inconsistency");
                }
        }

        @Override
        public boolean delete(Envelope v, int row) throws IOException {
                if (root.delete(v, row)) {
                        root = root.getNewRoot();
                        rootAddress = root.getAddress();

                        numElements--;

                        updateRows(row, -1);
                        return true;
                }
                return false;
        }

        @Override
        public Envelope[] getAllValues() throws IOException {
                return root.getAllValues();
        }

        public void updateRows(int startRow, int offset) throws IOException {
                if (updateRowNumbers && (startRow < size())) {
                        root.updateRows(startRow, offset);
                }
        }

        @Override
        public void insert(Envelope v, int rowIndex) throws IOException {
                // Check we update rows and it's not added to the last position
                updateRows(rowIndex, 1);
                root.insert(v, rowIndex);
                if (root.getParent() != null) {
                        root = root.getParent();
                        rootAddress = root.getAddress();
                }

                numElements++;
        }

        @Override
        public int size() {
                return numElements;
        }

        /**
         * @see org.gdms.data.indexes.btree.BTree#toString()
         */
        @Override
        public String toString() {
                return root.toString();
        }

        public int getN() {
                return n;
        }

        public int getEmptyBlocks() {
                return emptyBlocks.size();
        }

        public void removeNode(long nodeAddress) throws IOException {
                cache.remove(nodeAddress);
                emptyBlocks.add(nodeAddress);
                buffer.position(nodeAddress + 9);
                long extensionBlock = buffer.getLong();
                if (extensionBlock != -1) {
                        deleteExtensionBlock(extensionBlock);
                }
        }

        private void deleteExtensionBlock(long extensionBlockAddress) throws IOException {
                emptyBlocks.add(extensionBlockAddress);
                buffer.position(extensionBlockAddress);
                long nextExtensionBlock = buffer.getLong();
                if (nextExtensionBlock != -1) {
                        deleteExtensionBlock(nextExtensionBlock);
                }
        }

        @Override
        public int[] query(Envelope value) throws IOException {
                return root.query(value);
        }

        @Override
        public void query(Envelope value, IndexVisitor<Envelope> visitor) throws IOException {
                if (visitor != null) {
                        root.query(value, visitor);
                }
        }
}
