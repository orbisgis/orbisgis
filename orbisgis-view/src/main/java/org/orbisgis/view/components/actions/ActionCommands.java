/*
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
package org.orbisgis.view.components.actions;

import org.apache.log4j.Logger;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.view.components.actions.intern.RemoveActionControls;
import org.orbisgis.view.components.button.DropDownButton;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provide a way to expose actions through multiple controls.
 * - Add/Remove actions at any time, but insert action group before.
 * - Register/UnRegister controls at any time
 * @author Nicolas Fortin
 */
public class ActionCommands {
        private static final Logger LOGGER = Logger.getLogger(ActionCommands.class);
        // Actions
        private List<Action> actions = new ArrayList<Action>();
        // Menu containers
        private List<JComponent> containers = new ArrayList<JComponent>();

        /**
         * Actions will be inserted in the registered tool bar.
         * @param toolBar JToolBar instance
         */
        public void registerContainer(JToolBar toolBar) {
                containers.add(toolBar);
                applyActionsOnMenuContainer(toolBar,
                        actions.toArray(new Action[actions.size()]),true);
        }

        public void registerContainer(JPopupMenu menu) {
                containers.add(menu);
                applyActionsOnMenuContainer(menu,
                        actions.toArray(new Action[actions.size()]),true);
        }

        /**
         * Copy only enabled actions to the provided popup menu.
         * Removed actions are not removed from this menu.
         * This function is useful for temporary Popup menu.
         * @param menu
         */
        public void copyEnabledActions(JPopupMenu menu) {
                applyActionsOnMenuContainer(menu,
                        getEnabledActions(),false);
        }
        private Action[] getEnabledActions() {
                List<Action> enabledActions = new ArrayList<Action>(actions.size());
                for(Action action : actions) {
                        if(action.isEnabled()) {
                                enabledActions.add(action);
                        }
                }
                return enabledActions.toArray(new Action[enabledActions.size()]);
        }
        /**
         *
         * @param menuBar JMenuBar instance
         */
        public void registerContainer(JMenuBar menuBar) {
                containers.add(menuBar);
                applyActionsOnMenuContainer(menuBar,
                        actions.toArray(new Action[actions.size()]),true);
        }
        /**
         * Remove a linked container.
         * @param component JMenuBar,JPopupMenu or JToolBar instance.
         * @return true is found and removed
         */
        public boolean unregisterContainer(JComponent component) {
                //Remove property change
                removePropertyChangeListeners(component);
                return containers.remove(component);
        }

        /**
         * Remove the reference of Action to the container. Removed action  will not remove components in container.
         * @param container JMenuBar,JPopupMenu or JToolBar instance.
         */
        private void removePropertyChangeListeners(Container container) {
                Component[] components = getSubElements(container);
                for(Component component : components) {
                        Action action = getAction(component);
                        if(action instanceof AbstractAction) {
                                AbstractAction act = (AbstractAction)action;
                                List<PropertyChangeListener> copyOfListenerList = Arrays.asList(act.getPropertyChangeListeners());
                                for(PropertyChangeListener listener : copyOfListenerList) {
                                        if(listener instanceof RemoveActionControls) {
                                                RemoveActionControls removeListener = (RemoveActionControls)listener;
                                                if(removeListener.getContainer().equals(container)) {
                                                        action.removePropertyChangeListener(listener);
                                                }
                                        }
                                }
                        }
                        if(component instanceof Container) {
                                removePropertyChangeListeners((Container)component);
                        }
                }
        }
        private Action getAction(Component component) {
                if(component instanceof AbstractButton) {
                        return ((AbstractButton) component).getAction();
                }
                // Action cannot be retrieved from this container
                return null;
        }
        /**
         * Add an action and show it in all registered controls.
         * @param action
         */
        public void addAction(Action action) {
                if (!actions.contains(action)) {
                        actions.add(action);
                        applyActionsOnAllControls(new Action[]{action});
                }
        }

        /**
         * Remove this action of all registered controls.
         * PropertyChange listeners of action will remove all related menu items.
         * @param action
         */
        public void removeAction(Action action) {
                action.putValue(RemoveActionControls.DELETED_PROPERTY, true);
                actions.remove(action);
        }

