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
package org.orbisgis.geocatalog.impl.actions;


import org.orbisgis.sif.components.actions.DefaultAction;

import javax.swing.Icon;
import javax.swing.ListSelectionModel;
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
