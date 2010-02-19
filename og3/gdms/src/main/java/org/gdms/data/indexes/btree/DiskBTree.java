/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
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
import java.util.SortedSet;
import java.util.TreeSet;

import org.gdms.data.values.Value;

public class DiskBTree implements BTree {

	private static final byte LEAF = 2;
	private static final byte INTERIOR_NODE = 1;
	private int nodeBlockSize = 256;
	private RandomAccessFile fis;
	private ReadWriteBufferManager buffer;
	private int rootDir;
	private int n;
	private int numElements = 0;

	private SortedSet<Integer> emptyBlocks;
	private BTreeNode root;
	private HashMap<Integer, BTreeNode> cache;
	private boolean inMemory;
	private int directionSequence = 0;
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
		cache = new HashMap<Integer, BTreeNode>();
		root = createLeaf(this, 0, -1);
	}

	public DiskBTree() throws IOException {
		this(255, 1024);
	}

	public void newIndex(File file) throws IOException {
		if (file.exists()) {
			throw new IOException("File already exists");
		} else {
			inMemory = false;
			fis = new RandomAccessFile(file, "rw");
			buffer = new ReadWriteBufferManager(fis.getChannel(),
					64 * nodeBlockSize);
			emptyBlocks = new TreeSet<Integer>();
			rootDir = 20;
			writeHeader(-1);
			cache = new HashMap<Integer, BTreeNode>();
			root = createLeaf(this, 0, -1);
		}
	}

	public BTreeLeaf createLeaf(BTree tree, int baseDir, int parentDir)
			throws IOException {
		int dir = getEmptyBlock(baseDir);
		BTreeLeaf ret = new BTreeLeaf(this, dir, -1);
		if (!inMemory) {
			writeNodeAt(dir, ret);
		}
		cache.put(dir, ret);
		return ret;
	}

	public BTreeInteriorNode createInteriorNode(int baseDir, int parentDir)
			throws IOException {
		int dir = getEmptyBlock(baseDir);
		BTreeInteriorNode ret = new BTreeInteriorNode(this, dir, parentDir);
		if (!inMemory) {
			writeNodeAt(dir, ret);
		}
		cache.put(dir, ret);
		return ret;
	}

	public BTreeInteriorNode createInteriorNode(int baseDir, int parentDir,
			BTreeNode leftChild, BTreeNode rightChild) throws IOException {
		int dir = getEmptyBlock(baseDir);
		BTreeInteriorNode ret = new BTreeInteriorNode(this, dir, parentDir,
				leftChild, rightChild);
		if (!inMemory) {
			writeNodeAt(dir, ret);
		}
		cache.put(dir, ret);
		return ret;
	}

	public void openIndex(File file) throws IOException {
		if (!file.exists()) {
			throw new IOException("The file " + file + " does not exist");
		} else {
			inMemory = false;
			fis = new RandomAccessFile(file, "rw");
			FileChannel channel = fis.getChannel();
			buffer = new ReadWriteBufferManager(channel, 64 * nodeBlockSize);
			readHeader();
			cache = new HashMap<Integer, BTreeNode>();
			root = readNodeAt(rootDir);
		}
	}

	private int getEmptyBlock(int baseDirection) throws IOException {
		if (inMemory) {
			directionSequence++;
			return directionSequence;
		} else {
			SortedSet<Integer> nearestEmptyBlock = emptyBlocks
					.tailSet(baseDirection);
			if (!nearestEmptyBlock.isEmpty()) {
				int ret = nearestEmptyBlock.first();
				emptyBlocks.remove(ret);
				return ret;
			} else {
				// Create a new empty node at the end of the file
				return buffer.getEOFDirection();
			}
		}
	}

	/**
	 * @param position
	 * @param nodeBytes
	 * @param blockEnd
	 *            the latest available byte in this node
	 * @throws IOException
	 */
	private void writeNodeBytes(int position, byte[] nodeBytes, int blockEnd)
			throws IOException {
		int nodeBytesStartPosition = position + 8;

		byte[] remaining = new byte[0];
		byte[] bytesInThisBlock = nodeBytes;
		if (nodeBytesStartPosition + nodeBytes.length > blockEnd) {
			bytesInThisBlock = new byte[blockEnd - nodeBytesStartPosition + 1];
			System.arraycopy(nodeBytes, 0, bytesInThisBlock, 0,
					bytesInThisBlock.length);
			remaining = new byte[nodeBytes.length - bytesInThisBlock.length];
			System.arraycopy(nodeBytes, bytesInThisBlock.length, remaining, 0,
					nodeBytes.length - bytesInThisBlock.length);
		}

		buffer.position(position);
		// Write the direction of the extension node. -1 si written now but it's
		// fixed at the end of the method
		buffer.putInt(-1);

		// Write the size of the node bytes in this block
		buffer.putInt(bytesInThisBlock.length);

		// Write the bytes
		buffer.put(bytesInThisBlock);

		// Fill the rest of the block
		byte[] fillBytes = new byte[blockEnd - buffer.getPosition() + 1];
		buffer.put(fillBytes);

		// Write the extension node
		int extensionBlock = -1;
		if (remaining.length > 0) {
			extensionBlock = getEmptyBlock(position);
			buffer.position(position);
			buffer.putInt(extensionBlock);
			writeNodeBytes(extensionBlock, remaining, extensionBlock
					+ nodeBlockSize - 1);
		}
	}

	private byte[] readNodeBytes(int dir) throws IOException {
		buffer.position(dir);
		// Read the direction of the extension node
		int nextNode = buffer.getInt();
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
		System
				.arraycopy(thisBlockBytes, 0, nodeBytes, 0,
						thisBlockBytes.length);
		System.arraycopy(extensionBytes, 0, nodeBytes, thisBlockBytes.length,
				extensionBytes.length);
		return nodeBytes;
	}

	public void writeNodeAt(int dir, BTreeNode node) throws IOException {
		buffer.position(dir);
		if (node instanceof BTreeLeaf) {
			BTreeLeaf leaf = (BTreeLeaf) node;
			buffer.put(LEAF);
			buffer.putInt(leaf.getParentDir());
			byte[] nodeBytes = node.getBytes();
			writeNodeBytes(buffer.getPosition(), nodeBytes, dir + nodeBlockSize
					- 1);

		} else if (node instanceof BTreeInteriorNode) {
			BTreeInteriorNode interiorNode = (BTreeInteriorNode) node;
			buffer.put(INTERIOR_NODE);
			buffer.putInt(interiorNode.getParentDir());
			byte[] nodeBytes = interiorNode.getBytes();
			writeNodeBytes(buffer.getPosition(), nodeBytes, dir + nodeBlockSize
					- 1);
		} else {
			throw new RuntimeException("bug!");
		}

	}

	/**
	 * @param parentDir
	 * @return
	 * @throws IOException
	 */
	BTreeNode readNodeAt(int nodeDir) throws IOException {
		BTreeNode node = cache.get(nodeDir);
		if (node == null) {
			buffer.position(nodeDir);
			byte blockType = buffer.get();

			// Read the direction of the parent and neighbours
			int parentDir = buffer.getInt();
			// Read the bytes of the node
			byte[] nodeBytes = readNodeBytes(buffer.getPosition());

			switch (blockType) {
			case LEAF:
				// Return the instance
				node = BTreeLeaf.createLeafFromBytes(this, nodeDir, parentDir,
						n, nodeBytes);
				break;
			case INTERIOR_NODE:
				// Return the instance
				node = BTreeInteriorNode.createInteriorNodeFromBytes(this,
						nodeDir, parentDir, n, nodeBytes);
				break;
			default:
				throw new IOException("Cannot understand block type:"
						+ blockType);
			}

			cache.put(nodeDir, node);
		}

		return node;
	}

	void readHeader() throws IOException {
		nodeBlockSize = buffer.getInt();
		numElements = buffer.getInt();
		n = buffer.getInt();
		int emptyBlockDir = buffer.getInt();
		rootDir = buffer.getInt();
		if (emptyBlockDir != -1) {
			readEmptyBlockList(emptyBlockDir);
		}
	}

	private void readEmptyBlockList(int emptyBlockDir) throws IOException {
		emptyBlocks = new TreeSet<Integer>();
		byte[] bytes = readNodeBytes(emptyBlockDir);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bis);
		try {
			int emptyBlock = dis.readInt();
			emptyBlocks.add(emptyBlock);
		} catch (IOException e) {
		}
		dis.close();
	}

	private void writeEmptyBlockList(int emptyBlockDir) throws IOException {
		// Remove the empty blocks at the end of the file
		if (emptyBlocks.size() > 0) {
			int lastBlock = emptyBlocks.last();
			if (lastBlock + nodeBlockSize >= buffer.getEOFDirection()) {
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
		for (Integer emptyBlock : emptyBlocks) {
			dos.writeInt(emptyBlock);
		}
		dos.close();
		writeNodeBytes(emptyBlockDir, bos.toByteArray(), emptyBlockDir
				+ nodeBlockSize - 1);
	}

	void writeHeader(int emptyBlockDir) throws IOException {
		buffer.position(0);
		buffer.putInt(nodeBlockSize);
		buffer.putInt(numElements);
		buffer.putInt(n);
		buffer.putInt(emptyBlockDir);
		buffer.putInt(rootDir);
	}

	public void save() throws IOException {
		if (inMemory) {
			throw new UnsupportedOperationException("Memory "
					+ "indexes cannot be saved");
		} else {
			int emptyBlockDir = getEmptyBlock(0);
			writeEmptyBlockList(emptyBlockDir);
			writeHeader(emptyBlockDir);
			root.save();
			buffer.flush();

			// We throw all the tree we have in memory
			cache.clear();
			root = readNodeAt(rootDir);
		}
	}

	public void close() throws IOException {
		if (inMemory) {
			throw new UnsupportedOperationException("Memory "
					+ "indexes cannot be closed");
		} else {
			save();
			// Free resources
			fis.close();
		}
	}

	public void checkTree() throws IOException {
		root.checkTree();
		Value[] values = getAllValues();
		if (values.length != size()) {
			throw new RuntimeException("Size inconsistence");
		}
		for (int i = 0; i < values.length - 1; i++) {
			if (!values[i].lessEqual(values[i + 1]).getAsBoolean()) {
				throw new RuntimeException(values[i] + " is greater "
						+ "than its right neighbour at :" + i);
			}
		}
	}

	public void delete(Value v, int row) throws IOException {
		if (root.delete(v, row)) {
			root = root.getNewRoot();
			rootDir = root.getDir();

			numElements--;

			updateRows(row, -1);
		}

	}

	public void updateRows(int startRow, int offset) throws IOException {
		if (updateRowNumbers && (startRow < size())) {
			root.updateRows(startRow, offset);
		}
	}

	public Value[] getAllValues() throws IOException {
		return root.getAllValues();
	}

	public void insert(Value v, int rowIndex) throws IOException {
		updateRows(rowIndex, 1);
		root.insert(v, rowIndex);
		if (root.getParent() != null) {
			root = root.getParent();
			rootDir = root.getDir();
		}

		numElements++;
	}

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

	public void removeNode(int nodeDir) throws IOException {
		cache.remove(nodeDir);
		emptyBlocks.add(nodeDir);
		buffer.position(nodeDir + 5);
		int extensionBlock = buffer.getInt();
		if (extensionBlock != -1) {
			deleteExtensionBlock(extensionBlock);
		}
	}

	private void deleteExtensionBlock(int extensionBlockDir) throws IOException {
		emptyBlocks.add(extensionBlockDir);
		buffer.position(extensionBlockDir);
		int nextExtensionBlock = buffer.getInt();
		if (nextExtensionBlock != -1) {
			deleteExtensionBlock(nextExtensionBlock);
		}
	}

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

	public int[] getRow(Value value) throws IOException {
		return root.getIndex(new LessEqualComparator(value),
				new GreaterEqualComparator(value));
	}

}
