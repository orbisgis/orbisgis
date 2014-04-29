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

package org.orbisgis.view.table;


import org.orbisgis.viewapi.components.actions.ActionTools;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.table.ext.TableEditorActions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;

/**
 * Lock/Unlock table edition action.
 * @author Nicolas Fortin
 */
public class ActionEdition extends AbstractAction {
    private final TableEditableElementImpl editable;
    private final I18n i18N = I18nFactory.getI18n(ActionEdition.class);

    /**
     * Constructor
     * @param editable Editable instance
     */
    public ActionEdition(TableEditableElementImpl editable) {
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
            putValue(SMALL_ICON, OrbisGISIcon.getIcon("edition/unlock"));
        } else {
            putValue(NAME, i18N.tr("Start editing"));
            putValue(SMALL_ICON, OrbisGISIcon.getIcon("edition/lock"));
        }
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        editable.setEditing(!editable.isEditing());
    }
}
