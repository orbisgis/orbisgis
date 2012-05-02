/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.docking.internals;

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.CButton;
import bibliothek.gui.dock.common.action.CRadioGroup;
import bibliothek.gui.dock.common.action.CSeparator;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import org.orbisgis.core.events.Listener;
import org.orbisgis.view.docking.actions.CToggleButton;

/**
 * This class is responsible of converting JToolBar into custom DockingFrames CActions
 */


public class ToolBarActions {
    
    private List<CAction> customActions = new ArrayList<CAction>();
    //Map with key the hashCode of ButtonGroup and value the corresponding CRadioGroup
    private Map<Integer,CRadioGroup> radioGroups = new HashMap<Integer,CRadioGroup>();
    
    /**
     * Copy action listener to docking frames button
     * @param from
     * @param to 
     */
    private void transferActionsListeners(AbstractButton from , CButton to) {
        ActionListener[] listeners = from.getActionListeners();
        for(ActionListener listener : listeners) {
            to.addActionListener(listener);
        }
    }
    /**
     * 
     * @return The CAction created during the execution of convertToolBarToActions
     */
    public List<CAction> getCustomActions() {
        return customActions;
    }
    /**
     * Convert the swing toolbar into docking frames CAction
     */
    public void convertToolBarToActions(JToolBar viewToolBar) {
        if(viewToolBar!=null) {
            customActions.clear();
            Component[] components = viewToolBar.getComponents();
            //For each toolbar components
            for(Component component : components) {
                CAction action=null;
                if(component instanceof JToggleButton) {
                    final JToggleButton button = (JToggleButton) component;
                    CToggleButton dbutton = new CToggleButton(button.getText(), button.getIcon());
                    dbutton.setTooltip(button.getToolTipText());
                    ItemListener[] listeners = button.getItemListeners();
                    for(ItemListener listener : listeners) {
                        dbutton.getStateChanged().addListener(listener, EventHandler.create(Listener.class, listener, "itemStateChanged",""));
                    }
                    //Retrieve and apply button group
                    if(button.getModel() instanceof DefaultButtonModel) {
                        ButtonGroup bgroup = ((DefaultButtonModel)button.getModel()).getGroup();
                        //Find if there is an existing Docking Frames button group
                        CRadioGroup radio = radioGroups.get(bgroup.hashCode());
                        if(radio==null) {
                            radio = new CRadioGroup();
                            radioGroups.put(bgroup.hashCode(), radio);
                        }
                        radio.add(dbutton);
                    }
                    action = dbutton;
                } else if(component instanceof AbstractButton) {
                    AbstractButton button = (AbstractButton) component;
                    CButton dockingFramesButton = new CButton(button.getText(), button.getIcon());
                    dockingFramesButton.setTooltip(button.getToolTipText());
                    transferActionsListeners(button,dockingFramesButton);
                    action = dockingFramesButton;
                } else if (component instanceof JSeparator) {
                    action = CSeparator.SEPARATOR;
                }
                //Add custom actions into internal list and in the Docking Frames actions list
                if(action!=null) {
                    customActions.add(action);
                }
            }
        }
    }
}
