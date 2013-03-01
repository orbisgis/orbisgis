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

package org.orbisgis.view.map;

import com.sun.media.jai.rmi.HashSetState;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.components.actions.ActionTools;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.map.ext.AutomatonHolder;
import org.orbisgis.view.map.ext.MapEditorAction;
import org.orbisgis.view.map.ext.MapEditorExtension;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tool.ToolListener;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Link an Automaton with the MapEditor.
 * This action is quite complex because some listeners have to be installed
 * in order to update the state of this action, it depends on :
 * - Active layer
 * - Active automaton
 * This action must be released by calling the {@link AutomatonAction#disposeAutomaton()}
 * @author Nicolas Fortin
 */
public class AutomatonAction extends DefaultAction implements AutomatonHolder {
    private Automaton automaton;
    private MapEditorExtension extension;
    // Update this tool Selected state
    private SynchroniseTool toolListener = new SynchroniseTool();
    // To install, uninstall listener on MapContext
    private PropertyChangeListener mapEditorListener = EventHandler.create(PropertyChangeListener.class, this, "onMapEditorUpdate","");
    // To update this Action Enabled state
    private PropertyChangeListener mapContextListener = EventHandler.create(PropertyChangeListener.class, this, "onMapContextUpdate","");
    private AtomicBoolean isInitialised = new AtomicBoolean(false);
    private Set<String> trackedMapContextProperties = new HashSet<String>();

    public AutomatonAction(String actionId, Automaton automaton, MapEditorExtension extension) {
        super(actionId, automaton.getName());
        this.extension = extension;
        this.automaton = automaton;
        putValue(Action.SMALL_ICON,automaton.getImageIcon());
        putValue(Action.LARGE_ICON_KEY,automaton.getImageIcon());
        putValue(Action.SHORT_DESCRIPTION,automaton.getTooltip());
        putValue(ActionTools.TOGGLE_GROUP, MapEditorAction.TOGGLE_GROUP_AUTOMATONS); //radio group
        if(extension.getToolManager()!=null) {
            putValue(Action.SELECTED_KEY,isEqual(extension.getToolManager().getTool()));
        }
    }

    /**
     * @param otherAutomaton Automaton instance to compare with the Action's Automaton
     * @return True if it is the same
     */
    private boolean isEqual(Automaton otherAutomaton) {
        if(otherAutomaton!=null) {
            return this.automaton.getClass().getName().equals(otherAutomaton.getClass().getName());
        } else {
            return false;
        }
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
    public AutomatonAction setTrackedMapContextProperties(Collection<String> trackedMapContextProperties) {
        this.trackedMapContextProperties = new HashSet<String>(trackedMapContextProperties);
        return this;
    }

    /**
     * The automaton activation state (enabled/disabled) will be checked
     * when propertyName is updated.
     * @param propertyName One of MapContext#PROP_*
     * @return this
     */
    public AutomatonAction addTrackedMapContextProperty(String propertyName) {
        trackedMapContextProperties.add(propertyName);
        return this;
    }
    private void init() {
        if(!isInitialised.getAndSet(true)) {
            if(extension.getToolManager()!=null) {
                extension.getToolManager().addToolListener(toolListener);
            }
            extension.addPropertyChangeListener(mapEditorListener);
            if(!trackedMapContextProperties.isEmpty()) {
                if(extension.getMapElement()!=null) {
                    installMapContextListener(extension.getMapElement().getMapContext());
                }
            }
        }
    }
    private void removeMapContextListener(MapContext mapContext) {
        mapContext.removePropertyChangeListener(mapContextListener);
    }
    private void installMapContextListener(MapContext mapContext) {
        if(trackedMapContextProperties.size()==1) {
            mapContext.addPropertyChangeListener(trackedMapContextProperties.iterator().next(),mapContextListener);
        } else if(!trackedMapContextProperties.isEmpty()) {
            mapContext.addPropertyChangeListener(mapContextListener);
        }
        checkAutomatonState();
    }
    /**
     * The linked MapEditor accept a new Map
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
        } else if(MapEditorExtension.PROP_TOOL_MANAGER.equals(evt.getPropertyName())) {
            ToolManager oldTool = (ToolManager)evt.getOldValue();
            ToolManager newTool = (ToolManager)evt.getNewValue();
            if(oldTool!=null) {
                oldTool.removeToolListener(toolListener);
            }
            if(newTool!=null) {
                newTool.addToolListener(toolListener);
            }
        }
    }

    /**
     * The Map active Layer (the edited one) has been updated
     * @param evt
     */
    public void onMapContextUpdate(PropertyChangeEvent evt) {
        if(trackedMapContextProperties.contains(evt.getPropertyName())) {
            checkAutomatonState();
        }
    }
    /**
     * Remove listeners added by this action.
     */
    public void disposeAutomaton() {
        if(extension!=null && extension.getToolManager()!=null) {
            extension.getToolManager().removeToolListener(toolListener);
            extension.removePropertyChangeListener(mapEditorListener);
            if(extension.getMapElement()!=null) {
                removeMapContextListener(extension.getMapElement().getMapContext());
            }
        }
        extension = null;
    }

    /**
     * When the tracked properties change this method is called in order to enable/disable automaton
     */
    protected void checkAutomatonState() {
        init();
        if(extension.getMapElement()!=null && extension.getToolManager()!=null) {
            boolean automatonState = automaton.isEnabled(extension.getMapElement().getMapContext(),extension.getToolManager());
            if(automatonState!=enabled) {
                setEnabled(automatonState);
            }
        }
    }
    @Override
    public boolean isEnabled() {
        checkAutomatonState();
        return super.isEnabled();
    }

    /**
     * Get the automaton hold by this action.
     * @return Automation instance
     */
    public Automaton getAutomaton() {
        return automaton;
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(getValue(Action.SELECTED_KEY).equals(Boolean.TRUE) && extension.getToolManager()!=null) {
            extension.getToolManager().setTool(automaton);
        }
    }

    /**
     * In order to deactivate tools that are not in the same button group (different controls)
     * this toolListener disable unused Automaton.
     */
    private class SynchroniseTool implements ToolListener {

        @Override
        public void stateChanged(ToolManager toolManager) {
            checkAutomatonState();
        }

        @Override
        public void transitionException(ToolManager toolManager, TransitionException e) {
            checkAutomatonState();
        }

        @Override
        public void currentToolChanged(Automaton previous, ToolManager toolManager) {
            if(toolManager.getTool()!=null) {
                putValue(Action.SELECTED_KEY,isEqual(toolManager.getTool()));
            }
        }
    }
}
