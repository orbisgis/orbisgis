package org.gdms.data.indexes.rtree;

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

import org.gdms.data.indexes.btree.ReadWriteBufferManager;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class DiskRTree implements RTree {

	private static final byte LEAF = 2;
	private static final byte INTERIOR_NODE = 1;
	private int nodeBlockSize = 256;
	private RandomAccessFile fis;
	private ReadWriteBufferManager buffer;
	private int rootDir;
	private int n;
	private int numElements = 0;

	private SortedSet<Integer> emptyBlocks;
	private RTreeNode root;
	private HashMap<Integer, RTreeNode> cache;
	private boolean inMemory;
	private int directionSequence = 0;

	public DiskRTree(int n, int nodeBlockSize) throws IOException {
		this.n = n;
		this.nodeBlockSize = nodeBlockSize;
		inMemory = true;
		cache = new HashMap<Integer, RTreeNode>();
		root = createLeaf(this, 0, -1);
	}

	public DiskRTree() throws IOException {
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
			cache = new HashMap<Integer, RTreeNode>();
			root = createLeaf(this, 0, -1);
		}
	}

	public RTreeLeaf createLeaf(RTree tree, int baseDir, int parentDir)
			throws IOException {
		int dir = getEmptyBlock(baseDir);
		RTreeLeaf ret = new RTreeLeaf(this, dir, -1);
		if (!inMemory) {
			writeNodeAt(dir, ret);
		}
		cache.put(dir, ret);
		return ret;
	}

	public RTreeInteriorNode createInteriorNode(int baseDir, int parentDir)
			throws IOException {
		int dir = getEmptyBlock(baseDir);
		RTreeInteriorNode ret = new RTreeInteriorNode(this, dir, parentDir);
		if (!inMemory) {
			writeNodeAt(dir, ret);
		}
		cache.put(dir, ret);
		return ret;
	}

	public RTreeInteriorNode createInteriorNode(int baseDir, int parentDir,
			RTreeNode leftChild, RTreeNode rightChild) throws IOException {
		int dir = getEmptyBlock(baseDir);
		RTreeInteriorNode ret = new RTreeInteriorNode(this, dir, parentDir,
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
			cache = new HashMap<Integer, RTreeNode>();
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

	public void writeNodeAt(int dir, RTreeNode node) throws IOException {
		buffer.position(dir);
		if (node instanceof RTreeLeaf) {
			RTreeLeaf leaf = (RTreeLeaf) node;
			buffer.put(LEAF);
			buffer.putInt(leaf.getParentDir());
			byte[] nodeBytes = node.getBytes();
			writeNodeBytes(buffer.getPosition(), nodeBytes, dir + nodeBlockSize
					- 1);

		} else if (node instanceof RTreeInteriorNode) {
			RTreeInteriorNode interiorNode = (RTreeInteriorNode) node;
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
	RTreeNode readNodeAt(int nodeDir) throws IOException {
		RTreeNode node = cache.get(nodeDir);
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
				node = RTreeLeaf.createLeafFromBytes(this, nodeDir, parentDir,
						n, nodeBytes);
				break;
			case INTERIOR_NODE:
				// Return the instance
				node = RTreeInteriorNode.createInteriorNodeFromBytes(this,
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
//TODO		root.checkTree();
//		Geometry[] values = getAllValues();
//		for (int i = 0; i < values.length - 1; i++) {
//			if (!values[i].lessEqual(values[i + 1]).getAsBoolean()) {
//				throw new RuntimeException(values[i] + " is greater "
//						+ "than its right neighbour at :" + i);
//			}
//		}
	}

	public void delete(Geometry v, int row) throws IOException {
		RTreeNode newRoot = root.delete(v, row);
		if (newRoot != null) {
			root = newRoot;
			rootDir = root.getDir();
		}

		numElements--;
	}

	public Geometry[] getAllValues() throws IOException {
		return root.getAllValues();
	}

	public void insert(Geometry v, int rowIndex) throws IOException {
		RTreeNode newRoot = root.insert(v, rowIndex);
		if (newRoot != null) {
			root = newRoot;
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

//	public int[] getRow(Geometry min, boolean minIncluded, Geometry max,
//			boolean maxIncluded) throws IOException {
//		RangeComparator minComparator = null;
//		RangeComparator maxComparator = null;
//		if (min.isNull()) {
//			minComparator = new TrueComparator();
//		} else if (minIncluded) {
//			minComparator = new GreaterEqualComparator(min);
//		} else {
//			minComparator = new GreaterComparator(min);
//		}
//		if (max.isNull()) {
//			maxComparator = new TrueComparator();
//		} else if (maxIncluded) {
//			maxComparator = new LessEqualComparator(max);
//		} else {
//			maxComparator = new LessComparator(max);
//		}
//
//		RTreeLeaf startingNode = null;
//		if (min.isNull()) {
//			startingNode = root.getFirstLeaf();
//		} else {
//			startingNode = root.getChildNodeFor(min);
//		}
//
//		return startingNode.getIndex(minComparator, maxComparator);
//	}

	public int[] getRow(Envelope value) throws IOException {
		return root.getRows(value);
	}

}
