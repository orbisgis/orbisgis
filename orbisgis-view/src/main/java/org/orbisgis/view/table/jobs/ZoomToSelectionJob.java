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
package org.orbisgis.view.table.jobs;

import com.vividsolutions.jts.geom.Envelope;
import org.apache.log4j.Logger;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.jdbc.ReadTable;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

/**
 * Fetch selection extent and apply it to the map context.
 * @author Nicolas Fortin
 */
public class ZoomToSelectionJob implements BackgroundJob {
        private static final Logger LOGGER = Logger.getLogger(ZoomToSelectionJob.class);
        protected final static I18n I18N = I18nFactory.getI18n(ZoomToSelectionJob.class);
        private DataSource dataSource;
        private String tableName;
        private int[] modelSelection;
        private MapContext mapContext;

        /**
         * Constructor.
         * @param dataSource Active data source
         * @param tableName Table location
         * @param modelSelection Selected rows
         * @param mapContext Loaded map context
         */
        public ZoomToSelectionJob(DataSource dataSource,String tableName, int[] modelSelection, MapContext mapContext) {
                this.dataSource = dataSource;
                this.tableName = tableName;
                this.modelSelection = modelSelection;
                this.mapContext = mapContext;
        }
        
        @Override
        public void run(ProgressMonitor pm) {
                Envelope selectionEnvelope = null;
                try(Connection connection = dataSource.getConnection()) {
                    selectionEnvelope = ReadTable.getTableSelectionEnvelope(connection, tableName, modelSelection, pm);
                    if(selectionEnvelope!=null) {
                        mapContext.setBoundingBox(selectionEnvelope);
                    }
                }catch (SQLException ex) {
                        LOGGER.error(I18N.tr("Unable to establish the selection bounding box"),ex);
                }
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Zoom to selection");
        }
        
}
