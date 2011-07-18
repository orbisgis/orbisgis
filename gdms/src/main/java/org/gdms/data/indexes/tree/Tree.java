package org.gdms.data.indexes.tree;

import java.io.File;
import java.io.IOException;

/**
 * A base interface for trees of types <tt>T</tt>
 * @param <T> the type of objects in the tree
 * @author Antoine Gourlay
 */
public interface Tree<T> {

        /**
         * Inserts the given element <tt>v</tt> at the specified row index
         * @param v
         * @param rowIndex
         * @throws IOException
         */
        void insert(T v, int rowIndex) throws IOException;

        /**
         * Deletes the given element <tt>v</tt> at the specified row index
         * @param v
         * @param rowIndex
         * @return
         * @throws IOException
         */
        boolean delete(T v, int rowIndex) throws IOException;

        /**
         *
         * @param value
         * @return
         * @throws IOException
         */
        int[] getRow(T value) throws IOException;

        /**
         * Gets all elements stored in this node
         * @return
         * @throws IOException
         * @see org.gdms.data.indexes.btree.BTree#getAllValues()
         */
        T[] getAllValues() throws IOException;

        /**
         * @return 
         * @see org.gdms.data.indexes.btree.BTree#size()
         */
        int size();

        /**
         * @throws IOException
         * @see org.gdms.data.indexes.btree.BTree#checkTree()
         */
        void checkTree() throws IOException;

        /**
         * Creates a new index from this file
         * @param file
         * @throws IOException
         */
        void newIndex(File file) throws IOException;

        /**
         * Opens the index stored in a file
         * @param file
         * @throws IOException
         */
        void openIndex(File file) throws IOException;

        /**
         * Saves the index and frees the memory. The index is still operative
         *
         * @throws IOException
         */
        void save() throws IOException;

        /**
         * Closes the index. The index won't be accessible until a new call to
         * newIndex or openIndex is done
         *
         * @throws IOException
         */
        void close() throws IOException;
}
