/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
 * 
 * User support leader : Gwendall Petit, geomatic engineer.
 * 
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
package org.gdms.data.indexes.tree;

import java.io.IOException;

/**
 * Base interface for all tree nodes in the default indexes included in Gdms.
 * 
 * @param <T> type of the elements stored in the tree
 * @param <A> specialized node type
 * @param <Q> type of the object being used for querying the tree
 * @author Antoine Gourlay
 */
public interface TreeNode<T, A extends TreeNode<T, ?, Q>, Q> {

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
        
        /**
         * Queries this node with the specified query value.
         * @param queryValue a value to query for
         * @return an array of rows that match
         * @throws IOException 
         */
        int[] query(Q queryValue) throws IOException;
        
        /**
         * Queries this node with the specified query value, with the specified index visitor.
         * @param queryValue a value to query for
         * @param visitor a visitor
         * @return an array of rows that match
         * @throws IOException 
         */
        int[] query(Q queryValue, IndexVisitor<T> visitor) throws IOException;
}
