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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import org.orbisgis.view.components.actions.intern.RemoveActionControls;

/**
 * Provide a way to expose actions through multiple controls.
 * - Add actions
 * - Then create toolbars and popup menu through this instance
 * @author Nicolas Fortin
 */
public class ActionCommands {
        //Actions
        private List<Action> actions = new ArrayList<Action>();
        
        /**
         * @param action 
         */
        public void addAction(DefaultAction action) {
                actions.add(action);
        }
        
        public void removeAction(DefaultAction action) {
                action.putValue(RemoveActionControls.DELETED_PROPERTY, true);
                actions.remove(action);
        }
        
        /**
         * Add to the provided menu the intern actions
         * @param areaMenu 
         */
        public void feedPopupMenu(JPopupMenu areaMenu) {
                int customMenuCounter=0; // default position of components
                for(Action action : actions) {
                        JMenuItem actionItem = new JMenuItem(action);
                        areaMenu.insert(actionItem, customMenuCounter++);
                }
                
                //Separator at the end
                areaMenu.insert(new JSeparator(),customMenuCounter++);                
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
                                List<KeyStroke> strokes = ActionTools.getAdditionnalKeyStroke(action);
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
}
