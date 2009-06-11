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
package org.gdms.data.indexes.rtree;

import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;

public interface RTreeNode {

	public boolean isLeaf();

	/**
	 * Inserts the element. if there is not enough space reorganizes the tree
	 *
	 * @param v
	 * @param rowIndex
	 *            index where the value is at
	 * @return The new root if the reorganization of the tree changed the root
	 * @throws IOException
	 */
	public void insert(Envelope v, int rowIndex) throws IOException;

	/**
	 * Deletes the element. The tree can be reorganized to match btree
	 * restrictions
	 *
	 * @param v
	 * @param row
	 * @return The new root if the reorganization of the tree changed the root
	 * @throws IOException
	 */
	public boolean delete(Envelope v, int row) throws IOException;

	/**
	 * Sets the parent of a node
	 *
	 * @param m
	 */
	public void setParentDir(int parentDir);

	/**
	 * Checks that the tree is well formed. Throws any exception if it's not.
	 * Just for debugging purposes
	 *
	 * @throws IOException
	 */
	public void checkTree() throws IOException;

	/**
	 * Gets the parent of the node. The root node returns null
	 *
	 * @return
	 * @throws IOException
	 */
	public AbstractRTreeNode getParent() throws IOException;

	/**
	 * Gets the representation of this node as a byte array
	 *
	 * @return
	 * @throws IOException
	 */
	public byte[] getBytes() throws IOException;

	/**
	 * Saves permanently this node at disk
	 *
	 * @throws IOException
	 */
	public void save() throws IOException;

	/**
	 * Get the position in the file of this node
	 *
	 * @return
	 */
	public int getDir();

	/**
	 * Gets the envelope of all the geometries under this node
	 *
	 * @return
	 * @throws IOException
	 */
	public Envelope getEnvelope() throws IOException;

	/**
	 * Gets the rows that contains geometries that can possibly intersect with
	 * the specified envelopes
	 *
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public int[] getRows(Envelope value) throws IOException;

	/**
	 * Gets all the values under this node
	 *
	 * @return
	 * @throws IOException
	 */
	public Envelope[] getAllValues() throws IOException;

	/**
	 * Returns true if the node satisfies the conditions of a btree
	 *
	 * @return
	 * @throws IOException
	 */
	public boolean isValid() throws IOException;

	/**
	 * Splits the node of the tree
	 *
	 * @return
	 * @throws IOException
	 */
	public RTreeNode splitNode() throws IOException;

	/**
	 * Returns true if the node can give an element to a neighbour and be still
	 * valid
	 *
	 * @return
	 * @throws IOException
	 */
	public boolean canGiveElement() throws IOException;

	/**
	 * Moves the first element to the specified node. Both nodes are at the same
	 * level so they are implementations of the same class
	 *
	 * @param child
	 * @throws IOException
	 */
	public void moveFirstTo(RTreeNode child) throws IOException;

	/**
	 * Moves the last element to the specified node. Both nodes are at the same
	 * level so they are implementations of the same class
	 *
	 * @param child
	 * @throws IOException
	 */
	public void moveLastTo(RTreeNode child) throws IOException;

	/**
	 * Merges this node with the left one. The left node is specified as a
	 * parameter
	 *
	 * @param leftNeighbour
	 * @throws IOException
	 */
	public void mergeWithLeft(RTreeNode leftNeighbour) throws IOException;

	/**
	 * Merges this node with the right one. The right node is specified as a
	 * parameter
	 *
	 * @param rightNeighbour
	 * @throws IOException
	 */
	public void mergeWithRight(RTreeNode rightNeighbour) throws IOException;

	/**
	 * Gets the new root of the tree if this node is no longer the root
	 *
	 * @return
	 * @throws IOException
	 */
	public RTreeNode getNewRoot() throws IOException;

	public int getValueCount();

	public void updateRows(int row, int inc) throws IOException;
}
