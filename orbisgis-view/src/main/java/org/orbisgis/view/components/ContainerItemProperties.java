/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.components;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * Generic list item that store a key with the value shown
 * 
 * This class is created thanks to the NetBeans user interface.
 * Use the "Add property" NetBeans function to add properties easily.
 * See documentation related to java.beans management systems
 * 
 */
public class ContainerItemProperties implements Serializable {
    public static final long serialVersionUID = 2L; /*<! Update this integer while adding properties (1 for each new property)*/
    private PropertyChangeSupport propertySupport;
    
    private String label; //I18N label
    public static final String PROP_LABEL = "label";
    
    private final String key;   //Internal name of the item
    /**
     * Constructor
     * @param label The I18N label of the Item, shown in the GUI
     * @param key The internal name of this item, retrieved by listeners for processing
     */
    public ContainerItemProperties(String key, String label) {
        propertySupport = new PropertyChangeSupport(this);
        this.label = label;
        this.key = key;
    }
    /**
     * 
     * @return The internal name of this item
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the I18N GUI label
     * @return the I18N GUI label
     */
    public String getLabel() {
        return label;
    }
    /**
     * Set the I18N GUI label
     * @param label the I18N GUI label
     */
    public void setLabel(String label) {
        String oldLabel = this.label;
        this.label = label;
        propertySupport.firePropertyChange(PROP_LABEL, oldLabel, label);
    }

    /**
     * The GUI representation of the item
     * @return String GUI representation of the item
     */
    @Override
    public String toString() {
        return label;
    }

    /**
     * The equal is done on the KEY internal name.
     * @param obj Other item of the list
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContainerItemProperties other = (ContainerItemProperties) obj;
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
            return false;
        }
        return true;
    }

    /**
     * Compute hashcode for the item
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.key != null ? this.key.hashCode() : 0);
        return hash;
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
