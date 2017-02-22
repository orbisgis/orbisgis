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


import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.orbisgis.sif.components.actions.ActionTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import org.orbisgis.tableeditorapi.TableEditableElementImpl;

/**
 * Lock/Unlock table edition action.
 * @author Nicolas Fortin
 */
public class ActionEdition extends AbstractAction {
    private final TableEditableElement editable;
    private final I18n i18N = I18nFactory.getI18n(ActionEdition.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdition.class);

    /**
     * Constructor
     * @param editable Editable instance
     */
    public ActionEdition(TableEditableElement editable) {
        putValue(ActionTools.MENU_ID, TableEditorActions.A_EDITION);
        putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_EDITION);
        this.editable = editable;
        updateLabelAndIcon();
        editable.addPropertyChangeListener(TableEditableElementImpl.PROP_EDITING,
                EventHandler.create(PropertyChangeListener.class,this,"updateLabelAndIcon"));
    }

    /**
     * Called when the edition state of TableEditableElement change.
     */
    public final void updateLabelAndIcon() {
        if(editable.isEditing()) {
            putValue(NAME, i18N.tr("Stop editing"));
            putValue(SMALL_ICON, TableEditorIcon.getIcon("unlock"));
        } else {
            putValue(NAME, i18N.tr("Start editing"));
            putValue(SMALL_ICON, TableEditorIcon.getIcon("lock"));
        }
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(editable.isEditable()) {
            editable.setEditing(!editable.isEditing());
        } else {
            LOGGER.warn(editable.getNotEditableReason());
        }
    }
}
