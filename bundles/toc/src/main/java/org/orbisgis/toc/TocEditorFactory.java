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
package org.orbisgis.view.toc;

import org.orbisgis.view.components.actions.*;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.SingleEditorFactory;
import org.osgi.framework.*;

/**
 * This factory creates only one instance of Toc.
 */
public class TocEditorFactory implements SingleEditorFactory {
        public static final String FACTORY_ID = "TocFactory";
        private BundleContext bundleContext;
        // Looking for toc popup plugins
        private MenuItemServiceTracker<TocExt,TocActionFactory> popupActionTracker;
        private Toc tocPanel = null;

        /**
         *
         * @param bundleContext Bundle context in order to track toc plugins.
         */
        public TocEditorFactory(BundleContext bundleContext) {
            this.bundleContext = bundleContext;
        }

        @Override
        public void dispose() {
            if(popupActionTracker!=null) {
                popupActionTracker.close();
            }
        }

        @Override
        public EditorDockable[] getSinglePanels() {
                if(tocPanel==null) {
                        tocPanel = new Toc();
                        if(bundleContext!=null) {
                            popupActionTracker = new MenuItemServiceTracker<TocExt, TocActionFactory>(bundleContext,TocActionFactory.class,tocPanel.getPopupActions(),tocPanel);
                            popupActionTracker.open();
                        }
                }
                return new EditorDockable[] {tocPanel};
        }

        @Override
        public String getId() {
                return FACTORY_ID;
        }
}