        private void applyActionsOnAllControls(Action[] actionsAr) {
                for(JComponent component : containers) {
                        applyActionsOnMenuContainer(component, actionsAr,true);
                }
        }

        /**
         * Extract sub elements that should contains actions.
         * @param container
         * @return
         */
        private Component[] getSubElements(Container container) {
                if(container instanceof JMenu) {
                        return ((JMenu)container).getMenuComponents();
                } else {
                        return container.getComponents();
                }
        }
        private void feedMap(Container container, Map<String,Container> subContainers,Map<String,ButtonGroup> buttonGroups) {
                Component[] subElements = getSubElements(container);
                for(Component menuEl : subElements) {
                        if(menuEl instanceof AbstractButton) {
                                AbstractButton menu = (AbstractButton)menuEl;
                                Action menuAction = menu.getAction();
                                if(menuAction!=null) {
                                        String menuId = ActionTools.getMenuId(menuAction);
                                        if(!menuId.isEmpty()) {
                                                subContainers.put(menuId, menu);
                                        }
                                        String buttonGroup = ActionTools.getToggleGroup(menuAction);
                                        if(!buttonGroup.isEmpty() && !buttonGroups.containsKey(buttonGroup)) {
                                                //New button group
                                                ButtonModel bm = menu.getModel();
                                                if(bm instanceof DefaultButtonModel) {
                                                        buttonGroups.put(buttonGroup,((DefaultButtonModel) bm).getGroup());
                                                }
                                        }
                                }
                        }
                        if(menuEl instanceof DropDownButton) {
                                DropDownButton button = (DropDownButton)menuEl;
                                if(button.getComponentPopupMenu()==null) {
                                        button.setComponentPopupMenu(new JPopupMenu());
                                }
                                feedMap(button.getComponentPopupMenu(),subContainers,buttonGroups);
                        } else if(menuEl instanceof Container) {
                                feedMap((Container)menuEl,subContainers,buttonGroups);
                        }
                }
        }

