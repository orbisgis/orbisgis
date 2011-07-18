package org.gdms.data.indexes.tree;

import java.io.IOException;

/**
 *
 * @param <T> 
 * @param <A> 
 * @author Antoine Gourlay
 */
public interface TreeNode<T, A extends TreeNode<T, ?>> {

        /**
         * Inserts the given element <tt>v</tt> at the specified row index
         * @param v
         * @param rowIndex
         * @return
         * @throws IOException
         */
        T insert(T v, int rowIndex) throws IOException;

        /**
         * Deletes the given element <tt>v</tt> at the specified row index
         * @param v
         * @param rowIndex 
         * @return
         * @throws IOException
         */
        boolean delete(T v, int rowIndex) throws IOException;

        /**
         * Gets all elements stored in this node
         * @return
         * @throws IOException
         * @see org.gdms.data.indexes.btree.BTree#getAllValues()
         */
        T[] getAllValues() throws IOException;

        /**
         * Returns true if this node is a leaf, false otherwise
         * @return
         */
        boolean isLeaf();

        /**
         * Sets the parent address of a node
         *
         * @param parentAddress
         */
        void setParentAddress(long parentAddress);

        /**
         * Gets the parent address of a node
         *
         * @return
         */
        long getParentAddress();

        /**
         * Checks that the tree is well formed. Throws any exception if it's not.
         * Just for debugging purposes
         *
         * @throws IOException
         */
        void checkTree() throws IOException;

        /**
         * Gets the parent of the node. The root node returns null
         *
         * @return
         * @throws IOException
         */
        A getParent() throws IOException;

        /**
         * Gets the representation of this node as a byte array
         *
         * @return
         * @throws IOException
         */
        byte[] getBytes() throws IOException;

        /**
         * Saves permanently this node at disk
         *
         * @throws IOException
         */
        void save() throws IOException;

        /**
         * Get the position in the file of this node
         *
         * @return
         */
        long getAddress();

        /**
         * Return true if the node is valid
         *
         * @return
         * @throws IOException
         */
        boolean isValid() throws IOException;

        /**
         * Splits the node of the tree
         *
         * @return
         * @throws IOException
         */
        A splitNode() throws IOException;

        /**
         * Returns true if the node can give an element to a neighbour and be still
         * valid
         *
         * @return
         * @throws IOException
         */
        boolean canGiveElement() throws IOException;

        /**
         * Moves the first element to the specified node. Both nodes are at the same
         * level so they are implementations of the same class
         *
         * @param child
         * @throws IOException
         */
        void moveFirstTo(A child) throws IOException;

        /**
         * Moves the last element to the specified node. Both nodes are at the same
         * level so they are implementations of the same class
         *
         * @param child
         * @throws IOException
         */
        void moveLastTo(A child) throws IOException;

        /**
         * Merges this node with the left one. The left node is specified as a
         * parameter
         *
         * @param leftNeighbour
         * @throws IOException
         */
        void mergeWithLeft(A leftNeighbour) throws IOException;

        /**
         * Merges this node with the right one. The right node is specified as a
         * parameter
         *
         * @param rightNeighbour
         * @throws IOException
         */
        void mergeWithRight(A rightNeighbour) throws IOException;

        /**
         * Gets the new root of the tree if this node is no longer the root
         *
         * @return
         * @throws IOException
         */
        A getNewRoot() throws IOException;

        /**
         * Gets the number of value stored in this node
         * @return
         * @throws IOException
         */
        int getValueCount() throws IOException;

        /**
         * Updates all row addresses after <tt>row</tt> by an increment of <tt>inc</tt>
         * @param row the row address to start at
         * @param inc the amount to increment the addresses
         * @throws IOException
         */
        void updateRows(int row, int inc) throws IOException;
}
