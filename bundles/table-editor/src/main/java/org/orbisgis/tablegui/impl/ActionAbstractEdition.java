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

import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableEditListener;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import java.beans.EventHandler;

/**
 * All action where the enable state depends on the DataSource state extends this abstract class.
 * @author Nicolas Fortin
 */
public abstract class ActionAbstractEdition extends AbstractAction implements ActionDispose {
    protected TableEditableElement editable;
    protected static final I18n I18N = I18nFactory.getI18n(ActionAbstractEdition.class);
    protected static final Logger LOGGER = LoggerFactory.getLogger(ActionAbstractEdition.class);
    private TableEditListener listener = EventHandler.create(TableEditListener.class, this, "sourceEvent", "");

    protected ActionAbstractEdition(TableEditableElement editable) {
        setEditable(editable);
        putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_EDITION);
    }
    private void setEditable(TableEditableElement editable) {
        this.editable = editable;
        editable.getDataManager().addTableEditListener(editable.getTableReference(), listener);
    }
    protected ActionAbstractEdition(String s, Icon icon, TableEditableElement editable) {
        super(s, icon);
        setEditable(editable);
        putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_EDITION);
    }

    /**
     * Called when DataSource fire an event.
     */
    public abstract void onSourceUpdate();


    public void sourceEvent(TableEditEvent event) {
        if(SwingUtilities.isEventDispatchThread()) {
            onSourceUpdate();
        } else {
            SwingUtilities.invokeLater(new SourceEvent(this));
        }
    }
    @Override
    public void dispose() {
        editable.getDataManager().removeTableEditListener(editable.getTableReference(), listener);
    }

    private static class SourceEvent implements Runnable {
        ActionAbstractEdition abstractEdition;

        public SourceEvent(ActionAbstractEdition abstractEdition) {
            this.abstractEdition = abstractEdition;
        }

        @Override
        public void run() {
            abstractEdition.onSourceUpdate();
        }
    }
}
