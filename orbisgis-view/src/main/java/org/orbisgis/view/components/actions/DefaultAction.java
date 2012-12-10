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

import org.orbisgis.sif.common.MenuCommonFunctions;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Action implementation, linked with an action listener. Implement additional properties related to ActionCommands
 * @author Nicolas Fortin
 */
public class DefaultAction extends AbstractAction {        
        private static final long serialVersionUID = 1L;
        private ActionListener actionListener;

        /**
         * @param actionId Action identifier, should be unique for ActionCommands
         * @param actionLabel I18N label short label
         */
        public DefaultAction(String actionId, String actionLabel) {
            super(actionLabel);
            putValue(ActionTools.MENU_ID,actionId);
            MenuCommonFunctions.setMnemonic(this);
        }
        /**
         * @param actionId Action identifier, should be unique for ActionCommands
         * @param actionLabel I18N label short label
         * @param icon Icon
         */
        public DefaultAction(String actionId, String actionLabel, Icon icon) {
            super(actionLabel,icon);
            putValue(ActionTools.MENU_ID,actionId);
            MenuCommonFunctions.setMnemonic(this);
        }
        /**
         * @param actionId Action identifier, should be unique for ActionCommands
         * @param actionLabel I18N label short label
         * @param icon Icon
         * @param actionListener Fire the event to this listener
         */
        public DefaultAction(String actionId, String actionLabel, Icon icon,ActionListener actionListener) {
            this(actionId, actionLabel, icon);
            this.actionListener = actionListener;
        }
        /**
         * @param actionId Action identifier, should be unique for ActionCommands
         * @param actionLabel I18N label short label
         * @param actionToolTip I18N tool tip text
         * @param icon Icon
         * @param actionListener Fire the event to this listener
         * @param keyStroke ShortCut for this action
         */
        public DefaultAction(String actionId, String actionLabel,String actionToolTip, Icon icon,ActionListener actionListener,KeyStroke keyStroke) {
                this(actionId, actionLabel, icon);
                this.actionListener = actionListener;
                putValue(SHORT_DESCRIPTION, actionToolTip);
                if(keyStroke!=null) {
                        putValue(ACCELERATOR_KEY, keyStroke);
                }
        }

        /**
         * @param actionListener Replace current action listener
         * @return this
         */
        public DefaultAction setActionListener(ActionListener actionListener) {
            this.actionListener = actionListener;
            return this;
        }

    /**
         * @return The listener set
         */
        public ActionListener getActionListener() {
                return actionListener;
        }
        /**
         * 
         * @return The action shortcut
         */
        public KeyStroke getKeyStroke() {
                return (KeyStroke)getValue(ACCELERATOR_KEY);
        }
        
        /**
         * 
         * @return The icon or null
         */
        public Icon getIcon() {
                Object value = getValue(SMALL_ICON);
                if(value instanceof Icon) {
                        return (Icon) value;
                } else {
                        return null;
                }
        }
        @Override
        public void actionPerformed(ActionEvent ae) {
                actionListener.actionPerformed(ae);
        }
        /**
         * Add a new Accelerator for this action (not used in menu and toolbars)
         * @param keyStroke
         * @return this
         */
        public DefaultAction addStroke(KeyStroke keyStroke) {
                List<KeyStroke> additionalKeyStrokes = getAdditionalKeyStrokes();
                additionalKeyStrokes.add(keyStroke);
                firePropertyChange(ActionTools.ADDITIONAL_ACCELERATOR_KEY,null,keyStroke);
                return this;
        }
       /**
        * @return Accelerator for this action (not used in menu and toolbars)
        */
        public List<KeyStroke> getAdditionalKeyStrokes() {
                List<KeyStroke> additionalKeyStrokes = (List<KeyStroke>)getValue(ActionTools.ADDITIONAL_ACCELERATOR_KEY);
                if(additionalKeyStrokes==null) {
                    additionalKeyStrokes = new ArrayList<KeyStroke>();
                    putValue(ActionTools.ADDITIONAL_ACCELERATOR_KEY, additionalKeyStrokes);
                }
                return additionalKeyStrokes;
        }
        /**
         * @param isGroup If true, this action will create a JMenu instance instead of a JMenuItem.
         * @return this
         */
        public DefaultAction setMenuGroup(boolean isGroup) {
                putValue(ActionTools.MENU_GROUP, isGroup);
                return this;
        }

        public DefaultAction setAfter(String otherMenuID) {
            putValue(ActionTools.INSERT_AFTER_MENUID,otherMenuID);
            return this;
        }
        public DefaultAction setBefore(String otherMenuID) {
            putValue(ActionTools.INSERT_BEFORE_MENUID, otherMenuID);
            return this;
        }

        /**
         * If set, other actions with the same actionGroup will be unSet if this action is set active.
         * ButtonGroup will be created by ActionCommands.
         * Setting a value will create a JRadioButton or a JRadioButtonMenu instead of JButton and JMenuItem.
         * @param buttonGroup Actions with the same button group will share the same ButtonGroup
         * @return this
         */
        public DefaultAction setButtonGroup(String buttonGroup) {
            putValue(ActionTools.TOGGLE_GROUP,buttonGroup);
            if(getValue(Action.SELECTED_KEY)==null) {
                putValue(Action.SELECTED_KEY,Boolean.FALSE);
            }
            return this;
        }
        public DefaultAction setSelected(boolean newState) {
            putValue(Action.SELECTED_KEY,newState);
            return this;
        }

        /**
         * @return The state of Action.SELECTED_KEY
         */
        public boolean isSelected() {
                Object sel = getValue(Action.SELECTED_KEY);
                return sel!=null && sel.equals(Boolean.TRUE);
        }

        /**
         * @param parentMenuID Parent Action MENU_ID
         * @return this
         */
        public DefaultAction setParent(String parentMenuID) {
            putValue(ActionTools.PARENT_ID, parentMenuID);
            return this;
        }
        /**
         * @param toolTipText Short description of Action.
         * @return this
         */
        public DefaultAction setToolTipText(String toolTipText) {
            putValue(SHORT_DESCRIPTION, toolTipText);
            return this;
        }
        /**
         * @param logicalGroup Inserted action will insert a JSeparator if the next and
         *                     previous action is not in the same logical group
         * @return this
         */
        public DefaultAction setLogicalGroup(String logicalGroup) {
            putValue(ActionTools.LOGICAL_GROUP, logicalGroup);
            return this;
        }
        /**
         * @param insertFirst If true, this action will be inserted at index=0 instead of last insertion
         * @return this
         */
        public DefaultAction setInsertFirst(boolean insertFirst) {
            putValue(ActionTools.INSERT_FIRST, insertFirst);
            return this;
        }
}
