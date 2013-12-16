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

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
        /** SQL function to evaluate */
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

        private static String[] computeStatsSQL(DataSource dataSource, String tableName, String columnName, ProgressMonitor pm) throws SQLException {
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
                Statement st = connection.createStatement()) {

                // Cancel select
                PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
                pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                        listener);
                try(ResultSet rs = st.executeQuery(String.format("SELECT %s FROM %s",sb.toString(), tableName ))) {
                    if(rs.next()) {
                        for(STATS func : STATS.values()) {
                            stats[func.ordinal()] = rs.getString(func.name());
                        }
                    }
                } finally {
                    pm.removePropertyChangeListener(listener);
                }
            }
            return stats;
        }

        private static String[] computeStatsLocal(DataSource dataSource, String tableName, String columnName, SortedSet<Integer> rowNum, ProgressMonitor pm) throws SQLException {
            String[] res = new String[STATS.values().length];
            SummaryStatistics stats = new SummaryStatistics();
            try(Connection connection = dataSource.getConnection();
                Statement st = connection.createStatement()) {
                // Cancel select
                PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
                pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                        listener);
                try (ResultSet rs = st.executeQuery(String.format("SELECT %s FROM %s",columnName, tableName ))) {
                    Iterator<Integer> it = rowNum.iterator();
                    Integer fetch = null;
                    if(it.hasNext()) {
                        fetch = it.next();
                    }
                    ProgressMonitor fetchProgress = pm.startTask(rowNum.size());
                    while(rs.next() && fetch != null && !pm.isCancelled()) {
                        if(fetch.equals(rs.getRow())) {
                            stats.addValue(rs.getDouble(columnName));
                            fetch = it.next();
                            fetchProgress.endTask();
                        }
                    }
                } finally {
                    pm.removePropertyChangeListener(listener);
                }
            }
            res[STATS.SUM.ordinal()] = Double.valueOf(stats.getSum()).toString();
            res[STATS.AVG.ordinal()] = Double.valueOf(stats.getMean()).toString();
            res[STATS.COUNT.ordinal()] = Long.valueOf(stats.getN()).toString();
            res[STATS.MIN.ordinal()] = Double.valueOf(stats.getMin()).toString();
            res[STATS.MAX.ordinal()] = Double.valueOf(stats.getMax()).toString();
            res[STATS.STDDEV_SAMP.ordinal()] = Double.valueOf(stats.getStandardDeviation()).toString();
            return res;
        }

    @Override
    public void run(ProgressMonitor pm) {
        try {
            String fieldName;
            try (Connection connection = ds.getConnection()) {
                fieldName = JDBCUtilities.getFieldName(connection.getMetaData(), table, columnId);
            }
            boolean doRowFiltering = !statisticsRowFilter.isEmpty();
            String[] stats;
            if (doRowFiltering) {
                SortedSet<Integer> sortedSet;
                if (statisticsRowFilter instanceof SortedSet) {
                    sortedSet = (SortedSet<Integer>) statisticsRowFilter;
                } else {
                    sortedSet = new IntegerUnion(statisticsRowFilter);
                }
                stats = computeStatsLocal(ds, table, fieldName, sortedSet, pm);
            } else {
                stats = computeStatsSQL(ds, table, fieldName, pm);
            }
            // Show table statistics
            StringBuilder message = new StringBuilder();
            message.append(I18N.tr("\nTable {0}, statistics of the column {1}.\n", table, fieldName));
            message.append(I18N.tr("Row count : {0}\n", stats[STATS.COUNT.ordinal()]));
            message.append(I18N.tr("Minimum : {0}\n", stats[STATS.MIN.ordinal()]));
            message.append(I18N.tr("Maximum : {0}\n", stats[STATS.MAX.ordinal()]));
            message.append(I18N.tr("Sum : {0}\n", stats[STATS.SUM.ordinal()]));
            message.append(I18N.tr("Average : {0}\n", stats[STATS.AVG.ordinal()]));
            message.append(I18N.tr("Standard deviation : {0}\n", stats[STATS.STDDEV_SAMP.ordinal()]));
            LOGGER.info(message.toString());
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }
        
        @Override
        public String getTaskName() {
                return I18N.tr("Compute column statistics.");
        }        
}
