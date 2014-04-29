/**
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

import java.util.Collections;
import java.util.Set;
import org.apache.log4j.Logger;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.view.geocatalog.EditableSourceImpl;
import org.orbisgis.viewapi.table.TableEditableElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Interface to be implemented by those EditableElements that need to be edited
 * by the table editor.
 * 
 */
public class TableEditableElementImpl extends EditableSourceImpl implements TableEditableElement {
        public static final String TYPE_ID = "TableEditableElement";
        private static final Logger LOGGER = Logger.getLogger(TableEditableElementImpl.class);
        // Properties
        protected IntegerUnion selectedGeometries;
        private final I18n i18n = I18nFactory.getI18n(TableEditableElementImpl.class);

        /**
         * Constructor
         * @param selection
         * @param sourceName
         * @param dataManager
         */
        public TableEditableElementImpl(Set<Integer> selection, String sourceName, DataManager dataManager) {
                super(sourceName, dataManager);
                this.selectedGeometries = new IntegerUnion(selection);
        }

        /**
         * Constructor
         * @param sourceName
         * @param dataManager
         */
        public TableEditableElementImpl(String sourceName, DataManager dataManager) {
                super(sourceName, dataManager);
                this.selectedGeometries = new IntegerUnion();
        }

        @Override
        public String toString() {
            return i18n.tr("Table \"{0}\"", getTableReference());
        }

        @Override
        public Set<Integer> getSelection() {
            return Collections.unmodifiableSet(selectedGeometries);
        }

        @Override
        public void setSelection(Set<Integer> selection) {
                LOGGER.debug("Editable selection change");
                Set<Integer> oldSelection = this.selectedGeometries;
                this.selectedGeometries = new IntegerUnion(selection);
                propertyChangeSupport.firePropertyChange(PROP_SELECTION, oldSelection, getSelection());
        }

        @Override
        public String getTypeId() {
                return TYPE_ID;
        }
}
