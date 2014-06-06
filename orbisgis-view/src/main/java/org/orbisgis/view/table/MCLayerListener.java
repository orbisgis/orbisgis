package org.orbisgis.view.table;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.LayerCollectionEvent;
import org.orbisgis.coremap.layerModel.LayerListener;
import org.orbisgis.coremap.layerModel.LayerListenerEvent;
import org.orbisgis.coremap.layerModel.SelectionEvent;
import org.orbisgis.viewapi.table.TableEditableElement;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Synchronize layer selection -> Table selection
 * @author Nicolas Fortin
 */
public class MCLayerListener implements LayerListener {
    private final TableEditableElement tableEditableElement;
    private final AtomicBoolean fireRowSelectionEvent = new AtomicBoolean(true);
    private final TableLocation editorTable;

    /**
     * Constructor
     * @param tableEditableElement Table editable element instance
     */
    public MCLayerListener(TableEditableElement tableEditableElement) {
        this.tableEditableElement = tableEditableElement;
        editorTable = TableLocation.parse(tableEditableElement.getTableReference());
    }

    @Override
    public void nameChanged(LayerListenerEvent e) {
    }

    @Override
    public void visibilityChanged(LayerListenerEvent e) {
    }

    @Override
    public void styleChanged(LayerListenerEvent e) {
    }

    @Override
    public void layerAdded(LayerCollectionEvent e) {
        for (final ILayer layer : e.getAffected()) {
            layer.addLayerListenerRecursively(this);
        }
    }

    @Override
    public void layerRemoved(LayerCollectionEvent e) {
        for (final ILayer layer : e.getAffected()) {
            layer.removeLayerListenerRecursively(this);
        }
    }

    @Override
    public boolean layerRemoving(LayerCollectionEvent layerCollectionEvent) {
        for (final ILayer layer : layerCollectionEvent.getAffected()) {
            layer.removeLayerListener(this);
        }
        return true;
    }

    @Override
    public void layerMoved(LayerCollectionEvent e) {

    }

    @Override
    public void selectionChanged(SelectionEvent e) {
        ILayer layer = ((ILayer)e.getSource());
        TableLocation layerTable = TableLocation.parse(layer.getTableReference());
        if(editorTable.getSchema().equals(layerTable.getSchema()) &&
                editorTable.getTable().equals(layerTable.getTable()) && fireRowSelectionEvent.getAndSet(false)) {
            try {
                //Update table element selection
                tableEditableElement.setSelection(layer.getSelection());
            } finally {
                fireRowSelectionEvent.set(true);
            }
        }
    }
}
