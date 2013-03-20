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
package org.orbisgis.view.map;

import org.orbisgis.view.components.actions.MenuItemServiceTracker;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.SingleEditorFactory;
import org.orbisgis.view.main.frames.ext.ToolBarAction;
import org.orbisgis.view.map.ext.MapEditorAction;
import org.orbisgis.view.map.ext.MapEditorExtension;
import org.orbisgis.view.map.toolbar.DrawingToolBar;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * MapEditor cannot be opened twice, the the factory is a SingleEditorFactory.
 */
public class MapEditorFactory implements SingleEditorFactory {
        public static final String FACTORY_ID = "MapFactory";
        private MapEditor mapPanel = null;
        private DrawingToolBar drawingToolBar;
        private ServiceRegistration<ToolBarAction> drawingToolbarService;
        private MenuItemServiceTracker<MapEditorExtension,MapEditorAction> mapEditorExt;
        private BundleContext hostBundle;

        /**
         * Factory constructor
         * @param bc BundleContext for MapEditor extensions
         */
        public MapEditorFactory(BundleContext bc) {
            hostBundle = bc;
        }

        @Override
        public void dispose() {
            if(drawingToolbarService!=null) {
                drawingToolbarService.unregister();
            }
            if(mapEditorExt!=null) {
                mapEditorExt.close(); //Unregister MapEditor actions
            }
            if(mapPanel!=null) {
                mapPanel.dispose();
            }
        }

        @Override
        public EditorDockable[] getSinglePanels() {
                if(mapPanel==null) {
                        mapPanel = new MapEditor();
                        //Plugins Action will be added to ActionCommands of MapEditor
                        mapEditorExt = new MenuItemServiceTracker<MapEditorExtension,MapEditorAction>(hostBundle,MapEditorAction.class,mapPanel.getActionCommands(),mapPanel);
                        mapEditorExt.open(); // Start loading actions
                        // Create Drawing ToolBar
                        drawingToolBar = new DrawingToolBar(mapPanel);
                        drawingToolbarService = hostBundle.registerService(ToolBarAction.class,drawingToolBar,null);
                }
                return new EditorDockable[] {mapPanel};
        }

        @Override
        public String getId() {
                return FACTORY_ID;
        }
}
