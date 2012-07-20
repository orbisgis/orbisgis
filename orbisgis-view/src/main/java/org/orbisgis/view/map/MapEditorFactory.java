/**
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
package org.orbisgis.view.map;

import org.apache.log4j.Logger;
import org.orbisgis.view.edition.EditableElementException;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.SingleEditorFactory;

/**
 * MapEditor cannot be opened twice, the the factory is a SingleEditorFactory.
 */
public class MapEditorFactory implements SingleEditorFactory {
        public static final String FACTORY_ID = "MapFactory";
        private static final Logger LOGGER = Logger.getLogger(MapEditorFactory.class);
        private MapEditor mapPanel = null;

        public void dispose() {
                //Save the loaded map
                if(mapPanel!=null && mapPanel.getEditableElement()!=null) {
                        if(mapPanel.getEditableElement().isModified()) {
                                try {
                                        mapPanel.getEditableElement().save();
                                } catch (UnsupportedOperationException ex) {
                                        LOGGER.error(ex);
                                } catch (EditableElementException ex) {
                                        LOGGER.error(ex);
                                }
                        }
                }                
        }

        public EditorDockable[] getSinglePanels() {
                if(mapPanel==null) {
                        mapPanel = new MapEditor();
                }
                return new EditorDockable[] {mapPanel};
        }

        public String getId() {
                return FACTORY_ID;
        }
}
