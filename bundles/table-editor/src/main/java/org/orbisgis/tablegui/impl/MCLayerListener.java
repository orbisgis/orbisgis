/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.tablegui.impl;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.LayerCollectionEvent;
import org.orbisgis.coremap.layerModel.LayerListener;
import org.orbisgis.coremap.layerModel.LayerListenerEvent;
import org.orbisgis.coremap.layerModel.SelectionEvent;
import org.orbisgis.tableeditorapi.TableEditableElement;

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
