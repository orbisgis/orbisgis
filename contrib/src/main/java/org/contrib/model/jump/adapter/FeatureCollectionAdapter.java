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
package org.contrib.model.jump.adapter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.contrib.model.jump.model.Feature;
import org.contrib.model.jump.model.FeatureCollection;
import org.contrib.model.jump.model.FeatureSchema;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;


import com.vividsolutions.jts.geom.Envelope;

public class FeatureCollectionAdapter implements FeatureCollection {

    private SpatialDataSourceDecorator ds;

    public FeatureCollectionAdapter(SpatialDataSourceDecorator ds) {
        this.ds = ds;
    }
    
    public FeatureSchema getFeatureSchema() {
        return new FeatureSchemaAdapter(ds);
    }

    public Envelope getEnvelope() {
        
        try {
			return ds.getFullExtent();
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return null;
    }

    public int size() {
        try {
            return (int) ds.getRowCount();
        } catch (DriverException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    public List getFeatures() {
        return new FeatureListAdapter(ds);
    }

    public Iterator iterator() {
        return new FeatureIterator(ds);
    }

    public List query(Envelope envelope) {
        // TODO Auto-generated method stub
        return null;
    }

    public void add(Feature feature) {
        // TODO Auto-generated method stub
        
    }

    public void addAll(Collection features) {
        // TODO Auto-generated method stub
        
    }

    public void removeAll(Collection features) {
        // TODO Auto-generated method stub
        
    }

    public void remove(Feature feature) {
        // TODO Auto-generated method stub
        
    }

    public void clear() {
        // TODO Auto-generated method stub
        
    }

    public Collection remove(Envelope env) {
        // TODO Auto-generated method stub
        return null;
    }

}
