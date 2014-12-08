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
package org.orbisgis.viewapi.edition;

import org.orbisgis.viewapi.docking.DockingPanelLayout;

/**
 * This factory can create an unlimited number of editors.
 * The panels opened by this editor should by retrieved at the next
 * application startup thanks to lightweight custom panel layout
 * Do not keep reference to the created panels, use the {@link EditorManager} 
 * getEditors function to retrieve the created panels.
 */

public interface MultipleEditorFactory extends EditorFactory {

    
    /**
     * Return a layout corresponding to this editable element
     * @param editable editable element
     * @return Instance of DockingPanelLayout or null if the editable 
     * is not compatible with this factory
     */
    public DockingPanelLayout makeEditableLayout(EditableElement editable);    
      
    
    // Serialisation
    
    /**
     * Create an empty layout, will be used to apply XML file or a byte stream
     * @return 
     */
    public DockingPanelLayout makeEmptyLayout();
    
    /**
     * 
     * @param layout
     * @return True if the layout corresponding to the layout of this factory
     */
    public boolean match(DockingPanelLayout layout);
    
    /**
     * Return a new DockingPanel for this serialised panel
     * @param layout
     * @return 
     */
    public EditorDockable create(DockingPanelLayout layout);
           
}
