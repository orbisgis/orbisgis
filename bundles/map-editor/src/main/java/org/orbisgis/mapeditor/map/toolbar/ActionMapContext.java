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

package org.orbisgis.mapeditor.map.toolbar;

import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableEditListener;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.LayerCollectionEvent;
import org.orbisgis.coremap.layerModel.LayerListener;
import org.orbisgis.coremap.layerModel.LayerListenerAdapter;
import org.orbisgis.coremap.layerModel.LayerListenerEvent;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.SelectionEvent;
import org.orbisgis.mapeditor.map.MapEditor;
import org.orbisgis.mapeditorapi.MapEditorExtension;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.components.actions.DefaultAction;

import javax.swing.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An action that depend on the MapContext of the MapEditor
 * This action is quite complex because some listeners have to be installed
 * in order to update the state of this action, it depends on the loaded MapElement,MapContext, Layers and table updates.
 * This action must be released by calling the {@link ActionDisposable#dispose()}
 * @author Nicolas Fortin
 */
public abstract class ActionMapContext extends DefaultAction implements ActionDisposable {
    private MapEditor extension;
    // To install, uninstall listener on MapContext
    private PropertyChangeListener mapEditorListener = EventHandler.create(PropertyChangeListener.class, this, "onMapEditorUpdate","");
    // To update this Action Enabled state
    private PropertyChangeListener mapContextListener = EventHandler.create(PropertyChangeListener.class, this, "onMapContextUpdate","");
    // To update this Action Enabled state
    private final PropertyChangeListener layerListener = EventHandler.create(PropertyChangeListener.class, this, "onLayerUpdate","");
    private final LayerListenerAdd layerAddRemoveListener = new LayerListenerAdd(layerListener, this);

    private AtomicBoolean isInitialised = new AtomicBoolean(false);
    private Set<String> trackedMapContextProperties = new HashSet<String>();
    private Set<String> trackedLayersProperties = new HashSet<>();
    private boolean trackDataManager = false;

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
        this.extension = ((MapEditor)extension);
    }

    /**
     * @param trackDataManager True to track updates on tables that are shown in loaded map context
     */
    public void setTrackDataManager(boolean trackDataManager) {
        this.trackDataManager = trackDataManager;
    }

    @Override
    public boolean isEnabled() {
        checkActionState();
        return super.isEnabled();
    }

    /**
     * @return The Map extension linked with this Automaton Action
     */
    public MapEditor getExtension() {
        return extension;
    }

    /**
     * The automaton activation state (enabled/disabled) will be checked
     * when one of trackedMapContextProperties is updated.
     * @param trackedMapContextProperties One of MapContext#PROP_*
     * @return this
     */
    public ActionMapContext setTrackedMapContextProperties(String... trackedMapContextProperties) {
        this.trackedMapContextProperties = new HashSet<>(Arrays.asList(trackedMapContextProperties));
        return this;
    }

    /**
     * The automaton activation state (enabled/disabled) will be checked
     * when one of trackedLayersProperties is updated.
     * @param trackedLayersProperties One of {@link org.orbisgis.coremap.layerModel.ILayer#PROP_SELECTION}
     * @return this
     */
    public ActionMapContext setTrackedLayersProperties(String... trackedLayersProperties) {
        this.trackedLayersProperties = new HashSet<>(Arrays.asList(trackedLayersProperties));
        return this;
    }

    /**
     * The automaton activation state (enabled/disabled) will be checked
     * when propertyName is updated.
     * @param propertyName One of {@link org.orbisgis.coremap.layerModel.MapContext#PROP_ACTIVELAYER}
     * @return this
     */
    public ActionMapContext addTrackedMapContextProperty(String propertyName) {
        trackedMapContextProperties.add(propertyName);
        return this;
    }


    private void init() {
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
        removeListener(mapContext.getLayerModel(), layerListener, layerAddRemoveListener);
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
        if(trackDataManager || !trackedLayersProperties.isEmpty()) {
            installLayerListener(mapContext.getLayerModel());
        }
        checkActionState();
    }

    private void installLayerListener(ILayer layer) {
        layer.addLayerListener(layerAddRemoveListener);
        if(!trackedLayersProperties.isEmpty()) {
            layer.addPropertyChangeListener(layerListener);
        }
        if(trackDataManager && !layer.getTableReference().isEmpty() && layer.getDataManager() != null) {
            DataManager dataManager = layer.getDataManager();
            dataManager.addTableEditListener(layer.getTableReference(), layerAddRemoveListener  , false);
        }
        if(layer.acceptsChilds()) {
            for(ILayer layerChild : layer.getChildren()) {
                installLayerListener(layerChild);
            }
        }
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
     * The Map active Layer (the edited one) has been updated
     * @param evt
     */
    public void onLayerUpdate(PropertyChangeEvent evt) {
        if(trackedLayersProperties.contains(evt.getPropertyName())) {
            checkActionState();
        }
    }

    private static void removeListener(ILayer layer, PropertyChangeListener layerListener, LayerListenerAdd layerAddRemoveListener) {
        layer.removeLayerListener(layerAddRemoveListener);
        layer.removePropertyChangeListener(layerListener);
        if(!layer.getTableReference().isEmpty() && layer.getDataManager() != null) {
            layer.getDataManager().removeTableEditListener(layer.getTableReference(), layerAddRemoveListener);
        }
        for (int i = 0; i < layer.getLayerCount(); i++) {
            removeListener(layer.getLayer(i), layerListener, layerAddRemoveListener);
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

    private static class LayerListenerAdd extends LayerListenerAdapter implements TableEditListener {
        private PropertyChangeListener layerListener;
        private Action action;

        public LayerListenerAdd(PropertyChangeListener layerListener, Action action) {
            this.layerListener = layerListener;
            this.action = action;
        }

        private void addListener(ILayer layer) {
            layer.addLayerListener(this);
            layer.addPropertyChangeListener(layerListener);
            for (int i = 0; i < layer.getLayerCount(); i++) {
                addListener(layer.getLayer(i));
            }
        }

        @Override
        public void tableChange(TableEditEvent event) {
            // Update enabled state
            action.isEnabled();
        }

        @Override
        public void layerAdded(LayerCollectionEvent e) {
            for(ILayer layer : e.getAffected()) {
                addListener(layer);
            }
        }

        @Override
        public void layerRemoved(LayerCollectionEvent e) {
            for(ILayer layer : e.getAffected()) {
                removeListener(layer, layerListener, this);
            }
        }
    }
}
