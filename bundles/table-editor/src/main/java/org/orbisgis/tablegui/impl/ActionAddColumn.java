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

package org.orbisgis.tablegui.impl;

import java.awt.Component;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;

/**
 * Add a column in the DataSource.
 * @author Nicolas Fortin
 */
public class ActionAddColumn extends AbstractAction implements ActionDispose {
    private final TableEditableElement editable;
    private static final I18n I18N = I18nFactory.getI18n(ActionAddColumn.class);
    private final Logger logger = LoggerFactory.getLogger(ActionAddColumn.class);
    private final PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, this, "updateEnabledState");
    private Component parentComponent;
    
    /**
     * Constructor
     * @param editable Table editable instance
     */
    public ActionAddColumn(TableEditableElement editable) {
        super(I18N.tr("Add a column"), TableEditorIcon.getIcon("add_field"));
        putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_MODIFICATION_GROUP);
        putValue(ActionTools.MENU_ID,TableEditorActions.A_ADD_FIELD);
        this.editable = editable;
        this.parentComponent = parentComponent;
        updateEnabledState();
        editable.addPropertyChangeListener(TableEditableElement.PROP_EDITING,
                listener);
    }

    /**
     * Enable this action only if edition is enabled
     */
    public void updateEnabledState() {
        setEnabled(editable.isEditing());
    }

    @Override
    public void dispose() {
        editable.removePropertyChangeListener(listener);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(editable.isEditing()) {
            //Create panel to add a column
            
            
        }
    }
}
