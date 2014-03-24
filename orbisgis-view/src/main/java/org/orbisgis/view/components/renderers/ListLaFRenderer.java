/*
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
package org.orbisgis.view.components.renderers;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Instructions : Implement the method getListCellRendererComponent
 * Customise the component returned by lookAndFeelRenderer.getListCellRendererComponent .
 * 
 * @author Nicolas Fortin
 */
public abstract class ListLaFRenderer implements ListCellRenderer {
        
        protected ListCellRenderer lookAndFeelRenderer;
        
        /**
         * Update the native renderer.
         * Warning, Used only by PropertyChangeListener on UI property
         */
        public void updateLFRenderer() {
                lookAndFeelRenderer = new JList().getCellRenderer();
        }
        /**
         * Set listener to L&F events
         * @param list Where the listener has to be installed
         */
        public ListLaFRenderer(JList list) {
                initialize(list);
        }

        private void initialize(JList list) {
                updateLFRenderer();
                list.addPropertyChangeListener("UI",
                        EventHandler.create(PropertyChangeListener.class,this,"updateLFRenderer"));
        }

}
