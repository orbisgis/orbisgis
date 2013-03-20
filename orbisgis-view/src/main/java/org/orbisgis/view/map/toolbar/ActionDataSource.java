/*
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

package org.orbisgis.view.map.toolbar;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceListener;
import org.gdms.data.edition.EditionListener;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.ext.MapEditorExtension;

import javax.swing.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;

/**
 * This action check its enabled state depending on {@link MapContext#PROP_ACTIVELAYER} DataSource.
 * @author Nicolas Fortin
 */
public class ActionDataSource extends ActionActiveLayer {
    private final EditionListener editionListener = EventHandler.create(EditionListener.class, this, "sourceEvent");
    private final DataSourceListener dataSourceListener = EventHandler.create(DataSourceListener.class, this, "sourceEvent");

    /**
     * Constructor
     * @param actionId Unique action id
     * @param name Action name
     * @param extension MapEditor instance
     * @param icon Action icon
     */
    public ActionDataSource(String actionId, String name, MapEditorExtension extension,Icon icon) {
        super(actionId, name, extension,icon);
        addTrackedMapContextProperty(MapContext.PROP_ACTIVELAYER);
        addLayerListeners(getActiveLayer());
    }

    @Override
    public void onMapContextUpdate(PropertyChangeEvent evt) {
        super.onMapContextUpdate(evt);
        if(MapContext.PROP_ACTIVELAYER.equals(evt.getPropertyName())) {
            ILayer oldActive = (ILayer) evt.getOldValue();
            ILayer newActive = (ILayer) evt.getNewValue();
            if(oldActive!=null) {
                removeLayerListeners(oldActive);
            }
            if(newActive!=null) {
                addLayerListeners(newActive);
            }
        }
    }

    @Override
    protected void installMapContextListener(MapContext mapContext) {
        super.installMapContextListener(mapContext);
        addLayerListeners(mapContext.getActiveLayer());
    }

    @Override
    protected void removeMapContextListener(MapContext mapContext) {
        super.removeMapContextListener(mapContext);
        removeLayerListeners(mapContext.getActiveLayer());
    }

    private void addLayerListeners(ILayer activeLayer) {
        if(activeLayer!=null) {
            DataSource dataSource = activeLayer.getDataSource();
            if(dataSource!=null && dataSource.isEditable()) {
                try {
                    dataSource.addEditionListener(editionListener);
                    dataSource.addDataSourceListener(dataSourceListener);
                } catch (UnsupportedOperationException ex) {
                    //An active layer would be always an editable
                }
            }
        }
    }
    private void removeLayerListeners(ILayer activeLayer) {
        if(activeLayer!=null) {
            DataSource dataSource = activeLayer.getDataSource();
            if(dataSource!=null && dataSource.isEditable()) {
                try {
                    dataSource.removeEditionListener(editionListener);
                    dataSource.removeDataSourceListener(dataSourceListener);
                } catch (UnsupportedOperationException ex) {
                    //An active layer would be always an editable
                }
            }
        }
    }

    /**
     * Called when the DataSource has been updated
     */
    public final void sourceEvent() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                checkActionState();
            }
        });
    }
    @Override
    public void dispose() {
        super.dispose();
        // Remove active layer source listener
        removeLayerListeners(getActiveLayer());
    }
}
