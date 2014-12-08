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

package org.orbisgis.mapeditor.map.toolbar;

import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.mapeditor.map.ext.MapEditorExtension;

import javax.swing.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An action that depend on the MapContext of the MapEditor
 * This action is quite complex because some listeners have to be installed
 * in order to update the state of this action, it depends on the loaded MapElement
 * This action must be released by calling the {@link ActionDisposable#dispose()}
 * @author Nicolas Fortin
 */
public abstract class ActionMapContext extends DefaultAction implements ActionDisposable {
    private MapEditorExtension extension;
    // To install, uninstall listener on MapContext
    private PropertyChangeListener mapEditorListener = EventHandler.create(PropertyChangeListener.class, this, "onMapEditorUpdate","");
    // To update this Action Enabled state
    private PropertyChangeListener mapContextListener = EventHandler.create(PropertyChangeListener.class, this, "onMapContextUpdate","");
    private AtomicBoolean isInitialised = new AtomicBoolean(false);
    private Set<String> trackedMapContextProperties = new HashSet<String>();

    /**
     * Constructor
     * @param actionId Unique action id
     * @param name Action name
     * @param extension MapEditor instance
     * @param icon Action icon
     */
    public ActionMapContext(String actionId,String name, MapEditorExtension extension,Icon icon) {
        super(actionId, name);
        putValue(Action.SMALL_ICON,icon);
        putValue(Action.LARGE_ICON_KEY,icon);
        this.extension = extension;
    }

    @Override
    public boolean isEnabled() {
        checkActionState();
        return super.isEnabled();
    }

    /**
     * @return The Map extension linked with this Automaton Action
     */
    public MapEditorExtension getExtension() {
        return extension;
    }

    /**
     * The automaton activation state (enabled/disabled) will be checked
     * when one of trackedMapContextProperties is updated.
     * @param trackedMapContextProperties One of MapContext#PROP_*
     * @return this
     */
    public ActionMapContext setTrackedMapContextProperties(Collection<String> trackedMapContextProperties) {
        this.trackedMapContextProperties = new HashSet<String>(trackedMapContextProperties);
        return this;
    }

    /**
     * The automaton activation state (enabled/disabled) will be checked
     * when propertyName is updated.
     * @param propertyName One of MapContext#PROP_*
     * @return this
     */
    public ActionMapContext addTrackedMapContextProperty(String propertyName) {
        trackedMapContextProperties.add(propertyName);
        return this;
    }
    private final void init() {
        if(!isInitialised.getAndSet(true)) {
                doInit();
        }
    }

    /**
     * Initialisation of this Action.
     * Register listeners
     */
    protected void doInit() {
            extension.addPropertyChangeListener(mapEditorListener);
            if(!trackedMapContextProperties.isEmpty()) {
                    if(extension.getMapElement()!=null) {
                            installMapContextListener(extension.getMapElement().getMapContext());
                    }
            }
    }

    /**
     * Remove listeners related to this MapContext
     * @param mapContext MapContext instance
     */
    protected void removeMapContextListener(MapContext mapContext) {
        mapContext.removePropertyChangeListener(mapContextListener);
    }

    /**
     * Add listeners related to this MapContext
     * @param mapContext
     */
    protected void installMapContextListener(MapContext mapContext) {
        if(trackedMapContextProperties.size()==1) {
            mapContext.addPropertyChangeListener(trackedMapContextProperties.iterator().next(),mapContextListener);
        } else if(!trackedMapContextProperties.isEmpty()) {
            mapContext.addPropertyChangeListener(mapContextListener);
        }
        checkActionState();
    }
    /**
     * Called on MapEditor property update
     * @param evt
     */
    public void onMapEditorUpdate(PropertyChangeEvent evt) {
        if(MapEditorExtension.PROP_MAP_ELEMENT.equals(evt.getPropertyName())) {
            MapElement oldMap = (MapElement)evt.getOldValue();
            MapElement newMap = (MapElement)evt.getNewValue();
            if(oldMap!=null) {
                removeMapContextListener(oldMap.getMapContext());
            }
            if(newMap!=null) {
                installMapContextListener(newMap.getMapContext());

            }
        }
    }

    /**
     * The Map active Layer (the edited one) has been updated
     * @param evt
     */
    public void onMapContextUpdate(PropertyChangeEvent evt) {
        if(trackedMapContextProperties.contains(evt.getPropertyName())) {
            checkActionState();
        }
    }

    /**
     * A watch property of the MapContext has been fired.
     */
    protected void checkActionState() {
        init();
    }

    @Override
    public void dispose() {
        if(extension!=null) {
            extension.removePropertyChangeListener(mapEditorListener);
            if(extension.getMapElement()!=null) {
                removeMapContextListener(extension.getMapElement().getMapContext());
            }
        }
        extension = null;
    }
}
