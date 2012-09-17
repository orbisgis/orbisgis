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
package org.orbisgis.view.map.jobs;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.util.Set;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Zoom to provided layer selection
 * @author Nicolas Fortin
 */
public class ZoomToSelection implements BackgroundJob {
        private static final I18n I18N = I18nFactory.getI18n(ZoomToSelection.class);
        
        private MapContext mapContext;
        private ILayer[] layers;

        public ZoomToSelection(MapContext mapContext, ILayer[] layers) {
                this.mapContext = mapContext;
                this.layers = layers;
        }        
        
        @Override
        public void run(ProgressMonitor pm) {
                Envelope selectionEnvelope = new Envelope();
                for(ILayer layer : layers) {
                        try {
                                if(layer.isVisible()) {
                                        Envelope layerEnv = getLayerSelectionEnvelope(pm, layer);
                                        if(layerEnv!=null) {
                                                selectionEnvelope.expandToInclude(layerEnv);
                                        }
                                        if(pm.isCancelled()) {
                                                return;
                                        }
                                }
                        } catch(DriverException ex) {
                                I18N.tr("Unable to compute the zoom extent",ex);
                                return;
                        }
                }
                if(!selectionEnvelope.isNull()) {
                        mapContext.setBoundingBox(selectionEnvelope);
                }
        }

        private Envelope getLayerSelectionEnvelope(ProgressMonitor pm, ILayer layer) throws DriverException {
                Envelope selectionEnvelope = new Envelope();
                DataSource dataSource = layer.getDataSource();
                if(dataSource!=null) {
                        pm.startTask(I18N.tr("Compute envelope of {0}",layer.getName()), 100);
                        boolean isVectorial = dataSource.isVectorial();
                        boolean isRaster = dataSource.isRaster();
                        //Evaluate the selection bounding box
                        Set<Integer> modelSelection = layer.getSelection();
                        int selectionSize = modelSelection.size();
                        int done = 0;
                        if (isVectorial) {
                                for (int modelId : modelSelection) {
                                        Envelope rowEnvelope = getVectorialRowEnvelope(dataSource,modelId);
                                        if (rowEnvelope != null) {
                                                selectionEnvelope.expandToInclude(rowEnvelope);
                                        }
                                        if (pm.isCancelled()) {
                                                return null;
                                        } else {
                                                pm.progressTo(done / selectionSize * 100);
                                        }
                                        done++;
                                }
                        } else if (isRaster) {
                                for (int modelId : modelSelection) {
                                        selectionEnvelope.expandToInclude(getRasterRowEnvelope(dataSource, modelId));
                                        if (pm.isCancelled()) {
                                                return null;
                                        } else {
                                                pm.progressTo(done / selectionSize * 100);
                                        }
                                        done++;
                                }
                        }
                        pm.endTask();
                        return selectionEnvelope;
                } else {
                        return null;
                }
        }

        private Envelope getVectorialRowEnvelope(DataSource dataSource, int rowId) throws DriverException {
                Geometry geometry = dataSource.getGeometry(rowId);
                if (geometry != null) {
                        return geometry.getEnvelopeInternal();
                }
                return null;
        }
        
        private Envelope getRasterRowEnvelope(DataSource dataSource, int rowId) throws DriverException {
                return dataSource.getRaster(rowId).getMetadata().getEnvelope();
        }
        
        @Override
        public String getTaskName() {
                return I18N.tr("Zoom to the selected geometries");
        }
        
}
