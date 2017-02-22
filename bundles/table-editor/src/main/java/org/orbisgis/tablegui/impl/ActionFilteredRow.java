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

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;

import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.tableeditorapi.TableEditableElementImpl;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Enable and disable filtered row action
 * @author Erwan Bocher
 */
public class ActionFilteredRow extends AbstractAction{

    private final TableEditableElement tableEditableElement;
    private final I18n I18N = I18nFactory.getI18n(ActionFilteredRow.class);
    
    /**
     * Constructor
     * @param tableEditableElement Editable instance
     */
    public ActionFilteredRow(TableEditableElement tableEditableElement) {
        this.tableEditableElement = tableEditableElement;
        changeLabelAndIcon();
        tableEditableElement.addPropertyChangeListener(TableEditableElementImpl.PROP_FILTERED,
                EventHandler.create(PropertyChangeListener.class,this,"changeLabelAndIcon"));
        tableEditableElement.addPropertyChangeListener(TableEditableElementImpl.PROP_SELECTION,
                EventHandler.create(PropertyChangeListener.class,this,"changeSelection"));
    }

    public void changeSelection() {
       setEnabled(tableEditableElement.isFiltered() | !tableEditableElement.getSelection().isEmpty());
       changeLabelAndIcon();
    }

    /**
     * Update the label and the icon when a table is filtered or not
     */
    public void changeLabelAndIcon() {
        if(tableEditableElement.isFiltered()){
            putValue(NAME, I18N.tr("Clear row filter"));
            putValue(SMALL_ICON, TableEditorIcon.getIcon("row_filter_remove"));
        }
        else {
            putValue(NAME, I18N.tr("Filter selected rows"));
            putValue(SMALL_ICON, TableEditorIcon.getIcon("row_filter"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        tableEditableElement.setFiltered(!tableEditableElement.isFiltered());
    }
    
}
