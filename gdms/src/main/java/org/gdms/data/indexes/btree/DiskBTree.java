package org.gdms.data.indexes.btree;

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

	public DiskBTree(int n) throws IOException {
		this.n = n;
		inMemory = true;
		cache = new HashMap<Integer, BTreeNode>();
		inMemory = true;
		root = createLeaf(this, 0, -1, n);
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
			rootDir = 16;
			writeHeader();
			cache = new HashMap<Integer, BTreeNode>();
			root = createLeaf(this, 0, -1, n);
		}
	}

	public BTreeLeaf createLeaf(BTree tree, int baseDir, int parentDir, int n)
			throws IOException {
		int dir = getEmptyBlock(baseDir);
		BTreeLeaf ret = new BTreeLeaf(this, dir, -1, n);
		if (!inMemory) {
			writeNodeAt(dir, ret);
		}
		cache.put(dir, ret);
		return ret;
	}

	public BTreeInteriorNode createInteriorNode(int baseDir, int parentDir,
			int n) throws IOException {
		int dir = getEmptyBlock(baseDir);
		BTreeInteriorNode ret = new BTreeInteriorNode(this, dir, parentDir, n);
		if (!inMemory) {
			writeNodeAt(dir, ret);
		}
		cache.put(dir, ret);
		return ret;
	}

	public BTreeInteriorNode createInteriorNode(int baseDir, int parentDir,
			int n, BTreeNode leftChild, BTreeNode rightChild)
			throws IOException {
		int dir = getEmptyBlock(baseDir);
		BTreeInteriorNode ret = new BTreeInteriorNode(this, dir, parentDir, n,
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
				return nearestEmptyBlock.first();
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
	 *            the latest available byte
	 * @throws IOException
	 */
	private void writeNodeBytes(int position, byte[] nodeBytes, int blockEnd)
			throws IOException {
		byte[] remaining = new byte[0];
		byte[] bytesInThisBlock = nodeBytes;
		if (position + nodeBytes.length > blockEnd) {
			bytesInThisBlock = new byte[blockEnd - position + 1];
			System.arraycopy(nodeBytes, 0, nodeBytes, 0,
					bytesInThisBlock.length);
			System.arraycopy(nodeBytes, bytesInThisBlock.length, remaining, 0,
					nodeBytes.length - bytesInThisBlock.length);
		}

		buffer.position(position);
		// Write the direction of the extension node
		int extensionBlock = -1;
		if (remaining.length > 0) {
			extensionBlock = getEmptyBlock(position);
			buffer.putInt(extensionBlock);
		} else {
			buffer.putInt(-1);
		}

		// Write the size of the node bytes in this block
		buffer.putInt(bytesInThisBlock.length);

		// Write the bytes
		buffer.put(bytesInThisBlock);

		// Fill the rest of the block
		byte[] fillBytes = new byte[nodeBlockSize
				- (buffer.getPosition() - position)];
		buffer.put(fillBytes);

		// Write the extension node
		if (extensionBlock != -1) {
			writeNodeBytes(extensionBlock, remaining, extensionBlock
					+ nodeBlockSize - 1);
		}
	}

	private byte[] readNodeBytes() throws IOException {
		// Read the direction of the extension node
		int nextNode = buffer.getInt();
		// Read the size of the node bytes in this block
		int blockBytesLength = buffer.getInt();
		// Read all the bytes
		byte[] thisBlockBytes = new byte[blockBytesLength];
		buffer.get(thisBlockBytes);
		byte[] extensionBytes = new byte[0];
		if (nextNode != -1) {
			extensionBytes = readExtensionNode(nextNode);
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
			byte[] nodeBytes = readNodeBytes();

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

	private byte[] readExtensionNode(int nodeDir) throws IOException {
		buffer.position(nodeDir);
		return readNodeBytes();
	}

	void readHeader() throws IOException {
		nodeBlockSize = buffer.getInt();
		numElements = buffer.getInt();
		int emptyBlockCount = buffer.getInt();
		emptyBlocks = new TreeSet<Integer>();
		for (int i = 0; i < emptyBlockCount; i++) {
			emptyBlocks.add(buffer.getInt());
		}
		rootDir = buffer.getInt();
	}

	void writeHeader() throws IOException {
		buffer.position(0);
		buffer.putInt(nodeBlockSize);
		buffer.putInt(numElements);
		buffer.putInt(emptyBlocks.size());
		for (Integer emptyBlockDir : emptyBlocks) {
			buffer.putInt(emptyBlockDir);
		}
		buffer.putInt(rootDir);
	}

	public void save() throws IOException {
		if (inMemory) {
			throw new UnsupportedOperationException("Memory "
					+ "indexes cannot be saved");
		} else {
			writeHeader();
			root.save();
			buffer.flush();

			// We throw all the tree we have in memory
			cache = new HashMap<Integer, BTreeNode>();
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
	}

	public void delete(Value v) throws IOException {
		// find the appropiate leave
		BTreeNode node = root.getChildNodeFor(v);

		// Perform the deletion
		BTreeNode newRoot = node.delete(v);
		if (newRoot != null) {
			root = newRoot;
			rootDir = root.getDir();
		}

		numElements--;
	}

	public Value[] getAllValues() throws IOException {
		BTreeLeaf firstLeaf = root.getFirstLeaf();
		return firstLeaf.getAllValues();
	}

	public int[] getRow(Value value) throws IOException {
		BTreeLeaf node = root.getChildNodeFor(value);
		return node.getIndex(value);
	}

	public void insert(Value v, int rowIndex) throws IOException {
		// find the appropiate leave
		BTreeNode node = root.getChildNodeFor(v);

		// Perform the insertion
		BTreeNode newRoot = node.insert(v, rowIndex);
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

}
