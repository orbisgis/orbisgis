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
package org.orbisgis.view.docking;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.swing.Icon;


/**
 * @brief Parameters of a panel in a docking environnement
 * Theses parameters indicate the behavior related to a panel
 * in a docking environnement.
 * 
 * This class is created thanks to the NetBeans user interface.
 * Use the "Add property" NetBeans function to add properties easily.
 * See documentation related to java.beans management systems
 * 
 * @warning New properties must be linked with the current docking system {@link OrbisGISView} 
 */
public class DockingPanelParameters implements Serializable {
    private static final long serialVersionUID = 6L; /*<! Update this integer while adding properties (1 for each new property)*/
    
    private PropertyChangeSupport propertySupport;
    private DockingAreaParameters dockingAreaParameters = null;
    public static final String PROP_DOCKINGAREAPARAMETERS = "dockingAreaParameters";

    private boolean minimizable = true;
    public static final String PROP_MINIMIZABLE = "minimizable";
    private boolean externalizable = true;
    public static final String PROP_EXTERNALIZABLE = "externalizable";

    /**
     * Get the value of externalizable
     *
     * @return the value of externalizable
     */
    public boolean isExternalizable() {
        return externalizable;
    }

    /**
     * Set the value of externalizable
     *
     * @param externalizable new value of externalizable
     */
    public void setExternalizable(boolean externalizable) {
        boolean oldExternalizable = this.externalizable;
        this.externalizable = externalizable;
        propertySupport.firePropertyChange(PROP_EXTERNALIZABLE, oldExternalizable, externalizable);
    }

    /**
     * Get the value of minimizable
     *
     * @return the value of minimizable
     */
    public boolean isMinimizable() {
        return minimizable;
    }

    /**
     * Set the value of minimizable
     *
     * @param minimizable new value of minimizable
     */
    public void setMinimizable(boolean minimizable) {
        boolean oldMinimizable = this.minimizable;
        this.minimizable = minimizable;
        propertySupport.firePropertyChange(PROP_MINIMIZABLE, oldMinimizable, minimizable);
    }

    /**
     * Get the value of dockingAreaParameters
     *
     * @return the value of dockingAreaParameters
     */
    public DockingAreaParameters getDockingAreaParameters() {
        return dockingAreaParameters;
    }
    private String name = "";
    public static final String PROP_NAME = "name";

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     * The internal name of the docking frames, this name
     * is used to load/restore the view state of the frames.
     * @warning When the name is not set, 
     * the state of this window will be lost at application stop.
     * @param name new value of name
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        propertySupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    /**
     * Set the value of dockingAreaParameters
     * @warning Only the instance of DockingManager should use this method
     * This method is called when the Docking Area is alive and the panel is shown.
     * @param dockingAreaParameters new value of dockingAreaParameters
     */
    public void setDockingAreaParameters(DockingAreaParameters dockingAreaParameters) {
        DockingAreaParameters oldDockingAreaParameters = this.dockingAreaParameters;
        this.dockingAreaParameters = dockingAreaParameters;
        propertySupport.firePropertyChange(PROP_DOCKINGAREAPARAMETERS, oldDockingAreaParameters, dockingAreaParameters);
    }

    private String title;
    public static final String PROP_TITLE = "title";
    
    private Icon titleIcon = null;
    public static final String PROP_TITLEICON = "titleIcon";
    private String dockingArea = "";
    public static final String PROP_DOCKINGAREA = "dockingArea";

    /**
     * Get the value of dockingArea
     *
     * @return the value of dockingArea
     */
    public String getDockingArea() {
        return dockingArea;
    }

    /**
     * Set the value of dockingArea
     * When it is a non-empty string dockingArea will restrict the placement
     * of the panel into a reserved area. This area will be shared only with
     * other panels that have the same docking area name.
     * @param dockingArea new value of dockingArea
     */
    public void setDockingArea(String dockingArea) {
        String oldDockingArea = this.dockingArea;
        this.dockingArea = dockingArea;
        propertySupport.firePropertyChange(PROP_DOCKINGAREA, oldDockingArea, dockingArea);
    }

    /**
     * Get the value of titleIcon
     *
     * @return the value of titleIcon
     */
    public Icon getTitleIcon() {
        return titleIcon;
    }

    /**
     * Set the value of titleIcon
     *
     * @param titleIcon new value of titleIcon
     */
    public void setTitleIcon(Icon titleIcon) {
        Icon oldTitleIcon = this.titleIcon;
        this.titleIcon = titleIcon;
        propertySupport.firePropertyChange(PROP_TITLEICON, oldTitleIcon, titleIcon);
    }


    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param title new value of title
     */
    public void setTitle(String title) {
        String oldTitle = this.title;
        this.title = title;
        propertySupport.firePropertyChange(PROP_TITLE, oldTitle, title);
    }
    
    /**
     * Default constructor
     */
    public DockingPanelParameters() {
        propertySupport = new PropertyChangeSupport(this);
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
