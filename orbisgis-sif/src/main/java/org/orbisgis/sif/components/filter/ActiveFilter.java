/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sif.components.filter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Manage the content of a single filter component instance.
 * 
 * This class is created thanks to the NetBeans user interface.
 * Use the "Add property" NetBeans function to add properties easily.
 * See documentation related to java.beans management systems
 * 
 * This class provide a serialisation algorithm
 * through readStream and writeStream
 */
public abstract class ActiveFilter {
    protected final PropertyChangeSupport propertySupport;
    private String factoryId;
    public static final String PROP_CURRENTFILTERVALUE = "currentFilterValue";
    
    /**
     * Bean constructor
     * @param factoryId The factory unique ID
     */
    protected ActiveFilter(String factoryId) {
        propertySupport = new PropertyChangeSupport(this);
        this.factoryId = factoryId;
    }

        @Override
        public boolean equals(Object obj) {
                if (!(obj instanceof ActiveFilter)) {
                        return false;
                }
                if (!this.factoryId.equals(((ActiveFilter)obj).factoryId)) {
                        return false;
                }
                return true;
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 53 * hash + this.factoryId.hashCode();
                return hash;
        }

    
    
    /**
     * Writes the content of this filter content into <code>out</code>.
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    public void writeStream( DataOutputStream out ) throws IOException {
            out.writeUTF(factoryId);
    }
    
    /**
     * Reads the content of this filter from <code>out</code>. All
     * properties should be set to their default value or to the value read
     * from the stream.
     * @param in the stream to read
     * @throws IOException if an I/O-error occurs
     */
    public void readStream( DataInputStream in ) throws IOException {
            factoryId = in.readUTF();
    }
    

    /**
     * Get the value of factoryId
     *
     * @return the value of factoryId
     */
    public String getFactoryId() {
        return factoryId;
    }  

    /**
     * Add a property-change listener for all properties.
     * The listener is called for all properties.
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    /**
     * Add a property-change listener for a specific property.
     * The listener is called only when there is a change to 
     * the specified property.
     * @param prop The static property name PROP_..
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    public void addPropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(prop, listener);
    }
    /**
     * Remove the specified listener from the list
     * @param listener The listener instance
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Remove the specified listener for a specified property from the list
     * @param prop The static property name PROP_..
     * @param listener The listener instance
     */
    public void removePropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(prop,listener);
    }
}
