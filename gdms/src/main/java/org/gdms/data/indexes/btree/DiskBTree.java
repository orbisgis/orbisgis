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
package org.gdms.data.indexes.btree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.gdms.data.values.Value;
import org.gdms.driver.ReadWriteBufferManager;

public final class DiskBTree implements BTree {

        private static final byte LEAF = 2;
        private static final byte INTERIOR_NODE = 1;
        private int nodeBlockSize = 256;
        private RandomAccessFile fis;
        private ReadWriteBufferManager buffer;
        //The adress of the root of this tree
        private long rootAddress;
        //The order of this BTree
        private int n;
        private int numElements = 0;
        private SortedSet<Long> emptyBlocks;
        private BTreeNode root;
        /*
         * Thie map is an indexed cache between the nodes of this tree and their adress
         * in the file we are reading in.
         */
        private Map<Long, BTreeNode> cache;
        private boolean inMemory;
        private int addressesSequence = 0;
        private boolean updateRowNumbers;

        public DiskBTree(int n, int nodeBlockSize) throws IOException {
                this(n, nodeBlockSize, true);
        }

        public DiskBTree(int n, int nodeBlockSize, boolean updateRowNumbers)
                throws IOException {
                this.n = n;
                this.updateRowNumbers = updateRowNumbers;
                this.nodeBlockSize = nodeBlockSize;
                inMemory = true;
                cache = new HashMap<Long, BTreeNode>();
                root = createLeaf(0, -1);
        }

