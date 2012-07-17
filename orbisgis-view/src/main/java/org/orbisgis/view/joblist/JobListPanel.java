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
package org.orbisgis.view.joblist;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.apache.log4j.Logger;

/**
 * A panel that manage a list of subpanel.
 * 
 * This panel use the same model and renderer as a JList but it
 * store the item components. This class will respond to component repaint
 * but it is heavier than a JList
 */
public class JobListPanel extends JPanel {
        private ListModel listModel;
        private ListCellRenderer listRenderer;
        private BoxLayout cellsStack;
        private ModelListener modelListener = new ModelListener();
        private static final Logger LOGGER = Logger.getLogger(JobListPanel.class);
        
        public JobListPanel() {
                cellsStack = new BoxLayout(this, BoxLayout.Y_AXIS);
                setLayout(cellsStack);
        }

        /**
         * 
         * @return The JobListModel
         */
        public ListModel getModel() {
                return listModel;
        }

        @Override
        public void removeNotify() {
                super.removeNotify();
                setModel(null);               
        }

        
        
        /**
         * Set the model, contents change events will be ignored,
         * The component attach with the item must be updated
         * @param listModel 
         */
        public void setModel(ListModel listModel) {
                if(this.listModel!=null) {
                        this.listModel.removeListDataListener(modelListener);
                }
                this.listModel = listModel;
                if(listModel!=null) {
                        listModel.addListDataListener(modelListener);
                        //Read the content
                        for(int rowi=0;rowi<listModel.getSize();rowi++) {
                                onAddRow(rowi);
                        }
                        repaint();
                }
        }

        /**
         * The renderer
         * @return 
         */
        public ListCellRenderer getListRenderer() {
                return listRenderer;
        }

        /**
         * Sets the renderer, the component is required once for each component
         * @param listRenderer 
         */
        public void setRenderer(ListCellRenderer listRenderer) {
                this.listRenderer = listRenderer;
        }
        
        private void onAddRow(int index) {
                add(listRenderer.getListCellRendererComponent(null, listModel.getElementAt(index), index, true, true));
        }
        /**
         * Remove the Swing component
         * @param index 
         */
        private void onRemoveRow(int index) {
                try {
                        if(index<getComponentCount()) {
                                remove(index);
                        }
                } catch (ArrayIndexOutOfBoundsException ex) {
                        LOGGER.error(ex);
                }
        }
        
        private class ModelListener implements ListDataListener {

                @Override
                public void intervalAdded(ListDataEvent lde) {
                        for(int index=lde.getIndex0();index<=lde.getIndex1();index++) {
                                onAddRow(index);
                        }
                        repaint();
                }

                @Override
                public void intervalRemoved(ListDataEvent lde) {
                        for(int index=lde.getIndex0();index<=lde.getIndex1();index++) {
                                onRemoveRow(index);
                        }
                        repaint();
                }

                @Override
                public void contentsChanged(ListDataEvent lde) {
                        //JPanel already listen for child updates
                }
        }
}
