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

package org.orbisgis.view.map.toolbar;

import org.orbisgis.view.components.actions.ActionTools;
import org.orbisgis.view.main.frames.ext.ToolBarAction;
import org.orbisgis.view.map.ext.AutomatonHolder;
import org.orbisgis.view.map.ext.MapEditorAction;
import org.orbisgis.view.map.ext.MapEditorExtension;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tool.ToolListener;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

/**
 * Link an Automaton with the MapEditor.
 * This action is quite complex because some listeners have to be installed
 * in order to update the state of this action, it depends on :
 * - Active layer
 * - Active automaton
 * @author Nicolas Fortin
 */
public class ActionAutomaton extends ActionMapContext implements AutomatonHolder {
    private Automaton automaton;
    // Update this tool Selected state
    private SynchroniseTool toolListener = new SynchroniseTool();

    /**
     * Constructor
     * @param actionId Unique action id
     * @param automaton Automaton instance
     * @param extension MapEditor instance
     */
    public ActionAutomaton(String actionId, Automaton automaton, MapEditorExtension extension) {
        super(actionId, automaton.getName(),extension,automaton.getImageIcon());
        this.automaton = automaton;
        putValue(Action.SHORT_DESCRIPTION,automaton.getTooltip());
        putValue(ActionTools.TOGGLE_GROUP, MapEditorAction.TOGGLE_GROUP_AUTOMATONS); //radio group
        putValue(ActionTools.LOGICAL_GROUP, ToolBarAction.DRAWING_GROUP);
        if(extension.getToolManager()!=null) {
            putValue(Action.SELECTED_KEY,isEqual(extension.getToolManager().getTool()));
        }
    }

    /**
     * @param otherAutomaton Automaton instance to compare with the Action's Automaton
     * @return True if it is the same
     */
    private boolean isEqual(Automaton otherAutomaton) {
        return otherAutomaton != null &&
                this.automaton.getClass().getName().equals(otherAutomaton.getClass().getName());
    }

    @Override
    protected void doInit() {
        super.doInit();
        if(getExtension().getToolManager()!=null) {
            getExtension().getToolManager().addToolListener(toolListener);
        }
    }

    /**
     * The linked MapEditor accept a new Map
     * @param evt
     */
    public void onMapEditorUpdate(PropertyChangeEvent evt) {
        super.onMapEditorUpdate(evt);
        if(MapEditorExtension.PROP_TOOL_MANAGER.equals(evt.getPropertyName())) {
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

    @Override
    public void dispose() {
        if(getExtension()!=null && getExtension().getToolManager()!=null) {
            getExtension().getToolManager().removeToolListener(toolListener);
        }
    }

    /**
     * When the tracked properties change this method is called in order to enable/disable automaton
     */
    @Override
    protected void checkActionState() {
        super.checkActionState();
        if(getExtension().getMapElement()!=null && getExtension().getToolManager()!=null) {
            boolean automatonState = automaton.isEnabled(getExtension().getMapElement().getMapContext(),getExtension().getToolManager());
            if(automatonState!=enabled) {
                setEnabled(automatonState);
            }
        }
    }

    @Override
    public Automaton getAutomaton() {
        return automaton;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(getValue(Action.SELECTED_KEY).equals(Boolean.TRUE) && getExtension().getToolManager()!=null) {
            getExtension().getToolManager().setTool(automaton);
        }
    }

    /**
     * In order to deactivate tools that are not in the same button group (different controls)
     * this toolListener disable unused Automaton.
     */
    private class SynchroniseTool implements ToolListener {

        @Override
        public void stateChanged(ToolManager toolManager) {
            checkActionState();
        }

        @Override
        public void transitionException(ToolManager toolManager, TransitionException e) {
            checkActionState();
        }

        @Override
        public void currentToolChanged(Automaton previous, ToolManager toolManager) {
            if(toolManager.getTool()!=null) {
                putValue(Action.SELECTED_KEY,isEqual(toolManager.getTool()));
            }
        }
    }
}
