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
package org.orbisgis.view.table.jobs;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public class ZoomToSelectionJob implements BackgroundJob {
        private static final Logger LOGGER = Logger.getLogger(ZoomToSelectionJob.class);
        protected final static I18n I18N = I18nFactory.getI18n(ZoomToSelectionJob.class);
        private DataSource dataSource;
        private int[] modelSelection;
        private MapContext mapContext;

        public ZoomToSelectionJob(DataSource dataSource, int[] modelSelection, MapContext mapContext) {
                this.dataSource = dataSource;
                this.modelSelection = modelSelection;
                this.mapContext = mapContext;
        }
        
        
           
        private Envelope getVectorialRowEnvelope(int rowId) throws DriverException {
                Geometry geometry = dataSource.getGeometry(rowId);
                if (geometry != null) {
                        return geometry.getEnvelopeInternal();
                }
                return null;
        }
        
        private Envelope getRasterRowEnvelope(int rowId) throws DriverException {
                return dataSource.getRaster(rowId).getMetadata().getEnvelope();
        }
        
        @Override
        public void run(ProgressMonitor pm) {
                Envelope selectionEnvelope = null;               
                try {
                        boolean isVectorial = dataSource.isVectorial();
                        boolean isRaster = dataSource.isRaster();
                        //Evaluate the selection bounding box
                        int done=0;
                        if(isVectorial) {
                                selectionEnvelope = getVectorialRowEnvelope(modelSelection[0]);                                
                                for(int modelId : modelSelection) {
                                        Envelope rowEnvelope = getVectorialRowEnvelope(modelId);
                                        if(rowEnvelope!=null) {
                                                selectionEnvelope.expandToInclude(rowEnvelope);
                                        }
                                        if(pm.isCancelled()) {
                                                return;
                                        } else {
                                                pm.progressTo(done / modelSelection.length * 100);
                                        }
                                        done++;
                                }
                        } else if(isRaster) {
                                selectionEnvelope = getVectorialRowEnvelope(modelSelection[0]);
                                for(int modelId : modelSelection) {
                                        selectionEnvelope.expandToInclude(getRasterRowEnvelope(modelId));   
                                        if(pm.isCancelled()) {
                                                return;
                                        } else {
                                                pm.progressTo(done / modelSelection.length * 100);
                                        }
                                        done++;                                     
                                }
                        }
                }catch (DriverException ex) {
                        LOGGER.error(I18N.tr("Unable to establish the selection bounding box"),ex);
                        return;
                }
                if(selectionEnvelope!=null) {
                        mapContext.setBoundingBox(selectionEnvelope);                                
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Zoom to selection");
        }
        
}
