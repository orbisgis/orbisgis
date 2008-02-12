package org.gdms.data.indexes.btree;

import java.io.File;
import java.io.IOException;

import org.gdms.data.values.Value;

public interface BTree {

	/**
	 * @throws IOException
	 * @see org.gdms.data.indexes.btree.BTree#insert(org.gdms.data.values.Value,
	 *      int)
	 */
	public abstract void insert(Value v, int rowIndex) throws IOException;

	/**
	 * @throws IOException
	 * @see org.gdms.data.indexes.btree.BTree#delete(org.gdms.data.values.Value)
	 */
	public abstract void delete(Value v, int rowIndex) throws IOException;

	/**
	 * @throws IOException
	 * @see org.gdms.data.indexes.btree.BTree#getRow(org.gdms.data.values.Value)
	 */
	public abstract int[] getRow(Value value) throws IOException;

	/**
	 * @throws IOException
	 * @see org.gdms.data.indexes.btree.BTree#getAllValues()
	 */
	public abstract Value[] getAllValues() throws IOException;

	/**
	 * @see org.gdms.data.indexes.btree.BTree#size()
	 */
	public abstract int size();

	/**
	 * @throws IOException
	 * @see org.gdms.data.indexes.btree.BTree#checkTree()
	 */
	public abstract void checkTree() throws IOException;

	public void newIndex(File file) throws IOException;

	public void openIndex(File file) throws IOException;

	/**
	 * Saves the index and frees the memory. The index is still operative
	 *
	 * @throws IOException
	 */
	public void save() throws IOException;

	/**
	 * Closes the index. The index won't be accessible until a new call to
	 * newIndex or openIndex is done
	 *
	 * @throws IOException
	 */
	public void close() throws IOException;

	/**
	 * Range query
	 *
	 * @param min
	 * @param minIncluded
	 * @param max
	 * @param maxIncluded
	 * @return
	 * @throws IOException
	 */
	public abstract int[] getRow(Value min, boolean minIncluded, Value max,
			boolean maxIncluded) throws IOException;

}