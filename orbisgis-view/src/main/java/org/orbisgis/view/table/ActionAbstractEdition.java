/*
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

package org.orbisgis.view.table;

import org.apache.log4j.Logger;
import org.gdms.data.DataSourceListener;
import org.gdms.data.edition.EditionListener;
import org.orbisgis.view.components.actions.ActionTools;
import org.orbisgis.view.table.ext.TableEditorActions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import java.beans.EventHandler;

/**
 * All action where the enable state depends on the DataSource state extends this abstract class.
 * @author Nicolas Fortin
 */
public abstract class ActionAbstractEdition extends AbstractAction implements ActionDispose {
    protected TableEditableElement editable;
    protected static final I18n I18N = I18nFactory.getI18n(ActionAbstractEdition.class);
    protected static final Logger LOGGER = Logger.getLogger(ActionAbstractEdition.class);
    private final EditionListener editionListener = EventHandler.create(EditionListener.class, this, "onSourceUpdate");
    private final DataSourceListener dataSourceListener = EventHandler.create(DataSourceListener.class, this, "onSourceUpdate");

    protected ActionAbstractEdition(TableEditableElement editable) {
        setEditable(editable);
        putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_EDITION);
    }
    private void setEditable(TableEditableElement editable) {
        this.editable = editable;
        editable.getDataSource().addEditionListener(editionListener);
        editable.getDataSource().addDataSourceListener(dataSourceListener);
    }
    protected ActionAbstractEdition(String s, Icon icon, TableEditableElement editable) {
        super(s, icon);
        setEditable(editable);
    }

    /**
     * Called when DataSource fire an event.
     */
    public abstract void onSourceUpdate();

    @Override
    public void dispose() {
        editable.getDataSource().removeEditionListener(editionListener);
        editable.getDataSource().removeDataSourceListener(dataSourceListener);
    }
}
