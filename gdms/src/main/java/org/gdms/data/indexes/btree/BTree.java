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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.data.indexes.btree;

import java.io.IOException;

import org.gdms.data.indexes.tree.IndexVisitor;
import org.gdms.data.indexes.tree.Tree;
import org.gdms.data.values.Value;

public interface BTree extends Tree<Value> {

	/**
	 * Performs a range query.
	 *
	 * @param min
	 * @param minIncluded
	 * @param max
	 * @param maxIncluded
	 * @return an array of row indexes that match the query
	 * @throws IOException
	 */
	int[] rangeQuery(Value min, boolean minIncluded, Value max,
			boolean maxIncluded) throws IOException;
        
        /**
	 * Performs a range query with an index visitor notified on elements that match.
	 *
	 * @param min
	 * @param minIncluded
	 * @param max
	 * @param maxIncluded
         * @param visitor 
	 * @throws IOException
	 */
	void rangeQuery(Value min, boolean minIncluded, Value max,
			boolean maxIncluded, IndexVisitor<Value> visitor) throws IOException;
        
        /**
         * Gets the smallest value in the whole tree.
         * @return the smallest value
         * @throws IOException 
         */
        Value getSmallestValue() throws IOException;
        
        /**
         * Gets the largest value in the whole tree.
         * @return the largest value
         * @throws IOException 
         */
        Value getLargestValue() throws IOException;

        /**
         * @return the smallest row in the tree
         * @throws IOException 
         */
        int smallest() throws IOException;
        
        /**
         * @return the largest row in the tree
         * @throws IOException 
         */
        int largest() throws IOException;
}