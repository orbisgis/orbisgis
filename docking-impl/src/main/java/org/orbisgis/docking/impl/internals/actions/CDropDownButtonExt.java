/**
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
package org.orbisgis.docking.impl.internals.actions;

import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.CDropDownButton;
import javax.swing.Action;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Nicolas Fortin
 */
public class CDropDownButtonExt extends CDropDownButton implements CActionHolder {
    private Action action;
    public CDropDownButtonExt(Action action) {
        this.action = action;
        // Read properties from the action
        onActionPropertyChange(new PropertyChangeEvent(action,null,null,null));
        // Listen to action property changes
        action.addPropertyChangeListener(
                EventHandler.create(PropertyChangeListener.class, this, "onActionPropertyChange", ""));

    }

    @Override
    public void add(CAction action) {
        super.add(action);
        //If this is the first action, set it as selected
        if(intern().size()==1) {
            intern().setSelection(action.intern());
        }
    }

    /**
     * Used by PropertyChangeListener, update CRadioButton properties
     * @param propertyChangeEvent Event property
     */
    public void onActionPropertyChange(PropertyChangeEvent propertyChangeEvent) {
        CommonFunctions.onActionPropertyChangeDecorateable(this, action, propertyChangeEvent);
    }

    @Override
    public Action getAction() {
        return action;
    }
}
