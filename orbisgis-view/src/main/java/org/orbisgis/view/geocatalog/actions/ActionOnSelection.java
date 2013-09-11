/**
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
package org.orbisgis.view.geocatalog.actions;

import org.orbisgis.view.components.actions.DefaultAction;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * An action that request at least one selected data source.
 * @author Nicolas Fortin
 */
public class ActionOnSelection extends DefaultAction {
    ListSelectionModel selModel;

    /**
     * Constructor for menu group
     * @param actionId Action identifier, should be unique for ActionCommands
     * @param actionLabel I18N label short label
     * @param isGroup if this action is an action group.
     */
    public ActionOnSelection(String actionId, String actionLabel,boolean isGroup,ListSelectionModel selModel) {
        super(actionId, actionLabel);
        setMenuGroup(isGroup);
        this.selModel=selModel;
    }
    /**
     * @param actionId Action identifier, should be unique for ActionCommands
     * @param actionLabel I18N label short label
     * @param actionToolTip I18N tool tip text
     * @param icon Icon
     * @param actionListener Fire the event to this listener
     */
    public ActionOnSelection(String actionId, String actionLabel,String actionToolTip, Icon icon,ActionListener actionListener,ListSelectionModel selModel) {
        super(actionId, actionLabel, actionToolTip, icon, actionListener, null);
        this.selModel=selModel;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && !selModel.isSelectionEmpty();
    }
}
