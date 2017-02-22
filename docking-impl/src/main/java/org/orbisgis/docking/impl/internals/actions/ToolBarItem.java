/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.docking.impl.internals.actions;

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import org.orbisgis.sif.components.actions.ActionTools;

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
