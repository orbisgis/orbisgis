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
package org.orbisgis.coremap.process;

import com.vividsolutions.jts.geom.Envelope;
import org.apache.log4j.Logger;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.ReadTable;
import org.orbisgis.coremap.layerModel.MapContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import java.sql.SQLException;
import java.util.SortedSet;

/**
 * Fetch selection extent and apply it to the map context.
 * @author Nicolas Fortin
 */
public class ZoomToSelectedFeatures extends SwingWorkerPM {
        private static final Logger LOGGER = Logger.getLogger(ZoomToSelectedFeatures.class);
        protected final static I18n I18N = I18nFactory.getI18n(ZoomToSelectedFeatures.class);
        private DataManager dataManager;
        private String tableName;
        private SortedSet<Long> modelSelection;
        private MapContext mapContext;

        /**
         * Constructor.
         * @param dataManager data manager
         * @param tableName Table location
         * @param modelSelection Selected rows
         * @param mapContext Loaded map context
         */
        public ZoomToSelectedFeatures(DataManager dataManager, String tableName, SortedSet<Long> modelSelection, MapContext mapContext) {
            this.dataManager = dataManager;
            this.tableName = tableName;
            this.modelSelection = modelSelection;
            this.mapContext = mapContext;
            setTaskName(I18N.tr("Zoom to selection"));
        }

        @Override
        protected Object doInBackground() throws SQLException {
            try {
                Envelope selectionEnvelope = ReadTable.getTableSelectionEnvelope(dataManager, tableName, modelSelection, this);
                if(selectionEnvelope!=null) {
                    mapContext.setBoundingBox(selectionEnvelope);
                }
            }catch (SQLException ex) {
                LOGGER.error(I18N.tr("Unable to establish the selection bounding box"),ex);
            }
            return null;
        }
}