        /**
         * Add provided actions to rootMenu.
         * Convert Actions in swing controls.
         * @param rootMenu JPopupMenu, JToolBar or JMenuBar
         * @param actionsAr Action items to convert.
         * @param addRemoveListener If true, a listener is inserted in action that
         *                          contain a reference to container is inserted.
         *                          This listener remove the swing component if the Action is set as removed.
         */
        private void applyActionsOnMenuContainer(JComponent rootMenu, Action[] actionsAr,boolean addRemoveListener) {
                // Map of Parent->Menu
                Map<String,Container> subContainers = new HashMap<String,Container>();
                // Map of TOGGLE_GROUP -> ButtonGroup instance
                Map<String,ButtonGroup> buttonGroups = new HashMap<String, ButtonGroup>();
                subContainers.put("",rootMenu);
                // Add existing menu in map
                feedMap(rootMenu, subContainers,buttonGroups);
                // Insert new menu groups in map
                for(Action action : actionsAr) {
                        if(ActionTools.isMenu(action)) {
                                subContainers.put(ActionTools.getMenuId(action),
                                        new TemporaryContainer(action));
                         }
                }
                // Insert
                for(Action action : actionsAr) {
                        // Fetch parent menu of this action
                        String parentId = ActionTools.getParentMenuId(action);
                        Container parent;
                        if(parentId.isEmpty()) {
                                parent = rootMenu;
                        } else {
                                parent = subContainers.get(parentId);
                                if(parent==null) { //Orphan action
                                        LOGGER.warn("Menu action ("+action+") parent '"+parentId+"' does not exists.");
                                } else {
                                        if(parent instanceof TemporaryContainer) {
                                                parent = convertContainer((TemporaryContainer)parent,subContainers);
                                        }
                                }
                        }
                        if(parent!=null) {
                                Component child;
                                // Creation of Child items
                                // Child item class depends on action properties and parent class.
                                if(ActionTools.isMenu(action)) {
                                        child = subContainers.get(ActionTools.getMenuId(action));
                                        if(child instanceof TemporaryContainer) {
                                                child = convertContainer((TemporaryContainer)child,subContainers);
                                        }
                                } else {
                                        String buttonGroup = ActionTools.getToggleGroup(action);
                                        if(!(parent instanceof JToolBar)) {
                                                if(buttonGroup.isEmpty()) {
                                                        child = new JMenuItem(action);
                                                } else {
                                                        JRadioButtonMenuItem radioMenu = new JRadioButtonMenuItem(action);
                                                        ButtonGroup bGroup = getOrPutButtonGroup(buttonGroups,action);
                                                        bGroup.add(radioMenu);
                                                        child = radioMenu;
                                                }
                                        } else {
                                                if(buttonGroup.isEmpty()) {
                                                        child = new CustomButton(action);
                                                } else {
                                                        JToggleButton button = new JToggleButton(action);
                                                        ButtonGroup bGroup = getOrPutButtonGroup(buttonGroups,action);
                                                        bGroup.add(button);
                                                        child = button;
                                                }
                                        }
                                }
                                insertMenu(parent, child, action,addRemoveListener);
                        }
                }
        }
        private ButtonGroup getOrPutButtonGroup(Map<String,ButtonGroup> existingGroups,Action action) {
                String actionGroup = ActionTools.getToggleGroup(action);
                ButtonGroup actionBGroup = existingGroups.get(actionGroup);
                if(actionBGroup==null) {
                        actionBGroup = new ButtonGroup();
                        existingGroups.put(actionGroup,actionBGroup);
                }
                return actionBGroup;
        }
        /***
         * TemporaryContainer is temporary because the parent container
         * need to be known before creating the child container.
         * @param current Current container
         * @param subContainers Action, Container map
         * @return Final instance of the container
         */
        private Container convertContainer(TemporaryContainer current,Map<String,Container> subContainers) {
                //Get parent container
                Action action = current.getAction();
                Container parent = subContainers.get(ActionTools.getParentMenuId(action));
                if(parent instanceof TemporaryContainer) {
                        parent = convertContainer((TemporaryContainer)parent,subContainers);
                }
                Container child;
                if(parent instanceof JToolBar) {
                        DropDownButton button = new DropDownButton(action);
                        if(ActionTools.getIcon(action)==null) { //Get icon from selected menu
                            button.setButtonAsMenuItem(true);
                        }
                        button.setComponentPopupMenu(new JPopupMenu());
                        child = button;
                } else {
                        child = new JMenu(action);
                }
                // Update link between action and control
                subContainers.put(ActionTools.getMenuId(action),child);
                return child;
        }
        /**
         * Find the most appropriate action insertion index.
         * This is sorting by insertion. But it doesn't guaranty to solve complex order issues.
         * @param parent MenuItem container
         * @param action Action to insert
         * @return Advised insertion id [0-parent.getComponentCount()]
         */
        private int getInsertPosition(Container parent, Action action) {
                if(ActionTools.isFirstInsertion(action)) {
                    return 0;
                }
                final String newElMenuId = ActionTools.getMenuId(action);
                Component[] components;
                if(parent instanceof JMenu) {
                        // Special case, JMenu use an internal JPopupMenu
                        components = ((JMenu)parent).getMenuComponents();
                } else if(parent instanceof DropDownButton) {
                        components = ((DropDownButton) parent).getComponentPopupMenu().getComponents();
                } else {
                        components = parent.getComponents();
                }
                if(newElMenuId.isEmpty()) {
                        return components.length;
                }
                final String insertAfter = ActionTools.getInsertAfterMenuId(action);
                final String insertBefore= ActionTools.getInsertBeforeMenuId(action);
                for(int i=0;i<parent.getComponentCount();i++) {
                        Component comp = parent.getComponent(i);
                        if(comp instanceof AbstractButton) {
                                Action compAction = ((AbstractButton)comp).getAction();
                                if(compAction!=null) {
                                        String curMenuId = ActionTools.getMenuId(compAction);
                                        if(!curMenuId.isEmpty()) {
                                                // Read new Action and existing Action parameters to set order
                                                if(insertAfter.equals(curMenuId) ||
                                                        ActionTools.getInsertBeforeMenuId(compAction)
                                                                .equals(newElMenuId)) {
                                                        return i+1;
                                                }
                                                if(insertBefore.equals(curMenuId) ||
                                                        ActionTools.getInsertBeforeMenuId(compAction)
                                                                .equals(newElMenuId)) {
                                                        return i;
                                                }
                                        }
                                }
                        }
                }
                return components.length;
        }

