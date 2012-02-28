/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.filters;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @brief Manage the content of a single filter component instance
 * 
 * This class is created thanks to the NetBeans user interface.
 * Use the "Add property" NetBeans function to add properties easily.
 * See documentation related to java.beans management systems
 * 
 */
public class ActiveFilter {
    private PropertyChangeSupport propertySupport;
    private final String factoryId;


    private String currentFilterValue = "";
    public static final String PROP_CURRENTFILTERVALUE = "currentFilterValue";

    /**
     * Bean constructor
     * @param factoryId The factory unique ID
     * @param currentFilterValue The filter value
     */
    public ActiveFilter(String factoryId,String currentFilterValue) {
        propertySupport = new PropertyChangeSupport(this);
        this.factoryId = factoryId;
        this.currentFilterValue = currentFilterValue;
    }

    /**
     * Get the value of currentFilterValue
     *
     * @return the value of currentFilterValue
     */
    public String getCurrentFilterValue() {
        return currentFilterValue;
    }

    /**
     * Set the value of currentFilterValue
     *
     * @param currentFilterValue new value of currentFilterValue
     */
    public void setCurrentFilterValue(String currentFilterValue) {
        String oldCurrentFilterValue = this.currentFilterValue;
        this.currentFilterValue = currentFilterValue;
        propertySupport.firePropertyChange(PROP_CURRENTFILTERVALUE, oldCurrentFilterValue, currentFilterValue);
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
