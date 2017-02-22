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

import bibliothek.gui.dock.common.intern.action.CDecorateableAction;
import bibliothek.gui.dock.common.intern.action.CSelectableAction;

import javax.swing.*;
import java.beans.PropertyChangeEvent;

/**
 * Common methods that read Action property change and update DockingFrame CAction.
 * @author Nicolas Fortin
 */
public class CommonFunctions {
    private CommonFunctions() {
    }

    /**
     * Propagate property change from Action to CAction.
     * @param cAction Docking Frames Common Action
     * @param action Swing Action
     * @param propertyChangeEvent Property change information
     */
    public static void onActionPropertyChangeSelectable(CSelectableAction cAction,Action action,PropertyChangeEvent propertyChangeEvent) {
        String prop = propertyChangeEvent.getPropertyName();
        if(prop==null || prop.equals(Action.SELECTED_KEY)) {
            Object selected = action.getValue(Action.SELECTED_KEY);
            if(selected!=null) {
                cAction.setSelected((Boolean)selected);
            }
        }
        onActionPropertyChangeDecorateable(cAction,action,propertyChangeEvent);
    }
    /**
     * Propagate property change from Action to CAction.
     * @param cAction Docking Frames Common Action
     * @param action Swing Action
     * @param propertyChangeEvent Property change information
     */
    public static void onActionPropertyChangeDecorateable(CDecorateableAction cAction,Action action,PropertyChangeEvent propertyChangeEvent) {
        String prop = propertyChangeEvent.getPropertyName();
        if(prop==null || prop.equals(Action.NAME)) {
            Object text = action.getValue(Action.NAME);
            if(text!=null) {
                cAction.setText((String)text);
            }
        }
        if(prop==null || prop.equals(Action.SMALL_ICON)) {
            Object icon = action.getValue(Action.SMALL_ICON);
            if(icon!=null) {
                cAction.setIcon((Icon)icon);
            }
        }
        if(prop==null || prop.equals(Action.SHORT_DESCRIPTION)) {
            Object description = action.getValue(Action.SHORT_DESCRIPTION);
            if(description!=null) {
                cAction.setTooltip((String)description);
            }
        }
        if(prop==null || prop.equals("enabled")) {
            cAction.setEnabled(action.isEnabled());
        }

    }
}