    /**
     * Insert a separator at insertPosition if action and otherComp are not in the same logical group.
     * @param parent
     * @param otherComp
     * @param action
     * @param insertPosition
     */
        private void insertSeparator(Container parent,Component otherComp,Action action,int insertPosition) {
            String logicalGroup = ActionTools.getLogicalGroup(action);
            Action bAction = getAction(otherComp);
            if(bAction!=null) {
                // 2 consecutive actions with != logical action group
                if(!logicalGroup.equals(ActionTools.getLogicalGroup(bAction))) {
                    parent.add(new JSeparator(),insertPosition);
                }
            }
        }
        private void insertMenu(Container parent, Component child, Action action,boolean addRemoveListener) {
                // Get insertion index
                int insertPosition = getInsertPosition(parent, action);
                // Insert the action at is right place
                parent.add(child, insertPosition);
                // Insert Separator
                if(insertPosition>0) {
                    insertSeparator(parent,getSubElements(parent)[insertPosition-1],action,insertPosition);
                }
                if(insertPosition<getSubElements(parent).length) {
                    insertSeparator(parent,getSubElements(parent)[insertPosition],action,insertPosition+1);
                }
                if(addRemoveListener) {
                        // Remove child from parentComponent when action is removed
                        action.addPropertyChangeListener(new RemoveActionControls(parent,child));
                }
        }

        private JMenu createMenu(Action action) {
                return new JMenu(action);
        }
        /**
         * @deprecated Use register instead
         */
        public void feedPopupMenu(JPopupMenu areaMenu) {
                boolean addSeparator = areaMenu.getComponentCount() != 0;
                int customMenuCounter=0; // default position of components
                for(Action action : actions) {
                        JMenuItem actionItem = new JMenuItem(action);
                        areaMenu.insert(actionItem, customMenuCounter++);
                }
                if(addSeparator) {
                        //Separator at the end
                        areaMenu.insert(new JSeparator(),customMenuCounter++);
                }
        }

        /**
         * Apply to the component the actions
         * Text key shortcuts, Accelerators
         */
        public void setAccelerators(JComponent component) {
                InputMap im = component.getInputMap(JComponent.WHEN_FOCUSED);
                ActionMap actionMap = component.getActionMap();
                for(Action action : actions) {
                        KeyStroke actionStroke = ActionTools.getKeyStroke(action);
                        if(actionStroke!=null) {
                                im.put(actionStroke, action);
                                actionMap.put(action, action);
                                //Additionnal strokes
                                List<KeyStroke> strokes = ActionTools.getAdditionalKeyStroke(action);
                                if(strokes!=null) {
                                        for(KeyStroke stroke : strokes) {
                                                im.put(stroke, action);
                                        }
                                }
                        }
                }
        }
        /**
         * Return a set of button to control actions
         *
         * @param setButtonText If true, a text is set on the buttons
         * @return Instance of JToolBar
         * @deprecated Use register instead
         */
        public JToolBar getEditorToolBar(boolean setButtonText) {
                JToolBar commandToolBar = new JToolBar();
                //Add all registered actions
                for(Action action : actions) {
                        if(ActionTools.getIcon(action)!=null) {
                                JButton newButton = new JButton(action);
                                // Remove this button when action is removed.
                                action.addPropertyChangeListener(new RemoveActionControls(commandToolBar, newButton));
                                commandToolBar.add(newButton);
                        }
                }

                //Final separator
                commandToolBar.add(new JSeparator());
                return commandToolBar;
        }

        /**
         * Postpone the creation of the action control
         */
        private class TemporaryContainer extends Container {
                private Action action;

                private TemporaryContainer(Action action) {
                        this.action = action;
                }

                public Action getAction() {
                        return action;
                }
        }


}
