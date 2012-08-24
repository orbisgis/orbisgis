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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.SQLScript;
import org.gdms.sql.engine.SQLStatement;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Nicolas Fortin
 */
public class ComputeFieldStatistics implements BackgroundJob {
        protected final static I18n I18N = I18nFactory.getI18n(ComputeFieldStatistics.class);
        private static final Logger LOGGER = Logger.getLogger("gui."+ComputeFieldStatistics.class);
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
                        String tableName = ds.getName();
                        long rowCount = ds.getRowCount();
                        boolean doRowFiltering = !statisticsRowFilter.isEmpty() && statisticsRowFilter.size() < ds.getRowCount();
                        if(doRowFiltering) {
                                // Create a smaller data source then compute the stats
                                rowCount = statisticsRowFilter.size();
                                tableName = makeFilteredData(dsf,fieldName);                                
                        }
                        // Compute statistics for the whole table
                        SQLScript s = Engine.loadScript(
                                ComputeFieldStatistics.class.getResourceAsStream("compute_stats.bsql"));
                        LOGGER.debug("Set Table name : "+tableName);
                        //Retrieve the first statement
                        SQLStatement statement = s.getStatements()[0];
                        statement.setFieldParameter("fieldName", fieldName);
                        statement.setTableParameter("tableName", tableName);
                        statement.setDataSourceFactory(dsf);
                        statement.prepare();
                        DataSet dataSet = statement.execute();
                        //Read statistics
                        Value[] row = dataSet.getRow(0);
                        String[] rowLabels = dataSet.getMetadata().getFieldNames();
                        Map<String,Value> values = new HashMap<String,Value>();
                        for(int col=0;col<row.length;col++) {
                                values.put(rowLabels[col],row[col]);
                        }
                        // Show table statistics
                        StringBuilder message = new StringBuilder();
                        message.append(I18N.tr("Table {0}, statistics of the column {1}.\n",ds.getName(),fieldName));
                        message.append(I18N.tr("Row count : {0}\n",rowCount));
                        message.append(I18N.tr("Minimum : {0}\n",values.get("min")));
                        message.append(I18N.tr("Maximum : {0}\n",values.get("max")));
                        message.append(I18N.tr("Sum : {0}\n",values.get("sum")));
                        message.append(I18N.tr("Average : {0}\n",values.get("avg")));
                        message.append(I18N.tr("Standart deviation : {0}\n",values.get("std")));
                        LOGGER.info(message.toString());                        
                        //Free temporary tables
                        if(doRowFiltering) {
                                dsf.getSourceManager().remove(tableName);
                        }
                        statement.cleanUp();
                } catch (DriverException ex) {
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
