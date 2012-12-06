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

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import java.awt.*;
import java.util.ArrayList;
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
                        actions.toArray(new Action[actions.size()]));
        }

        public void registerContainer(JPopupMenu menu) {
                containers.add(menu);
                applyActionsOnMenuContainer(menu,
                        actions.toArray(new Action[actions.size()]));
        }

        /**
         *
         * @param menuBar JMenuBar instance
         */
        public void registerContainer(JMenuBar menuBar) {
                containers.add(menuBar);
                applyActionsOnMenuContainer(menuBar,
                        actions.toArray(new Action[actions.size()]));
        }
        /**
         * Remove a linked container.
         * @param component
         * @return true is found and removed
         */
        public boolean unregisterContainer(JComponent component) {
                return containers.remove(component);
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
                        applyActionsOnMenuContainer(component, actionsAr);
                }
        }

        private void feedMap(Container container, Map<String,Container> subContainers) {
                Component[] subElements;
                if(container instanceof JMenu) {
                        subElements = ((JMenu)container).getMenuComponents();
                } else {
                        subElements = container.getComponents();
                }
                for(Component menuEl : subElements) {
                        if(menuEl instanceof AbstractButton) {
                                AbstractButton menu = (AbstractButton)menuEl;
                                Action menuAction = menu.getAction();
                                if(menuAction!=null) {
                                        String menuId = ActionTools.getMenuId(menuAction);
                                        if(!menuId.isEmpty()) {
                                                subContainers.put(menuId, menu);
                                        }
                                }
                        }
                        if(menuEl instanceof DropDownButton) {
                                DropDownButton button = (DropDownButton)menuEl;
                                if(button.getComponentPopupMenu()==null) {
                                        button.setComponentPopupMenu(new JPopupMenu());
                                }
                                feedMap(button.getComponentPopupMenu(),subContainers);
                        } else if(menuEl instanceof Container) {
                                feedMap((Container)menuEl,subContainers);
                        }
                }
        }

        private void applyActionsOnMenuContainer(JComponent rootMenu, Action[] actionsAr) {
                // Map of Parent->Menu
                Map<String,Container> subContainers = new HashMap<String,Container>();
                subContainers.put("",rootMenu);
                // Add existing menu in map
                feedMap(rootMenu, subContainers);
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
                                if(ActionTools.isMenu(action)) {
                                        child = subContainers.get(ActionTools.getMenuId(action));
                                        if(child instanceof TemporaryContainer) {
                                                child = convertContainer((TemporaryContainer)child,subContainers);
                                        }
                                } else {
                                        if(!(parent instanceof JToolBar)) {
                                                child = new JMenuItem(action);
                                        } else {
                                                child = new CustomButton(action);
                                        }
                                }
                                insertMenu(parent, child, action);
                        }
                }
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
        private void insertMenu(Container parent, Component child, Action action) {
                // Insert the action at is right place
                parent.add(child, getInsertPosition(parent, action));
                // Remove child from parentComponent when action is removed
                action.addPropertyChangeListener(new RemoveActionControls(parent,child));
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
