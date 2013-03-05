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
package org.orbisgis.view.docking;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.swing.Icon;

/**
 * Parameters of a Docking Area.
 */
public class DockingAreaParameters implements Serializable {
    private static final long serialVersionUID = 3L; /*<! Update this integer while adding properties (1 for each new property)*/
    

    private PropertyChangeSupport propertySupport;
    
    public DockingAreaParameters() {
        propertySupport = new PropertyChangeSupport(this);
    }

    private String areaTitle = "";
    public static final String PROP_AREATITLE = "areaTitle";

    private Icon areaIcon;
    public static final String PROP_AREAICON = "areaIcon";
    private boolean acceptParentFlap = true;
    public static final String PROP_ACCEPTPARENTFLAP = "acceptParentFlap";

    /**
     * Get the value of acceptParentFlap
     *
     * @return the value of acceptParentFlap
     */
    public boolean isAcceptParentFlap() {
        return acceptParentFlap;
    }

    /**
     * Set the value of acceptParentFlap
     * If false, the Docking Area will refuse to be included
     * into a FlapDockStation instance.
     * @param acceptParentFlap new value of acceptParentFlap
     */
    public void setAcceptParentFlap(boolean acceptParentFlap) {
        boolean oldAcceptParentFlap = this.acceptParentFlap;
        this.acceptParentFlap = acceptParentFlap;
        propertySupport.firePropertyChange(PROP_ACCEPTPARENTFLAP, oldAcceptParentFlap, acceptParentFlap);
    }

    /**
     * Get the value of areaIcon
     *
     * @return the value of areaIcon
     */
    public Icon getAreaIcon() {
        return areaIcon;
    }

    /**
     * Set the value of areaIcon
     *
     * @param areaIcon new value of areaIcon
     */
    public void setAreaIcon(Icon areaIcon) {
        Icon oldAreaIcon = this.areaIcon;
        this.areaIcon = areaIcon;
        propertySupport.firePropertyChange(PROP_AREAICON, oldAreaIcon, areaIcon);
    }

    /**
     * Get the value of areaTitle
     *
     * @return the value of areaTitle
     */
    public String getAreaTitle() {
        return areaTitle;
    }

    /**
     * Set the value of areaTitle
     *
     * @param areaTitle new value of areaTitle
     */
    public void setAreaTitle(String areaTitle) {
        String oldAreaTitle = this.areaTitle;
        this.areaTitle = areaTitle;
        propertySupport.firePropertyChange(PROP_AREATITLE, oldAreaTitle, areaTitle);
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
