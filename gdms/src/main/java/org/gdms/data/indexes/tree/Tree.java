/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
         * Queries the tree for the given value.
         * @param value
         * @return an array of row indexes for the given value
         * @throws IOException
         */
        int[] query(T value) throws IOException;
        
        /**
         * Queries the tree for the given value, with the specified visitor.
         * @param value
         * @param visitor 
         * @throws IOException
         */
        void query(T value, IndexVisitor<T> visitor) throws IOException;

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
