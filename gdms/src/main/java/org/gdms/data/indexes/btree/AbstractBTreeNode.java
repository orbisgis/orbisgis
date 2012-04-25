/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer,
Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.indexes.btree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.values.Value;

public abstract class AbstractBTreeNode implements BTreeNode {

	private static int nodes = 0;

	protected List<Value> values;
//        The adress of the parent node.
	private long parentAddress;
	protected String name;

	protected DiskBTree tree;
        //The adress of this node.
	protected long address;

	private BTreeInteriorNode parent;

	public AbstractBTreeNode(DiskBTree btree, long dir, long parentAddress) {
		this.tree = btree;
		this.address = dir;
		this.parentAddress = parentAddress;
		values = new ArrayList<Value>();
		this.name = "node-" + nodes;
		nodes++;
	}

	@Override
	public void setParentAddress(long parentAddress) {
		if (this.parentAddress != parentAddress) {
			this.parentAddress = parentAddress;
			this.parent = null;
		}
	}

	protected abstract boolean isValid(int valueCount) throws IOException;

	@Override
	public BTreeInteriorNode getParent() throws IOException {
		if ((parent == null) && (parentAddress != -1)) {
			parent = (BTreeInteriorNode) tree.readNodeAt(parentAddress);
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

	@Override
	public long getParentAddress() {
		return parentAddress;
	}

	@Override
	public long getAddress() {
		return address;
	}

	@Override
	public boolean canGiveElement() throws IOException {
		return isValid(values.size() - 1);
	}
}
