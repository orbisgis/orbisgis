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
package org.orbisgis.view.background;

import com.vividsolutions.jts.geom.Envelope;

import java.sql.SQLException;
import java.util.Set;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Zoom to provided layer selection
 * @author Nicolas Fortin
 */
public class ZoomToSelection implements BackgroundJob {
        private static final I18n I18N = I18nFactory.getI18n(ZoomToSelection.class);
        private static final Logger LOGGER = Logger.getLogger(ZoomToSelection.class);
        private MapContext mapContext;
        private ILayer[] layers;

        public ZoomToSelection(MapContext mapContext, ILayer[] layers) {
            this.mapContext = mapContext;
            this.layers = layers;
        }

        @Override
        public void run(ProgressMonitor pm) {
            try {
                Envelope selectionEnvelope = new Envelope();
                for(ILayer layer : layers) {
                    if(layer.isVisible()) {
                        Envelope layerEnv = getLayerSelectionEnvelope(pm, layer);
                        if(layerEnv!=null) {
                            selectionEnvelope.expandToInclude(layerEnv);
                        }
                        if(pm.isCancelled()) {
                            return;
                        }
                    }
                }
                if(!selectionEnvelope.isNull()) {
                    mapContext.setBoundingBox(selectionEnvelope);
                }
            } catch (SQLException ex ){
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }

        private Envelope getLayerSelectionEnvelope(ProgressMonitor pm, ILayer layer) throws SQLException {
            SortedSet<Integer> sortedSet;
            Set<Integer> data = layer.getSelection();
            if(data instanceof SortedSet) {
                sortedSet = (SortedSet<Integer>)data;
            } else {
                sortedSet = new IntegerUnion(data);
            }
            return ReadTable.getTableSelectionEnvelope(mapContext.getDataManager(), layer.getTableReference(),sortedSet, pm);
        }
        
        @Override
        public String getTaskName() {
                return I18N.tr("Zoom to the selected geometries");
        }
        
}
