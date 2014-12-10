package org.orbisgis.wkguiapi;

import org.orbisgis.frameworkapi.CoreWorkspace;

import java.beans.PropertyChangeListener;

/**
 * @author Nicolas Fortin
 */
public interface ViewWorkspace {
    String PROP_DOCKINGLAYOUTFILE = "dockingLayoutFile";
    String PROP_SIFPATH = "SIFPath";
    String PROP_MAPCONTEXTPATH = "mapContextPath";

    /**
     * Get the value of mapContextPath
     * This folder contains all serialised Map Context shown in
     * the Map Context library
     * @return the value of mapContextPath
     */
    String getMapContextPath();

    /**
     *
     * @return The core workspace
     */
    CoreWorkspace getCoreWorkspace();

    /**
     * Get the value of SIFPath
     *
     * @return the value of SIFPath
     */
    String getSIFPath();

    /**
     * Get the value of dockingLayoutFile
     *
     * @return the value of dockingLayoutFile
     */
    String getDockingLayoutFile();

    /**
     * @return The full path of the layout file
     */
    String getDockingLayoutPath();

    /**
     * Add a property-change listener for all properties.
     * The listener is called for all properties.
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a property-change listener for a specific property.
     * The listener is called only when there is a change to
     * the specified property.
     * @param prop The static property name PROP_..
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    void addPropertyChangeListener(String prop, PropertyChangeListener listener);

    /**
     * Remove the specified listener from the list
     * @param listener The listener instance
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove the specified listener for a specified property from the list
     * @param prop The static property name PROP_..
     * @param listener The listener instance
     */
    void removePropertyChangeListener(String prop, PropertyChangeListener listener);
}
