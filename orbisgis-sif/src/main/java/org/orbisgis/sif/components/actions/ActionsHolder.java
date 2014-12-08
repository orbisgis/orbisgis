/*
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

package org.orbisgis.sif.components.actions;

import javax.swing.Action;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * A class that hold swing Actions. Used by plugin system helper {@link MenuItemServiceTracker}.
 * @author Nicolas Fortin
 */
public interface ActionsHolder {
        public static final String PROP_ACTIONS = "actions";

        /**
         * Add action and show in registered control.
         * @param action action to show
         */
        void addAction(Action action);
        /**
         * Add action list and show in all registered controls.
         * @param newActions Action list
         */
        void addActions(List<Action> newActions);

        /**
         * Remove this action list of all registered controls.
         * PropertyChange listeners of action will remove all related menu items.
         * @param action action to remove
         * @return True if the action has been successfully removed
         */
        public boolean removeAction(Action action) ;
        /**
         * Remove this action list of all registered controls.
         * PropertyChange listeners of action will remove all related menu items.
         * @param actionList
         */
        public void removeActions(List<Action> actionList);


        /**
         * Add a property-change listener for all properties.
         * The listener is called for all properties.
         * @param listener The PropertyChangeListener instance
         * @note Use EventHandler.create to build the PropertyChangeListener instance
         */
        public void addPropertyChangeListener(PropertyChangeListener listener);

        /**
         * Add a property-change listener for a specific property.
         * The listener is called only when there is a change to
         * the specified property.
         * @param prop The static property name PROP_..
         * @param listener The PropertyChangeListener instance
         * @note Use EventHandler.create to build the PropertyChangeListener instance
         */
        public void addPropertyChangeListener(String prop,PropertyChangeListener listener);
        /**
         * Remove the specified listener from the list
         * @param listener The listener instance
         */
        public void removePropertyChangeListener(PropertyChangeListener listener);

        /**
         * Remove the specified listener for a specified property from the list
         * @param prop The static property name PROP_..
         * @param listener The listener instance
         */
        public void removePropertyChangeListener(String prop,PropertyChangeListener listener);
}
