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
package org.orbisgis.coremap.process;

import com.vividsolutions.jts.geom.Envelope;

import java.sql.SQLException;
import java.util.Set;
import java.util.SortedSet;

import org.slf4j.*;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.corejdbc.common.LongUnion;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Zoom to provided layer selection
 * @author Nicolas Fortin
 */
public class ZoomToSelection extends SwingWorkerPM {
        private static final I18n I18N = I18nFactory.getI18n(ZoomToSelection.class);
        private static final Logger LOGGER = LoggerFactory.getLogger(ZoomToSelection.class);
        private MapContext mapContext;
        private ILayer[] layers;

        public ZoomToSelection(MapContext mapContext, ILayer[] layers) {
            this.mapContext = mapContext;
            this.layers = layers;
            setTaskName(I18N.tr("Zoom to the selected geometries"));
        }

        @Override
        protected Object doInBackground() throws Exception {
            Thread.currentThread().setName(this.getClass().getSimpleName());
            try {
                Envelope selectionEnvelope = new Envelope();
                for(ILayer layer : layers) {
                    if(layer.isVisible()) {
                        Set<Long> data = layer.getSelection();
                        if(!data.isEmpty()){
                            Envelope layerEnv = getLayerSelectionEnvelope(data, layer.getTableReference());
                            if(layerEnv!=null) {
                                selectionEnvelope.expandToInclude(layerEnv);
                            }
                            if(isCancelled()) {
                                break;
                            }
                        }
                    }
                }
                if(!selectionEnvelope.isNull()) {
                    mapContext.setBoundingBox(selectionEnvelope);
                }
            } catch (SQLException ex ){
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
            return null;
        }
        
        /**
         * Compute the envelope based on the selection
         * @param data
         * @param tableReference
         * @return
         * @throws SQLException 
         */
        private Envelope getLayerSelectionEnvelope(Set<Long> data,String tableReference) throws SQLException {
            SortedSet<Long> sortedSet;
            if(data instanceof SortedSet) {
                sortedSet = (SortedSet<Long>)data;
            } else {
                sortedSet = new LongUnion(data);
            }
            return ReadTable.getTableSelectionEnvelope(mapContext.getDataManager(), tableReference,sortedSet, this.getProgressMonitor());
        }
}
