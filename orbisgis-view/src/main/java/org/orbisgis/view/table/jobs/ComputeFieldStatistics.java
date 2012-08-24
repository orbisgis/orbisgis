/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import java.io.IOException;
import java.util.Set;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SQLScript;
import org.gdms.sql.engine.SQLStatement;

/**
 *
 * @author Nicolas Fortin
 */
public class ComputeFieldStatistics implements BackgroundJob {
        protected final static I18n I18N = I18nFactory.getI18n(ComputeFieldStatistics.class);
        private static final Logger LOGGER = Logger.getLogger(ComputeFieldStatistics.class);
        private Set<Integer> statisticsRowFilter;
        private DataSource ds;
        private int columnId;

        public ComputeFieldStatistics(Set<Integer> statisticsRowFilter, DataSource dataSource, int columnId) {
                this.statisticsRowFilter = statisticsRowFilter;
                this.ds = dataSource;
                this.columnId = columnId;
        }
        
        
        
        @Override
        public void run(ProgressMonitor pm) {
                try {
                        final DataSourceFactory dsf = (Services.getService(DataManager.class)).getDataSourceFactory();
                        Metadata metadata = ds.getMetadata();
                        String fieldName = metadata.getFieldName(columnId);
                        String tableName = ds.getDriverTableName();
                        boolean doRowFiltering = !statisticsRowFilter.isEmpty() && statisticsRowFilter.size() < ds.getRowCount();
                        if(doRowFiltering) {
                                // Create a smaller data source then compute the stats
                                tableName = makeFilteredData(dsf,fieldName);                                
                        }
                        // Compute statistics for the whole table
                        SQLScript s = Engine.loadScript(
                                ComputeFieldStatistics.class.getResourceAsStream("compute_stats.bsql"));
                        s.setDataSourceFactory(dsf);
                        s.setFieldParameter("fieldName", fieldName);
                        s.setTableParameter("tableName", tableName);
                        //Retrieve the first statement
                        SQLStatement statement = s.getStatements()[0];
                        DataSource d = dsf.getDataSource(statement, DataSourceFactory.DEFAULT,pm);
                        //TODO Release filtered column values
                        d.close();
                        if(doRowFiltering) {
                                dsf.getSourceManager().remove(tableName);
                        }
                } catch (DriverException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                } catch (DataSourceCreationException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }  catch (IOException ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }                
        }

        private String makeFilteredData(DataSourceFactory dsf,String fieldName) throws DriverException {

                int fieldIndex = ds.getFieldIndexByName(fieldName);
                int fieldType = ds.getFieldType(fieldIndex).getTypeCode();

                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField(fieldName, fieldType);
                MemoryDataSetDriver driver = new MemoryDataSetDriver(metadata);
                for (Integer rowId : statisticsRowFilter) {
                        driver.addValues(new Value[]{ds.getFieldValue(rowId,
                                        fieldIndex)});
                }                
                return dsf.getDataSource(driver, DriverManager.DEFAULT_SINGLE_TABLE_NAME).getName();
        }        
        
        @Override
        public String getTaskName() {
                return I18N.tr("Compute column statistics.");
        }        
}
