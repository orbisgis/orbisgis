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
package org.gdms.data.indexes.btree;

import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.values.Value;

public abstract class AbstractBTreeNode implements BTreeNode {

	private static int nodes = 0;

	protected ArrayList<Value> values;
	private int parentDir;
	protected String name;

	protected DiskBTree tree;

	protected int dir;

	private BTreeInteriorNode parent;

	public AbstractBTreeNode(DiskBTree btree, int dir, int parentDir) {
		this.tree = btree;
		this.dir = dir;
		this.parentDir = parentDir;
		values = new ArrayList<Value>();
		this.name = "node-" + nodes;
		nodes++;
	}

	public void setParentDir(int parentDir) {
		if (this.parentDir != parentDir) {
			this.parentDir = parentDir;
			this.parent = null;
		}
	}

	protected abstract boolean isValid(int valueCount) throws IOException;

	public BTreeInteriorNode getParent() throws IOException {
		if ((parent == null) && (parentDir != -1)) {
			parent = (BTreeInteriorNode) tree.readNodeAt(parentDir);
		}
		return parent;
	}

	/**
	 * Gets the index of a value. If the value exist it returns its index.
	 * Otherwise it returns the place where it should be inserted
	 *
	 * @param v
	 *            search key
	 * @param values
	 *            keys to search
	 * @param valueCount
	 *            number of values
	 * @return The index in the value array where this value will be inserted
	 */
	protected int getIndexOf(Value v) {
		int index = values.size();
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).isNull() || v.lessEqual(values.get(i)).getAsBoolean()) {
				index = i;
				break;
			}
		}
		return index;
	}

	public int getParentDir() {
		return parentDir;
	}

	public int getDir() {
		return dir;
	}

	public boolean canGiveElement() throws IOException {
		return isValid(values.size() - 1);
	}
}
