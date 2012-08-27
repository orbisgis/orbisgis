/**
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

import java.util.Set;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.view.geocatalog.EditableSource;

/**
 * Interface to be implemented by those EditableElements that need to be edited
 * by the table editor.
 * 
 */
public class TableEditableElement extends EditableSource {
        public static final String TYPE_ID = "TableEditableElement";
        // Properties names
        public static final String PROP_SELECTION = "selection";
        
        // Properties
        protected IntegerUnion selectedGeometries;

        public TableEditableElement(Set<Integer> selection, String sourceName) {
                super(sourceName);
                this.selectedGeometries = new IntegerUnion(selection);
        }

        public TableEditableElement(String sourceName) {
                super(sourceName);
                this.selectedGeometries = new IntegerUnion();
        }
        
	/**
	 * Get the selected geometries in the table
	 * @return
	 */
	public Set<Integer> getSelection() {
                return selectedGeometries;
        }

        /**
         * Set the selected geometries in the table
         * @param selection 
         */
        public void setSelection(Set<Integer> selection) {
                Set<Integer> oldSelection = this.selectedGeometries;
                this.selectedGeometries = new IntegerUnion(selection);
                propertyChangeSupport.firePropertyChange(PROP_SELECTION, oldSelection, this.selectedGeometries);
        }
        
        

        @Override
        public String getTypeId() {
                return TYPE_ID;
        }
}
