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
package org.orbisgis.view.docking.internals.actions;

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.CDropDownButton;
import bibliothek.gui.dock.common.action.CMenu;
import bibliothek.gui.dock.common.action.CRadioButton;
import bibliothek.gui.dock.common.action.CRadioGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.ActionTools;
import java.util.Map.Entry;

/**
 * Manage DockingFrames CActions, converted from Swing Actions.
 */
public class ToolBarActions {
    // Root actions
    private List<CAction> customActions = new ArrayList<CAction>();
    //Map with key the hashCode of ButtonGroup and value the corresponding CRadioGroup
    private Map<Integer,CRadioGroup> radioGroups = new HashMap<Integer,CRadioGroup>();

    /**
     *
     * @return The CAction created during the execution of convertToolBarToActions
     */
    public List<CAction> getCustomActions() {
        return customActions;
    }

    /**
     * Docking Frames doesn't provide the "Container" interface,
     * this method help to add item into multiple CAction container
     * @param container
     * @param item
     */
    private boolean addSubItem(CAction container,CAction item) {
        if(container instanceof CDropDownButton) {
            ((CDropDownButton)container).add(item);
        }else if(container instanceof CMenu) {
            ((CMenu)container).add(item);
        }else{
            return false;
        }
        return true;
    }
    private Action getAction(CAction action) {
        if(action instanceof CActionHolder) {
            return ((CActionHolder)action).getAction();
        } else {
            return null;
        }
    }
    /**
     * Find the most appropriate action insertion index.
     * This is sorting by insertion. But it doesn't guaranty to solve complex order issues.
     * @param actionList CAction containers
     * @param newAction Action to insert
     * @return Advised insertion id [0-actionList.getSize()]
     */
    private int getInsertionPosition(List<CAction> actionList, Action newAction) {
        if(ActionTools.isFirstInsertion(newAction)) {
            return 0;
        }
        for(int i=0;i<actionList.size();i++) {
            Action compAction = getAction(actionList.get(i));
            if(compAction!=null) {
                int position = ActionCommands.getInsertionPosition(i,newAction,compAction);
                if(position!=-1) {
                    return position;
                }
            }
        }
        return actionList.size();
    }

    /**
     * Convert swing Action to DockingFrame buttons.
     * @param actions Actions to convert
     */
    public void setActions(List<Action> actions) {
        customActions.clear();
        // Action containers
        Map<String,CAction> menuActions = new HashMap<String, CAction>();
        // Radio groups
        Map<String,CRadioGroup> actionGroup = new HashMap<String, CRadioGroup>();
        // At the time of writing there is no way to retrieve CAction container items
        // Then an intermediate list must be create in order to sort sub items.
        Map<String,ArrayList<CAction>> tempCActionContainer = new HashMap<String, ArrayList<CAction>>();

        // Create menu item container
        for(Action action : actions) {
            if(ActionTools.isMenu(action)) {
                String parentId = ActionTools.getParentMenuId(action);
                if(parentId.isEmpty()) {
                    menuActions.put(ActionTools.getMenuId(action), new CDropDownButtonExt(action));
                } else {
                    menuActions.put(ActionTools.getMenuId(action), new CMenuExt(action));
                }
            }
        }
        // Create menu item and put in containers

        for(Action action : actions) {
            // Retrieve Parent CAction
            String parentId = ActionTools.getParentMenuId(action);
            // Create menu item
            CAction cAction=null;
            if(ActionTools.isMenu(action)) {
                cAction = menuActions.get(ActionTools.getMenuId(action));
            } else {
                String toggleGroup = ActionTools.getToggleGroup(action);
                if(toggleGroup.isEmpty()) {
                    //Standard button,
                    // if it has no parent, the button is not created if it has no icons
                    if(!parentId.isEmpty() || ActionTools.getIcon(action)!=null) {
                        cAction = new CButtonExt(action);
                    }
                } else {
                    CRadioButton cRadioButton = new CToggleButton(action);
                    // Get CRadioGroup
                    CRadioGroup radioGroup = actionGroup.get(toggleGroup);
                    if(radioGroup==null) {
                        radioGroup = new CRadioGroup();
                        actionGroup.put(toggleGroup,radioGroup);
                    }
                    radioGroup.add(cRadioButton);
                    cAction = cRadioButton;
                }
            }
            if(cAction!=null) {
                // Insert the created CAction in a temporary container
                ArrayList<CAction> actionList = tempCActionContainer.get(parentId);
                if(actionList==null) {
                    actionList = new ArrayList<CAction>();
                    tempCActionContainer.put(parentId,actionList);
                }
                actionList.add(getInsertionPosition(actionList,action),cAction);
            }
        }
        // Insert CAction in each containers
        for(Entry<String,ArrayList<CAction>> entry : tempCActionContainer.entrySet()) {
            // Retrieve Parent CAction
            CAction parentAction=null;
            if(!entry.getKey().isEmpty()) {
                parentAction = menuActions.get(entry.getKey());
            }
            for(CAction cAction :entry.getValue()) {
                // Put CAction in root or action container.
                if(parentAction!=null) {
                    // Sub CAction
                    addSubItem(parentAction,cAction);
                } else {
                    // Root items
                    customActions.add(cAction);
                }
            }
        }
    }
}
