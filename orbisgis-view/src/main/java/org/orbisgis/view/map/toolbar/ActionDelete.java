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

package org.orbisgis.view.map.toolbar;

import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.DefaultJobId;
import org.orbisgis.view.components.gdms.DeleteRows;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.main.frames.ext.ToolBarAction;
import org.orbisgis.view.map.ext.MapEditorExtension;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

/**
 * Delete selected geometries.
 * @author Nicolas Fortin
 */
public class ActionDelete extends ActionActiveLayer {
    private static final I18n I18N = I18nFactory.getI18n(ActionDelete.class);
    private PropertyChangeListener selectionChangeListener = EventHandler.create(PropertyChangeListener.class,this,"onSelectionUpdate");

    /**
     * Constructor
     * @param extension MapExtension instance
     */
    public ActionDelete(MapEditorExtension extension) {
        super(ToolBarAction.DRAW_DELETE, I18N.tr("Delete"), extension, OrbisGISIcon.getIcon("edition/delete"));
        setToolTipText(I18N.tr("Delete selected geometries"));
        setLogicalGroup(ToolBarAction.DRAWING_GROUP);
    }

    /**
     * Selection has been updated
     */
    public void onSelectionUpdate() {
        checkActionState();
    }
    @Override
    protected void checkActionState() {
        super.checkActionState();
        if(getActiveLayer()!=null) {
            setEnabled(!getActiveLayer().getSelection().isEmpty());
        }
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
            activeLayer.addPropertyChangeListener(ILayer.PROP_SELECTION, selectionChangeListener);
        }
    }
    private void removeLayerListeners(ILayer activeLayer) {
        if(activeLayer!=null) {
            activeLayer.removePropertyChangeListener(selectionChangeListener);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        ILayer activeLayer = getActiveLayer();
        if(activeLayer!=null) {
            Set<Integer> selectedRows = activeLayer.getSelection();
            int response = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                    I18N.tr("Are you sure to remove the {0} selected geometries ?", selectedRows.size()),
                    I18N.tr("Delete selected geometries"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(response==JOptionPane.YES_OPTION) {
                // Launch process
                BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);
                backgroundManager.nonBlockingBackgroundOperation(new DefaultJobId("DeleteRows"),new DeleteRows(selectedRows,activeLayer.getDataSource()));
            }
        }
    }
}