        public DiskBTree() throws IOException {
                this(255, 1024);
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
                        cache = new HashMap<Long, BTreeNode>();
                        root = createLeaf(0, -1);
                }
        }

        public BTreeLeaf createLeaf(long baseAddress, long parentAddress)
                throws IOException {
                long dir = getEmptyBlock(baseAddress);
                BTreeLeaf ret = new BTreeLeaf(this, dir, -1);
                if (!inMemory) {
                        writeNodeAt(dir, ret);
                }
                cache.put(dir, ret);
                return ret;
        }

        public BTreeInteriorNode createInteriorNode(long baseAddress, long parentAddress)
                throws IOException {
                long dir = getEmptyBlock(baseAddress);
                BTreeInteriorNode ret = new BTreeInteriorNode(this, dir, parentAddress);
                if (!inMemory) {
                        writeNodeAt(dir, ret);
                }
                cache.put(dir, ret);
                return ret;
        }

        public BTreeInteriorNode createInteriorNode(long baseAddress, long parentAddress,
                BTreeNode leftChild, BTreeNode rightChild) throws IOException {
                long dir = getEmptyBlock(baseAddress);
                BTreeInteriorNode ret = new BTreeInteriorNode(this, dir, parentAddress,
                        leftChild, rightChild);
                if (!inMemory) {
                        writeNodeAt(dir, ret);
                }
                cache.put(dir, ret);
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
                        cache = new HashMap<Long, BTreeNode>();
                        root = readNodeAt(rootAddress);
                }
        }

        /**
         * Gets the address of an empty block. It is the lowest address greater than
         * baseAddressection
         * @param baseAddress
         * @return
         * @throws IOException
         */
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
         * Try to write the array nodeBytes at the position position in the output.
         * @param position
         * @param nodeBytes
         * @param blockEnd
         *            the latest available byte in this node
         * @throws IOException
         */
        private void writeNodeBytes(long position, byte[] nodeBytes, long blockEnd)
                throws IOException {
                long nodeBytesStartPosition = position + 12;
                byte[] remaining = new byte[0];
                byte[] bytesInThisBlock = nodeBytes;
                if (nodeBytesStartPosition + nodeBytes.length > blockEnd) {
                        long size = blockEnd - nodeBytesStartPosition + 1;
                        if (size < (long) Integer.MAX_VALUE) {
                                bytesInThisBlock = new byte[(int) size];
                        } else {
                                throw new IOException("this buffer is too large, isn't it ?");
                        }
                        System.arraycopy(nodeBytes, 0, bytesInThisBlock, 0,
                                bytesInThisBlock.length);
                        remaining = new byte[nodeBytes.length - bytesInThisBlock.length];
                        System.arraycopy(nodeBytes, bytesInThisBlock.length, remaining, 0,
                                nodeBytes.length - bytesInThisBlock.length);
                }

                buffer.position(position);
                // Write the address of the extension node. -1 is written now but it's
                // fixed at the end of the method
                buffer.putLong(-1);
                // Write the size of the node bytes in this block
                buffer.putInt(bytesInThisBlock.length);
                // Write the bytes
                buffer.put(bytesInThisBlock);

                // Fill the rest of the block
                long sizebis = blockEnd - buffer.getPosition() + 1;
                if (sizebis < (long) Integer.MAX_VALUE) {
                        byte[] fillBytes = new byte[(int) sizebis];
                        buffer.put(fillBytes);

                        // Write the extension node
                        if (remaining.length > 0) {
                                long extensionBlock = getEmptyBlock(position);
                                buffer.position(position);
                                buffer.putLong(extensionBlock);
                                writeNodeBytes(extensionBlock, remaining, extensionBlock
                                        + nodeBlockSize - 1);
                        }
                } else {
                        throw new IOException("this buffer is too large, isn't it?");
                }
        }

        /**
         * Read the bytes that are associated to the node at the address dir.
         * @param position
         * @return
         * @throws IOException
         */
        private byte[] readNodeBytes(long position) throws IOException {
                buffer.position(position);
                // Read the address of the extension node
                long nextNode = buffer.getLong();
                // Read the size of the node bytes in this block
                int blockBytesLength = buffer.getInt();
                // Read all the bytes
                byte[] thisBlockBytes = new byte[blockBytesLength];
                buffer.get(thisBlockBytes);
                byte[] extensionBytes = new byte[0];
                if (nextNode != -1) {
                        extensionBytes = readNodeBytes(nextNode);
                }
                byte[] nodeBytes = new byte[thisBlockBytes.length
                        + extensionBytes.length];
                System.arraycopy(thisBlockBytes, 0, nodeBytes, 0,
                        thisBlockBytes.length);
                System.arraycopy(extensionBytes, 0, nodeBytes, thisBlockBytes.length,
                        extensionBytes.length);
                return nodeBytes;
        }

        /**
         * Writes the node node at the address dir.
         * @param position
         * @param node
         * @throws IOException
         */
        public void writeNodeAt(long position, BTreeNode node) throws IOException {
                buffer.position(position);
                if (node instanceof BTreeLeaf) {
                        BTreeLeaf leaf = (BTreeLeaf) node;
                        buffer.put(LEAF);
                        //We write the adress of the parent.
                        buffer.putLong(leaf.getParentAddress());
                        //We retrieve the datas in this node...
                        byte[] nodeBytes = node.getBytes();
                        //...and we write them to the disk.
                        writeNodeBytes(buffer.getPosition(), nodeBytes, position + nodeBlockSize - 1);

                } else if (node instanceof BTreeInteriorNode) {
                        BTreeInteriorNode interiorNode = (BTreeInteriorNode) node;
                        buffer.put(INTERIOR_NODE);
                        buffer.putLong(interiorNode.getParentAddress());
                        byte[] nodeBytes = interiorNode.getBytes();
                        writeNodeBytes(buffer.getPosition(), nodeBytes, position + nodeBlockSize - 1);
                } else {
                        throw new IllegalArgumentException("The given node is of unknown type.");
                }
        }

        /**
         * Read the node stored at the adress nodeDir
         * @param parentAddress
         * @return
         * @throws IOException
         */
        BTreeNode readNodeAt(long position) throws IOException {
                BTreeNode node = cache.get(position);
                if (node == null) {
                        buffer.position(position);
                        byte blockType = buffer.get();

                        // Read the address of the parent and neighbours
                        long parentAddress = buffer.getLong();
                        // Read the bytes of the node
                        byte[] nodeBytes = readNodeBytes(buffer.getPosition());

                        switch (blockType) {
                                case LEAF:
                                        // Return the instance
                                        node = BTreeLeaf.createLeafFromBytes(this, position, parentAddress,
                                                n, nodeBytes);
                                        break;
                                case INTERIOR_NODE:
                                        // Return the instance
                                        node = BTreeInteriorNode.createInteriorNodeFromBytes(this,
                                                position, parentAddress, n, nodeBytes);
                                        break;
                                default:
                                        throw new IOException("Cannot understand block type:"
                                                + blockType);
                        }

                        cache.put(position, node);
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

        /**
         * Get the element at the address emptyBlockDir as an empty block
         * @param emptyBlockAddress
         * @throws IOException
         */
        private void readEmptyBlockList(long emptyBlockAddress) throws IOException {
                emptyBlocks = new TreeSet<Long>();
                byte[] bytes = readNodeBytes(emptyBlockAddress);
                if (bytes.length != 0) {
                        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                        DataInputStream dis = new DataInputStream(bis);
                        try {
                                long emptyBlock = dis.readLong();
                                emptyBlocks.add(emptyBlock);
                        } catch (IOException e) {
                                throw new IOException("Problem while reading the empty block list", e);
                        }
                        dis.close();
                }
        }

        /**
         * Write the list of empty blocks
         * @param emptyBlockAddress
         * @throws IOException
         */
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

        /**
         * Write the header of this tree at the address emptyBLockDir
         * @param emptyBlockAddress
         * @throws IOException
         */
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
                        cache.clear();
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
                Value[] values = getAllValues();
                if (values.length != size()) {
                        throw new IllegalStateException("Size inconsistence");
                }
                for (int i = 0; i < values.length - 1; i++) {
                        if (!values[i].lessEqual(values[i + 1]).getAsBoolean()) {
                                throw new IllegalStateException(values[i] + " is greater "
                                        + "than its right neighbour at :" + i);
                        }
                }
        }

        @Override
        public boolean delete(Value v, int row) throws IOException {
                if (root.delete(v, row)) {
                        root = root.getNewRoot();
                        rootAddress = root.getAddress();

                        numElements--;

                        updateRows(row, -1);
                        return true;
                }
                return false;
        }

        public void updateRows(int startRow, int offset) throws IOException {
                if (updateRowNumbers && (startRow < size())) {
                        root.updateRows(startRow, offset);
                }
        }

        /**
         * Retrieve all the values contained in this tree
         * @return
         * @throws IOException
         */
        @Override
        public Value[] getAllValues() throws IOException {
                return root.getAllValues();
        }

        @Override
        public void insert(Value v, int rowIndex) throws IOException {
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

        /**
         * Return the number of empty blocks
         * @return
         */
        public int getEmptyBlocks() {
                return emptyBlocks.size();
        }

        /**
         * Remove a node in the tree. The corresponding address (and the following block)
         * is added to the empty blocks list.
         *
         * If there are some blocks that can be considered as extensions of this one, they
         * are deleted to.
         * @param nodeAddress
         * @throws IOException
         */
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
        public int[] getRow(Value min, boolean minIncluded, Value max,
                boolean maxIncluded) throws IOException {
                RangeComparator minComparator = null;
                RangeComparator maxComparator = null;
                if (min.isNull()) {
                        minComparator = new TrueComparator();
                } else if (minIncluded) {
                        minComparator = new GreaterEqualComparator(min);
                } else {
                        minComparator = new GreaterComparator(min);
                }
                if (max.isNull()) {
                        maxComparator = new TrueComparator();
                } else if (maxIncluded) {
                        maxComparator = new LessEqualComparator(max);
                } else {
                        maxComparator = new LessComparator(max);
                }

                return root.getIndex(minComparator, maxComparator);
        }

        @Override
        public int[] getRow(Value value) throws IOException {
                return root.getIndex(new LessEqualComparator(value),
                        new GreaterEqualComparator(value));
        }
}
