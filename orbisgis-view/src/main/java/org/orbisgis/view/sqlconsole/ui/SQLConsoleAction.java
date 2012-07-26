/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.sqlconsole.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Action implementation for SQL Editor.
 * @author Nicolas Fortin
 */
public class SQLConsoleAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private ActionListener actionListener;
        private KeyStroke keyStroke;
        private List<KeyStroke> additionnalKeyStrokes = new ArrayList<KeyStroke>();
        /**
         * 
         * @param actionLabel I18N label short label
         * @param actionToolTip I18N tool tip text
         * @param icon Icon
         * @param actionListener Fire the event to this listener
         * @param keyStroke ShortCut for this action
         */
        public SQLConsoleAction(String actionLabel,String actionToolTip, Icon icon,ActionListener actionListener,KeyStroke keyStroke) {
                super(actionLabel, icon);
                this.actionListener = actionListener;
                this.keyStroke = keyStroke;
                putValue(SHORT_DESCRIPTION, actionToolTip);
                if(keyStroke!=null) {
                        putValue(ACCELERATOR_KEY, keyStroke);
                }
        }
        /**
         * 
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
                return keyStroke;
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
         * @see getAdditionnalKeyStrokes
         * @param keyStroke
         * @return this
         */
        public SQLConsoleAction addStroke(KeyStroke keyStroke) {
                additionnalKeyStrokes.add(keyStroke);
                return this;
        }
        /**
        * @see addStroke
        * @return Accelerator for this action (not used in menu and toolbars)
        */
        public List<KeyStroke> getAdditionnalKeyStrokes() {
                return additionnalKeyStrokes;
        }
        
        
}
