/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */ 
package org.orbisgis.view.edition;

import java.util.ArrayList;
import java.util.List;
import org.orbisgis.view.docking.DockingManager;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelLayout;

/**
 * The editor Manager is responsible of all EditorFactories.
 * It can 
 *  -for an editableElement, find and open the appropriate editor(s) through declared EditorFactories
 *  -save the state of all EditableElement opened;
 */


public class EditorManager {
    private List<EditorFactory> factories = new ArrayList<EditorFactory>();
    private DockingManager dockingManager;

        public EditorManager(DockingManager dockingManager) {
                this.dockingManager = dockingManager;
        }
    
    
        /**
        * Add a new editor factory, if the factory is a SingleEditorFactory then
        * the panels are immediately shown
        * @param editorFactory 
        */
        public void addEditorFactory(EditorFactory editorFactory) {
                factories.add(editorFactory);
                if(editorFactory instanceof MultipleEditorFactory) {
                        dockingManager.registerPanelFactory(editorFactory.getId(), new EditorPanelFactoryDecorator((MultipleEditorFactory)editorFactory));
                } else {
                        for(EditorDockable dockPanel : ((SingleEditorFactory)editorFactory).getSinglePanels()) {
                                dockingManager.show(dockPanel);
                        }
                }
        }

        /**
        * Open this editable with all compatible factories.
        * @param editableElement 
        */
        public void openEditable(EditableElement editableElement) {
                
                // Open the element in editors
                for( DockingPanel panel : dockingManager.getPanels()) {
                        if(panel instanceof EditorDockable) {
                                EditorDockable editor = (EditorDockable)panel;
                                editor.setEditableElement(editableElement);
                        }
                }
                
                //Open the element in MultipleEditorFactories
                for( EditorFactory factory : factories) {
                        if(factory instanceof MultipleEditorFactory) {
                                MultipleEditorFactory mFactory = (MultipleEditorFactory)factory;
                                DockingPanelLayout data = mFactory.makeEditableLayout(editableElement);
                                if(data!=null) {
                                        dockingManager.show(mFactory.getId(), data);
                                }
                        }
                }
        }

        /**
        * Release all factories resources
        */
        public void dispose() {
                for(EditorFactory factory : factories) {
                        factory.dispose();
                }
        }
}
