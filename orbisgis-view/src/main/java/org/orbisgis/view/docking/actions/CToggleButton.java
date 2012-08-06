/**
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
package org.orbisgis.view.docking.actions;

import bibliothek.gui.dock.common.action.CRadioButton;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Icon;
import org.apache.log4j.Logger;
import org.orbisgis.core.events.EventException;
import org.orbisgis.core.events.Listener;
import org.orbisgis.core.events.ListenerContainer;

/**
 * Implementation of listener on CRadioButton.
 */
public class CToggleButton extends CRadioButton implements ItemSelectable {
    public interface StateListener extends Listener<ItemEvent> {
            
    }
    private Logger LOGGER = Logger.getLogger(CToggleButton.class);
    private ListenerContainer<ItemEvent> stateChanged = new ListenerContainer<ItemEvent>();

    public CToggleButton(String text, Icon icon) {
        super(text, icon);
    }

    public CToggleButton() {
    }

   
    @Override
    protected void changed() {
        try {
            int newState;
            if(this.isSelected()) {
                newState = ItemEvent.SELECTED;
            } else {
                newState = ItemEvent.DESELECTED;
            }
            stateChanged.callListeners(new ItemEvent(this,0,null,newState));
        } catch (EventException ex) {
            LOGGER.error("Exception on Changing state of toggle button", ex);
        }
    }
    public ListenerContainer<ItemEvent> getStateChanged() {
        return stateChanged;
    }

    public Object[] getSelectedObjects() {
        return new Object[] {};
    }

    public void addItemListener(ItemListener il) {
        throw new UnsupportedOperationException("Use getStateChanged().addListener()");
    }

    public void removeItemListener(ItemListener il) {
        throw new UnsupportedOperationException("Use getStateChanged().removeListener()");
    }
}
