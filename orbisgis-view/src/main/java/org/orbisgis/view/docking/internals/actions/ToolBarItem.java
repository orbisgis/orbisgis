/*
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

package org.orbisgis.view.docking.internals.actions;

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import org.orbisgis.viewapi.components.actions.ActionTools;

import javax.swing.Action;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Dockable Tool Bar, related only with a swing Action instance.
 * @author Nicolas Fortin
 */
public class ToolBarItem extends CToolbarItem implements CActionHolder {
        private Action action;
        private PropertyChangeListener visibleListener = EventHandler.create(PropertyChangeListener.class,this,"onActionPropertyChange","");
        private boolean trackActionVisibleState = false;
        /**
         * Create a new toolbar item
         * @param id
         * @param cAction Null or CAction instance
         */
        public ToolBarItem(String id,CAction cAction) {
                super(id);
                setItem(cAction);
        }

        /**
         * If true the visible state of the ToolBarItem will be updated by the Visible property of the linked Action
         * @param trackActionVisibleState
         */
        public void setTrackActionVisibleState(boolean trackActionVisibleState) {
            this.trackActionVisibleState = trackActionVisibleState;
        }

        /**
         * The visible property of an action has been updated
         * @param evt
         */
        public void onActionPropertyChange(PropertyChangeEvent evt) {
            if(trackActionVisibleState && ActionTools.VISIBLE.equals(evt.getPropertyName())) {
                if(evt.getNewValue() instanceof Boolean) {
                    this.setVisible((Boolean)evt.getNewValue());
                }
            }
        }
        /**
         * In order to keep layout, the ToolBarItem can be kept and CAction and Action removed.
         */
        public void resetItem() {
            setVisible(false);
            setItem((CAction)null);
        }
        @Override
        public void setItem(CAction item) {
            if(item instanceof CActionHolder) {
                setAction(((CActionHolder) item).getAction());
            } else if(item==null) {
                setAction(null);
            }
            super.setItem(item);
        }
        private void setAction(Action newAction) {
            if(action!=null) {
                action.removePropertyChangeListener(visibleListener);
            }
            if(newAction!=null) {
                newAction.addPropertyChangeListener(visibleListener);
            }
            action = newAction;
        }
        @Override
        public Action getAction() {
                return action;
        }
}
