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
package org.orbisgis.view.frames;

import java.awt.BorderLayout;
import java.awt.dnd.*;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.orbisgis.utils.I18N;
/**
 * @brief This is the GeoCatalog panel. That Panel show the list of avaible layers
 * 
 * It is connected with the layer collection model.
 */
public class Catalog extends JPanel implements DragGestureListener,
		DragSourceListener {

	private DragSource dragSource;

	public Catalog() {
	        this.setName(I18N.getString("org.orbisgis.view.frames.Catalog.title"));
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(), BorderLayout.CENTER);
		dragSource = DragSource.getDefaultDragSource();

	}
        /**
         * Implement DragGestureListener
         * @param dge DragGestureEvent instance
         */
        public void dragGestureRecognized(DragGestureEvent dge) {
        }

        public void dragEnter(DragSourceDragEvent dsde) {
        }

        public void dragOver(DragSourceDragEvent dsde) {
        }

        public void dropActionChanged(DragSourceDragEvent dsde) {
        }

        public void dragExit(DragSourceEvent dse) {
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
        }

}
