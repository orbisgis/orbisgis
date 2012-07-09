
package org.orbisgis.view.joblist;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * A panel that manage a list of subpanel
 */
public class JobListPanel extends JPanel {
        private ListModel listModel;
        private ListCellRenderer listRenderer;
        private BoxLayout cellsStack;
        private ModelListener modelListener = new ModelListener();

        public JobListPanel() {
                cellsStack = new BoxLayout(this, BoxLayout.Y_AXIS);
                setLayout(cellsStack);
        }

        public ListModel getModel() {
                return listModel;
        }

        public void setModel(ListModel listModel) {
                if(this.listModel!=null) {
                        this.listModel.removeListDataListener(modelListener);
                }
                listModel.addListDataListener(modelListener);
                this.listModel = listModel;
        }

        public ListCellRenderer getListRenderer() {
                return listRenderer;
        }

        public void setRenderer(ListCellRenderer listRenderer) {
                this.listRenderer = listRenderer;
        }
        
        private void onAddRow(int index) {
                add(listRenderer.getListCellRendererComponent(null, listModel.getElementAt(index), index, true, true));
                repaint();
        }
        
        private void onRemoveRow(int index) {
                remove(index);
                repaint();
        }
        
        private class ModelListener implements ListDataListener {

                @Override
                public void intervalAdded(ListDataEvent lde) {
                        for(int index=lde.getIndex0();index<=lde.getIndex1();index++) {
                                onAddRow(index);
                        }
                }

                @Override
                public void intervalRemoved(ListDataEvent lde) {
                        for(int index=lde.getIndex0();index<=lde.getIndex1();index++) {
                                onRemoveRow(index);
                        }
                }

                @Override
                public void contentsChanged(ListDataEvent lde) {
                        //JPanel already listen for child updates
                }
        }
}
