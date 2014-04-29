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
/**
 * Contains the actual implementation of disk and memory BTree structure.
 * 
 * <p>
 * The class {@link DiskBTree} is the main entry point for the structure. It can be queried for alphanumeric
 * ranges.<br />
 * The structure can be either:
 * <ul>
 * <li>in memory only</li>
 * <li>on disk, with a cache in memory</li>
 * </ul>
 * </p>
 * <p>
 * The actual tree implemented is a B+tree structure. The  keys are the {@link Value} objects being ordered, the
 * records are the row indexes corresponding to the keys:
 * <ul>
 * <li>The leaf nodes store the actual indexes (records) and the corresponding Value objects (key).</li>
 * <li>The ordering is based on the comparison of the Values objects.</li>
 * <li>The internal nodes store the bound keys (Value objects) of its children.</li>
 * </ul>
 * </p>
 * <p>
 * The implementation only supports generic range queries. There is no special optimization for equality queries or full
 * tree traversal.
 * </p>
 */
package org.gdms.data.indexes.btree;
