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

import java.io.File;
import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;

public interface RTree {

	/**
	 * @throws IOException
	 * @see org.gdms.data.indexes.btree.BTree#insert(org.gdms.data.values.Value,
	 *      int)
	 */
	public abstract void insert(Envelope v, int rowIndex) throws IOException;

	/**
	 * @throws IOException
	 * @see org.gdms.data.indexes.btree.BTree#delete(org.gdms.data.values.Value)
	 */
	public abstract void delete(Envelope v, int rowIndex) throws IOException;

	/**
	 * @throws IOException
	 * @see org.gdms.data.indexes.btree.BTree#getRow(org.gdms.data.values.Value)
	 */
	public abstract int[] getRow(Envelope value) throws IOException;

	/**
	 * @throws IOException
	 * @see org.gdms.data.indexes.btree.BTree#getAllValues()
	 */
	public abstract Envelope[] getAllValues() throws IOException;

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

}