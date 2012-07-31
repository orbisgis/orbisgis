/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import org.orbisgis.view.docking.DockingPanelLayout;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.MultipleEditorFactory;

/**
 *  This factory receive the {@link TableEditableElement} and open a new editor.
 */
public class TableEditorFactory implements MultipleEditorFactory {
        public static final String FACTORY_ID = "TableEditorFactory";

        @Override
        public DockingPanelLayout makeEditableLayout(EditableElement editable) {
                if(editable instanceof TableEditableElement) {
                        return new TablePanelLayout((TableEditableElement)editable);
                } else {
                        return null;
                }
        }

        @Override
        public DockingPanelLayout makeEmptyLayout() {
                return new TablePanelLayout();
        }

        @Override
        public boolean match(DockingPanelLayout layout) {
                return layout instanceof TablePanelLayout;
        }

        @Override
        public EditorDockable create(DockingPanelLayout layout) {
                return new TableEditor(((TablePanelLayout)layout).getTableEditableElement());
        }

        @Override
        public DockingPanelLayout getLayout(EditorDockable panel) {
                return new TablePanelLayout(((TableEditor)panel).getTableEditableElement());
        }

        @Override
        public String getId() {
                return FACTORY_ID;
        }

        @Override
        public void dispose() {
                //
        }        
}
