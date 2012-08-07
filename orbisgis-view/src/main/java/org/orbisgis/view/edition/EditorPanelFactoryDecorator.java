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
package org.orbisgis.view.edition;

import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelFactory;
import org.orbisgis.view.docking.DockingPanelLayout;

/**
 * MultipleEditorFactory must define EditorPanels instead of DockingPanel
 * then this decorator 
 * make the link between a MultipleEditorFactory and a DcokingPanelFactory.
 */

public class EditorPanelFactoryDecorator implements DockingPanelFactory {
        private MultipleEditorFactory multipleEditorFactory;

        public EditorPanelFactoryDecorator(MultipleEditorFactory multipleEditorFactory) {
                this.multipleEditorFactory = multipleEditorFactory;
        }

        public DockingPanelLayout makeEmptyLayout() {
                return multipleEditorFactory.makeEmptyLayout();
        }

        public boolean match(DockingPanelLayout layout) {
                return multipleEditorFactory.match(layout);
        }

        public DockingPanel create(DockingPanelLayout layout) {
                return multipleEditorFactory.create(layout);
        }

        public DockingPanelLayout getLayout(DockingPanel panel) {
                if(!(panel instanceof EditorDockable)) {
                        throw new IllegalArgumentException("Editors factory accepts only EditorDockable layout");
                }
                return multipleEditorFactory.getLayout((EditorDockable)panel);
        }
        
}
