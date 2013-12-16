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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;

/**
 * This job compute a numeric column statistics.
 * @author Nicolas Fortin
 */
public class ComputeFieldStatistics implements BackgroundJob {
        protected final static I18n I18N = I18nFactory.getI18n(ComputeFieldStatistics.class);
        private static final Logger LOGGER = Logger.getLogger("gui."+ComputeFieldStatistics.class);
        private Set<Integer> statisticsRowFilter;
        private DataSource ds;
        private int columnId;
        private String table;
        private enum STATS { COUNT, SUM, AVG, STDDEV_SAMP, MIN, MAX}

        /**
         * Constructor
         * @param statisticsRowFilter Row id filter (not primary key)
         * @param dataSource JDBC Datasource
         * @param columnId Column Id [0-n]
         * @param tableName Table identifier
         */
        public ComputeFieldStatistics(Set<Integer> statisticsRowFilter, DataSource dataSource, int columnId, String tableName) {
                this.statisticsRowFilter = statisticsRowFilter;
                this.ds = dataSource;
                this.columnId = columnId;
                table = tableName;
        }
        
        private static String[] computeStatsSQL(DataSource dataSource, String tableName, String columnName) throws SQLException {
            String[] stats = new String[STATS.values().length];
            StringBuilder sb = new StringBuilder();
            for(STATS func : STATS.values()) {
                if(sb.length()!=0) {
                    sb.append(", ");
                }
                sb.append(func.name());
                sb.append("(");
                sb.append(columnName);
                sb.append(") ");
                sb.append(func.name());
            }
            try(Connection connection = dataSource.getConnection();
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(String.format("SELECT %s FROM %s",sb.toString(), tableName ))) {
                if(rs.next()) {
                    for(STATS func : STATS.values()) {
                        stats[func.ordinal()] = rs.getString(func.name());
                    }
                }
            }
            return stats;
        }

        private static String[] computeStatsLocal(DataSource dataSource, String tableName, String columnName, SortedSet<Integer> rowNum) throws SQLException {
            String[] res = new String[STATS.values().length];
            SummaryStatistics stats = new SummaryStatistics();

            return res;
        }
        
        @Override
        public void run(ProgressMonitor pm) {
                try {
                        final DataSourceFactory dsf = ds.getDataSourceFactory();
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
                        message.append(I18N.tr("\nTable {0}, statistics of the column {1}.\n",ds.getName(),fieldName));
                        message.append(I18N.tr("Row count : {0}\n",rowCount));
                        message.append(I18N.tr("Minimum : {0}\n",values.get("min")));
                        message.append(I18N.tr("Maximum : {0}\n",values.get("max")));
                        message.append(I18N.tr("Sum : {0}\n",values.get("sum")));
                        message.append(I18N.tr("Average : {0}\n",values.get("avg")));
                        message.append(I18N.tr("Standard deviation : {0}\n",values.get("std")));
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
        
        @Override
        public String getTaskName() {
                return I18N.tr("Compute column statistics.");
        }        
}
