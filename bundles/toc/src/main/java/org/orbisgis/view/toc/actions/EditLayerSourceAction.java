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

package org.orbisgis.view.toc.actions;

import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.view.toc.Toc;
import org.orbisgis.viewapi.toc.ext.TocActionFactory;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * @author Nicolas Fortin
 */
public class EditLayerSourceAction extends LayerAction {
    private Toc toc;
    private boolean enabledOnActiveLayer = false;
    private boolean enabledOnNotActiveLayer = false;
    private boolean enabledOnModifiedLayer = false;

    /**
     * Constructor
     * @param toc Toc instance
     * @param actionId Action identifier, should be unique for ActionCommands
     * @param actionLabel I18N label short label
     * @param actionToolTip I18N tool tip text
     * @param icon Icon
     * @param actionListener Fire the event to this listener
     * @param keyStroke ShortCut for this action
     */
    public EditLayerSourceAction(Toc toc, String actionId, String actionLabel, String actionToolTip, Icon icon, ActionListener actionListener, KeyStroke keyStroke) {
        super(toc,actionId, actionLabel, actionToolTip, icon, actionListener, keyStroke);
        this.toc = toc;
        setLogicalGroup(TocActionFactory.G_DRAWING);
        setOnRealLayerOnly(true);
        setOnVectorSourceOnly(true);
    }

    /**
     * @param enabledOnActiveLayer If true this action will be seen only on map active layer
     * {@link org.orbisgis.coremap.layerModel.MapContext#setActiveLayer(org.orbisgis.coremap.layerModel.ILayer)}
     */
    public EditLayerSourceAction setEnabledOnActiveLayer(boolean enabledOnActiveLayer) {
        this.enabledOnActiveLayer = enabledOnActiveLayer;
        return this;
    }

    /**
     * @param enabledOnModifiedLayer If true this action is only visible on layer with modified data source
     */
    public EditLayerSourceAction setEnabledOnModifiedLayer(boolean enabledOnModifiedLayer) {
        this.enabledOnModifiedLayer = enabledOnModifiedLayer;
        return this;
    }
    /**
    * @param enabledOnNotActiveLayer If true this action will be seen only if this layer is not the map active layer.
    * {@link org.orbisgis.coremap.layerModel.MapContext#setActiveLayer(org.orbisgis.coremap.layerModel.ILayer)}
     */
    public EditLayerSourceAction setEnabledOnNotActiveLayer(boolean enabledOnNotActiveLayer) {
        this.enabledOnNotActiveLayer = enabledOnNotActiveLayer;
        return this;
    }

    /**
     * Read selected dataSource and check for action constraints
     * @return True if the check succeed and the action can be shown.
     */
    private boolean checkDataSource() {
        ILayer activeLayer = toc.getMapContext().getActiveLayer();
        for(ILayer layer : toc.getSelectedLayers()) {
                    if(layer.getTableReference()==null || !layer.getTableReference().isEmpty() ||
                            ((enabledOnActiveLayer && !layer.equals(activeLayer))
                            || (enabledOnNotActiveLayer && layer.equals(activeLayer))
                            )) {
                        // TODO || (enabledOnModifiedLayer && !src.isModified())
                        return false;
                    }
        }
        return true;
    }
    @Override
    public boolean isEnabled() {
        return checkDataSource() && super.isEnabled();
    }
}
