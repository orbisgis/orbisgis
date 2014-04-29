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

import org.gdms.driver.DriverException;
import org.orbisgis.view.components.actions.ActionTools;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.table.ext.TableEditorActions;

import java.awt.event.ActionEvent;

/**
 * Lock/Unlock table edition action.
 * @author Nicolas Fortin
 */
public class ActionRedo extends ActionAbstractEdition {

    public ActionRedo(TableEditableElement editable) {
        super(I18N.tr("Redo"),OrbisGISIcon.getIcon("edit-redo"),editable);
        putValue(SHORT_DESCRIPTION,I18N.tr("Redo the last modification"));
        putValue(ActionTools.MENU_ID, TableEditorActions.A_REDO);
        onSourceUpdate();
    }

    @Override
    public void onSourceUpdate() {
        setEnabled(editable.getDataSource().canRedo());
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            editable.getDataSource().redo();
        } catch (DriverException ex) {
            LOGGER.error(ex.getLocalizedMessage(),ex);
        }
    }
}
