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
package org.orbisgis.view.docking;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

import org.orbisgis.view.docking.DockingLocation.Location;
import org.orbisgis.view.util.PropertyHost;
import org.orbisgis.view.util.VetoablePropertyHost;


/**
 * Parameters of a panel in a docking environment
 * Theses parameters indicate the behaviour related to a panel
 * in a docking environment.
 * 
 * This class is created thanks to the NetBeans user interface.
 * Use the "Add property" NetBeans function to add properties easily.
 * See documentation related to java.beans management systems
 * 
 * Using parameter beans instead of implementing docking frame panels
 * help to extends/update functionality of application without breaking codes
 * 
 * New properties must be linked with the current docking system org.orbisgis.view.docking.internals.OrbisGISView
 */
public class DockingPanelParameters implements Serializable, PropertyHost, VetoablePropertyHost {

        private static final long serialVersionUID = 12L; /*
         * <! Update this integer while adding properties (1 for each new property)
         */
        // Static property name

        public static final String PROP_MINIMIZABLE = "minimizable";
        public static final String PROP_EXTERNALIZABLE = "externalizable";
        public static final String PROP_DOCKINGAREAPARAMETERS = "dockingAreaParameters";
        public static final String PROP_CLOSEABLE = "closeable";
        public static final String PROP_VISIBLE = "visible"; // Vetoable
        public static final String PROP_NAME = "name";
        public static final String PROP_TITLE = "title";
        public static final String PROP_TITLEICON = "titleIcon";
        public static final String PROP_DOCKINGAREA = "dockingArea";
        public static final String PROP_LAYOUT = "layout";
        public static final String PROP_DEFAULTDOCKINGLOCATION = "defaultDockingLocation";
        public static final String PROP_DOCK_ACTIONS = "dockActions";
        
