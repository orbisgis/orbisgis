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
package org.orbisgis.tableeditorapi;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.common.LongUnion;
import org.orbisgis.editorjdbc.EditableSourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


/**
 * Interface to be implemented by those EditableElements that need to be edited
 * by the table editor.
 * 
 */
public class TableEditableElementImpl extends EditableSourceImpl implements TableEditableElement {
        public static final String TYPE_ID = "TableEditableElement";
        private static final Logger LOGGER = LoggerFactory.getLogger(TableEditableElementImpl.class);
        // Properties
        protected LongUnion selectedGeometries;
        private final I18n i18n = I18nFactory.getI18n(TableEditableElementImpl.class);
        private boolean isFiltered;

        /**
         * Constructor
         * @param selection
         * @param sourceName
         * @param dataManager
         */
        public TableEditableElementImpl(Set<Long> selection, String sourceName, DataManager dataManager) {
                super(sourceName, dataManager);
                this.selectedGeometries = new LongUnion(selection);
        }

        /**
         * Constructor
         * @param sourceName
         * @param dataManager
         */
        public TableEditableElementImpl(String sourceName, DataManager dataManager) {
                super(sourceName, dataManager);
                this.selectedGeometries = new LongUnion();
        }

        @Override
        public String toString() {
            return i18n.tr("Table \"{0}\"", getTableReference());
        }

        @Override
        public SortedSet<Long> getSelection() {
            return Collections.unmodifiableSortedSet(selectedGeometries);
        }

        @Override
        public void setSelection(Set<Long> selection) {
                LOGGER.debug("Editable selection change");
                Set<Long> oldSelection = this.selectedGeometries;
                this.selectedGeometries = new LongUnion(selection);
                propertyChangeSupport.firePropertyChange(PROP_SELECTION, oldSelection, getSelection());
        }

        @Override
        public String getTypeId() {
                return TYPE_ID;
        }

        @Override
        public boolean isFiltered() {
                return isFiltered;
        }

        @Override
        public void setFiltered(boolean isFiltered) {
            // Can filter only if there is selected geometries
            if(isFiltered || !selectedGeometries.isEmpty()) {
                boolean oldValue = this.isFiltered;
                this.isFiltered = isFiltered;
                propertyChangeSupport.firePropertyChange(PROP_FILTERED, oldValue, this.isFiltered);
            }
        }
}
