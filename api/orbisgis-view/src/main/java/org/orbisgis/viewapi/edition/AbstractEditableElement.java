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

package org.orbisgis.viewapi.edition;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class provide a default implementation of the Editable Element
 * @author Nicolas Fortin
 */
public abstract class AbstractEditableElement implements EditableElement {
        protected transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

        // Properties
        protected String id = "none";
        protected boolean modified = false;
        protected boolean open = false;

        /**
         * Add a property-change listener for all properties.
         * The listener is called for all properties.
         * @param listener The PropertyChangeListener instance
         * @note Use EventHandler.create to build the PropertyChangeListener instance
         */
        public void addPropertyChangeListener(PropertyChangeListener listener) {
                propertyChangeSupport.addPropertyChangeListener(listener);
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
                propertyChangeSupport.addPropertyChangeListener(prop, listener);
        }
        /**
         * Remove the specified listener from the list
         * @param listener The listener instance
         */
        public void removePropertyChangeListener(PropertyChangeListener listener) {
                propertyChangeSupport.removePropertyChangeListener(listener);
        }

        /**
         * Remove the specified listener for a specified property from the list
         * @param prop The static property name PROP_..
         * @param listener The listener instance
         */
        public void removePropertyChangeListener(String prop,PropertyChangeListener listener) {
                propertyChangeSupport.removePropertyChangeListener(prop,listener);
        }

        /**
         * Return the Id of this element (instance).
         *
         * @return the value of id
         */
        public String getId() {
                return id;
        }

        /**
         * Set the value of id
         *
         * @param id new value of id
         */
        protected void setId(String id) {
                String oldId = this.id;
                this.id = id;
                propertyChangeSupport.firePropertyChange(PROP_ID, oldId, id);
        }


        /**
         * Get the state of the editable element
         *
         * @return True if this element was modified since the last time save
         * or open was called
         */
        public boolean isModified() {
                return modified;
        }

        /**
         * Set the value of modified
         *
         * @param modified new value of modified
         */
        public void setModified(boolean modified) {
                boolean oldModified = this.modified;
                this.modified = modified;
                propertyChangeSupport.firePropertyChange(PROP_MODIFIED, oldModified, modified);
        }

        /**
         * True, if this editable is open by the editor
         * @return the value of open
         */
        public boolean isOpen() {
                return open;
        }

        /**
         * Set the value of the state of the editable element
         *
         * @param open new value of open
         */
        protected void setOpen(boolean open) {
                boolean oldOpen = this.open;
                this.open = open;
                propertyChangeSupport.firePropertyChange(PROP_OPEN, oldOpen, open);
        }
}
