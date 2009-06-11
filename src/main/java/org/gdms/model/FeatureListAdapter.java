/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.gdms.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

public class FeatureListAdapter implements List {

	private DataSource ds;
	private int sfi;

	public FeatureListAdapter(DataSource ds, int spatialFieldIndex) {
		this.ds = ds;
		this.sfi = spatialFieldIndex;
	}

	public int size() {
		try {
			return (int) ds.getRowCount();
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	public Iterator iterator() {
		return new FeatureIterator(ds, sfi);
	}

	public Object[] toArray() {
		Object[] ret = new Object[size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = get(i);
		}

		return ret;
	}

	public Object[] toArray(Object[] a) {
		throw new UnsupportedOperationException();
	}

	public boolean add(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection c) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public Object get(int index) {
		return new FeatureAdapter(ds, index, sfi);
	}

	public Object set(int index, Object element) {
		throw new UnsupportedOperationException();
	}

	public void add(int index, Object element) {
		throw new UnsupportedOperationException();
	}

	public Object remove(int index) {
		throw new UnsupportedOperationException();
	}

	public int indexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	public ListIterator listIterator() {
		throw new UnsupportedOperationException();
	}

	public ListIterator listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	public List subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

}