        // Property Change Support
        private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
        private VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);


        // Private property
        private String title;
        private Icon titleIcon = null;
        private String dockingArea = "";
        private DockingAreaParameters dockingAreaParameters = null;
        private boolean minimizable = true;
        private boolean externalizable = true;
        private boolean closeable = true;
        private boolean visible = true;
        private String name = "";
        private DockingPanelLayout layout = null;        
        private DockingLocation defaultDockingLocation = new DockingLocation(Location.TOP_OF, "");
        private List<Action> dockActions = new ArrayList<Action>();

        /**
         * Get the value of defaultDockingLocation
         *
         * @return the value of defaultDockingLocation
         */
        public DockingLocation getDefaultDockingLocation() {
                return defaultDockingLocation;
        }

        /**
         * Set the value of defaultDockingLocation
         * This location is read on the panel creation and when this property is changed
         * @param defaultDockingLocation new value of defaultDockingLocation
         */
        public void setDefaultDockingLocation(DockingLocation defaultDockingLocation) {
                DockingLocation oldDefaultDockingLocation = this.defaultDockingLocation;
                this.defaultDockingLocation = defaultDockingLocation;
                propertySupport.firePropertyChange(PROP_DEFAULTDOCKINGLOCATION, oldDefaultDockingLocation, defaultDockingLocation);
        }

        /**
         * Get the value of layout
         *
         * @return the value of layout
         */
        public DockingPanelLayout getLayout() {
                return layout;
        }

        /**
         * Set the value of layout
         * This layout can be used to add persistence on your frame.
         * For editors, the layout is provided by the factory
         * @param layout new value of layout
         */
        public void setLayout(DockingPanelLayout layout) {
                DockingPanelLayout oldLayout = this.layout;
                this.layout = layout;
                propertySupport.firePropertyChange(PROP_LAYOUT, oldLayout, layout);
        }

        /**
         * Get the value of visible
         *
         * @return the value of visible
         */
        public boolean isVisible() {
                return visible;
        }

        /**
         * Set the value of visible
         * @param visible new value of visible
         * @return true if the update has not been vetoed
         */
        public boolean setVisible(boolean visible) {
                try {
                    vetoableChangeSupport.fireVetoableChange(PROP_VISIBLE,this.visible, visible);
                    boolean oldVisible = this.visible;
                    this.visible = visible;
                    propertySupport.firePropertyChange(PROP_VISIBLE, oldVisible, visible);
                    return true;
                } catch (Exception ex) {
                    return false;
                }
        }

        /**
         * Get the value of closeable
         *
         * @return the value of closeable
         */
        public boolean isCloseable() {
                return closeable;
        }

        /**
         * Set the value of closeable
         *
         * @param closeable new value of closeable
         */
        public void setCloseable(boolean closeable) {
                boolean oldCloseable = this.closeable;
                this.closeable = closeable;
                propertySupport.firePropertyChange(PROP_CLOSEABLE, oldCloseable, closeable);
        }

        /**
         * Get the value of externalizable
         *
         * @return the value of externalizable
         */
        public boolean isExternalizable() {
                return externalizable;
        }

        /**
         * Set the value of externalisable
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

        /**
         * Get the value of name
         *
         * @return the value of name
         */
        public String getName() {
                return name;
        }

        /**
         * Set the value of name The internal name of the docking frames, this
         * name is used to load/restore the view state of the frames. @warning
         * When the name is not set, the state of this window will be lost at
         * application stop.
         *
         * @param name new value of name
         */
        public void setName(String name) {
                String oldName = this.name;
                this.name = name;
                propertySupport.firePropertyChange(PROP_NAME, oldName, name);
        }

        /**
         * Set the value of dockingAreaParameters @warning Only the instance of
         * DockingManager should use this method This method is called when the
         * Docking Area is alive and the panel is shown.
         *
         * @param dockingAreaParameters new value of dockingAreaParameters
         */
        public void setDockingAreaParameters(DockingAreaParameters dockingAreaParameters) {
                DockingAreaParameters oldDockingAreaParameters = this.dockingAreaParameters;
                this.dockingAreaParameters = dockingAreaParameters;
                propertySupport.firePropertyChange(PROP_DOCKINGAREAPARAMETERS, oldDockingAreaParameters, dockingAreaParameters);
        }

        /**
         * Get the value of dockingArea
         *
         * @return the value of dockingArea
         */
        public String getDockingArea() {
                return dockingArea;
        }

        /**
         * Set the value of dockingArea When it is a non-empty string
         * dockingArea will restrict the placement of the panel into a reserved
         * area. This area will be shared only with other panels that have the
         * same docking area name.
         *
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

        public void addPropertyChangeListener(PropertyChangeListener listener) {
                propertySupport.addPropertyChangeListener(listener);
        }

        public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
                propertySupport.addPropertyChangeListener(prop, listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
                propertySupport.removePropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
                propertySupport.removePropertyChangeListener(prop, listener);
        }

        @Override
        public void addVetoableChangeListener(VetoableChangeListener vetoableChangeListener) {
                vetoableChangeSupport.addVetoableChangeListener(vetoableChangeListener);
        }

        @Override
        public void removeVetoableChangeListener(VetoableChangeListener vetoableChangeListener) {
                vetoableChangeSupport.removeVetoableChangeListener(vetoableChangeListener);
        }

        @Override
        public void addVetoableChangeListener(String s, VetoableChangeListener vetoableChangeListener) {
                vetoableChangeSupport.addVetoableChangeListener(s,vetoableChangeListener);
        }

        @Override
        public void removeVetoableChangeListener(String s, VetoableChangeListener vetoableChangeListener) {
                vetoableChangeSupport.removeVetoableChangeListener(s,vetoableChangeListener);
        }

        /**
         * Replace the current list of Buttons on the docking frame action bar.
         * Additional buttons type and placement can be given by using {@link org.orbisgis.view.components.actions.ActionTools}
         * Actions without icons are not shown.
         * @param actions New action list.
         */
        public void setDockActions(List<Action> actions) {
                List<Action> oldList = new ArrayList<Action>(dockActions);
                dockActions = new ArrayList<Action>(actions);
                propertySupport.firePropertyChange(PROP_DOCK_ACTIONS,oldList,dockActions);
        }

        /**
         * Get the current button action shown on the action toolbar.
         * @return Unmodifiable current action list.
         */
        public List<Action> getDockActions() {
                return Collections.unmodifiableList(dockActions);
        }
}
