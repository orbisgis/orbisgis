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
package org.orbisgis.view.docking.internals;

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.CButton;
import bibliothek.gui.dock.common.action.CDropDownButton;
import bibliothek.gui.dock.common.action.CMenu;
import bibliothek.gui.dock.common.action.CRadioButton;
import bibliothek.gui.dock.common.action.CRadioGroup;
import bibliothek.gui.dock.common.action.CSeparator;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ItemListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.MenuElement;
import org.apache.log4j.Logger;
import org.orbisgis.view.components.button.DropDownButton;
import org.orbisgis.view.docking.actions.CToggleButton;

/**
 * This class is responsible of converting JToolBar into custom DockingFrames CActions.
 */
public class ToolBarActions {
    private static final Logger GUILOGGER = Logger.getLogger("gui."+ToolBarActions.class);
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
     * Docking Frames does't provide the "Container" interface,
     * this method help to add item into multiple CAction container
     * @param citem 
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
    /**
     * Copy Swing Menu Item into Docking Frames menu item
     * and select the selected item
     * @param jitem Java menu
     * @param citem Dockings Frames Menu
     */
    
    /**
     * Copy Swing Menu Item into Docking Frames menu item
     * and select the selected item
     * @param me The menu to copy
     * @param citem Destination of copy
     * @param dbutton The docking frames dropdownbutton
     * @param selectedItem The item to select in destination
     * @param radio The button group to disable when an action is done in a menu, can be null
     */
    private void CopyJMenuIntoCMenu(MenuElement me,CAction citem,CDropDownButton dbutton,JMenuItem selectedItem,CRadioGroup buttonGroup) {
        if(me instanceof JMenuItem) {
            JMenuItem jMenuItem = (JMenuItem)me;
            CButton cButton = new CButton(jMenuItem.getText(),jMenuItem.getIcon());
            cButton.setTooltip(jMenuItem.getToolTipText());
            addSubItem(citem,cButton);
            //If this is the selected item
            if(jMenuItem.equals(selectedItem)) {
                dbutton.setSelection(citem);
                dbutton.setIcon(jMenuItem.getIcon());
            }
            //Deselect actions first
            if(buttonGroup!=null) {
                cButton.addActionListener(new ButtonGroupActionListener(buttonGroup));
            }
            transferActionsListeners(jMenuItem,cButton);
        } else if(me instanceof JMenu || me instanceof JPopupMenu){
            CAction cmenu = citem;
            if(me instanceof JMenu) {
                JMenu jMenu = (JMenu)me;
                cmenu = new CMenu(jMenu.getText(), jMenu.getIcon());
                addSubItem(citem,cmenu);
            }
            for(MenuElement sme : me.getSubElements()) {
                CopyJMenuIntoCMenu(sme,cmenu,dbutton,selectedItem,buttonGroup);
            }
        }
    }
    
    /**
     * Retrieve the swing button group
     * @param dropButton Can be null
     */
    private CRadioGroup getButtonGroup(DropDownButton dropButton) {
        if(dropButton.getModel() instanceof DefaultButtonModel) {
            ButtonGroup bgroup = ((DefaultButtonModel)dropButton.getModel()).getGroup();
            //Find if there is an existing Docking Frames button group
            CRadioGroup radio = radioGroups.get(bgroup.hashCode());
            if(radio==null) {
                radio = new CRadioGroup();
                radioGroups.put(bgroup.hashCode(), radio);
            }
            return radio;
        }
        return null;
    }
    /**
     * Retrieve swing button group and apply to DockingFrames button group
     * @param button
     * @param dbutton 
     */
    private void applyButtonGroup(JToggleButton button,CRadioButton dbutton) {
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
    }
    /**
     * Convert the swing toolbar into docking frames CAction
     * @param viewToolBar 
     */
    public void convertToolBarToActions(JToolBar viewToolBar) {
        if(viewToolBar!=null) {
            customActions.clear();
            Component[] components = viewToolBar.getComponents();
            //For each toolbar components
            for(Component component : components) {
                CAction action=null;
                if(component instanceof DropDownButton) {
                    final DropDownButton button = (DropDownButton) component;
                    CDropDownButton dbutton = new CDropDownButton();
                    dbutton.setText(button.getName());
                    CopyJMenuIntoCMenu(button.getComponentPopupMenu(),dbutton,dbutton,button.getSelectedItem(),getButtonGroup(button));
                    action = dbutton;
                } else if(component instanceof JToggleButton) {
                    final JToggleButton button = (JToggleButton) component;
                    CToggleButton dbutton = new CToggleButton(button.getText(), button.getIcon());
                    dbutton.setSelected(button.isSelected());
                    dbutton.setTooltip(button.getToolTipText());
                    ItemListener[] listeners = button.getItemListeners();
                    for(ItemListener listener : listeners) {
                        dbutton.getStateChanged().addListener(listener, EventHandler.create(CToggleButton.StateListener.class, listener, "itemStateChanged",""));
                    }
                    //Retrieve and apply button group
                    applyButtonGroup(button,dbutton);
                    action = dbutton;
                } else if(component instanceof AbstractButton) {
                    AbstractButton button = (AbstractButton) component;
                    CButton dockingFramesButton = new CButton(button.getText(), button.getIcon());
                    dockingFramesButton.setTooltip(button.getToolTipText());
                    transferActionsListeners(button,dockingFramesButton);
                    //button.addPropertyChangeListener(AbstractButton., null);
                    //EventHandler.create(PropertyChangeListener.class,dockingFramesButton,"setEnabled","newValue")
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
