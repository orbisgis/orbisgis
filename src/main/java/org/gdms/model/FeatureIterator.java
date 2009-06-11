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

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;



public class FeatureIterator implements Iterator {

    private DataSource ds;
    private int index = 0;
	private int sfi;

    public FeatureIterator(DataSource ds, int spatialFieldIndex) {
        this.ds = ds;
		this.sfi = spatialFieldIndex;
    }

    public boolean hasNext() {
        try {
            return index < ds.getRowCount();
        } catch (DriverException e) {
            throw new RuntimeException(e);
        }
    }

    public Object next() {
        Feature f = new FeatureAdapter(ds, index, sfi);
        index++;

        return f;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
