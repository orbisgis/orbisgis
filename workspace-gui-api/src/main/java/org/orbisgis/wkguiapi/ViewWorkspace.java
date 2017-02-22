/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
     *
     * @return the value of mapContextPath
     */
    String getMapContextPath();

    /**
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
     *
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a property-change listener for a specific property.
     * The listener is called only when there is a change to
     * the specified property.
     *
     * @param prop     The static property name PROP_..
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    void addPropertyChangeListener(String prop, PropertyChangeListener listener);

    /**
     * Remove the specified listener from the list
     *
     * @param listener The listener instance
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove the specified listener for a specified property from the list
     *
     * @param prop     The static property name PROP_..
     * @param listener The listener instance
     */
    void removePropertyChangeListener(String prop, PropertyChangeListener listener);
}