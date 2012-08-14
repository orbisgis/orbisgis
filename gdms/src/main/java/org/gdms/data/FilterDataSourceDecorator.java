/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.source.SourceManager;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SQLStatement;

/**
 * This Decorator can filter a underlying DataSource with a SQL Expression.
 *
 * <p>
 * It does not produce a new DataSource, but rather allows access through getFieldValue()
 * to the filtered results.
 * This class does not replicate the data, it simply builds an list of rowIndexes from
 * the original DataSource that match the Expression, and use it to return the data
 * from the real DataSource. The list of indexes is stored in memory only.
 * </p><p>
 * The filter property has to be set before use. An optional order property is available:
 * if present the results are ordered according to it. The order supports everything that
 * can be specified in an SQL ORDER BY clause.
 * </p><p>
 * It is not necessary to call the open and close methods to access methods that do not
 * read any data in the original DataSource : getOriginalIndex, getRowCount and getIndexMap
 * can be called without the DataSourceDecorator or the actual DataSource being opened.
 * The first call to any of these will trigger the filtering.
 * <p/>
 * @author Antoine Gourlay
 */
public final class FilterDataSourceDecorator extends AbstractDataSourceDecorator {

        private List<Integer> map;
        private String filter;
        private String order;

        /**
         * Creates a new <tt>FilterDataSourceDecorator</tt> over the specified source.
         * <p>
         * A filter has to be specified with {@link #setFilter(java.lang.String) } before
         * use.
         * </p>
         *
         * @param internalDataSource a data source
         */
        public FilterDataSourceDecorator(DataSource internalDataSource) {
                this(internalDataSource, null);
        }

        /**
         * Creates a new <tt>FilterDataSourceDecorator</tt> over the specified source, with
         * the specified filter.
         *
         * @param internalDataSource a data source
         * @param filter an initial filter
         */
        public FilterDataSourceDecorator(DataSource internalDataSource, String filter) {
                super(internalDataSource);
                setFilter(filter);
                if (internalDataSource.isEditable()) {
                        internalDataSource.addEditionListener(new FilterDataSourceDecoratorEditionListener());
                }
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                if (map == null && super.isOpen()) {
                        map = getMapDriver();
                }
                return super.getFieldValue(map.get((int) rowIndex), fieldId);
        }

        /**
         * Returns the index in the original data source (unfiltered). This index
         * is kept in memory only, it is NOT written to disk.
         *
         * @param rowIndex a row in this (filtered) data source
         * @return the original index of the row
         * @throws DriverException if there is a problem building the filter
         */
        public long getOriginalIndex(long rowIndex) throws DriverException {
                if (map == null) {
                        map = getMapDriver();
                }
                return map.get((int) rowIndex);
        }

        @Override
        public long getRowCount() throws DriverException {
                if (map == null) {
                        map = getMapDriver();
                }
                return map.size();
        }

        @Override
        public void open() throws DriverException {
                getDataSource().open();
                if (map == null) {
                        map = getMapDriver();
                }
        }

        @Override
        public void close() throws DriverException {
                getDataSource().close();
        }

        /**
         * Returns the Driver used by the SelectionOp when processing the query
         *
         * @return the driver
         * @throws DriverException
         */
        private List<Integer> getMapDriver() throws DriverException {
                List<Integer> ints = new ArrayList<Integer>();
                if (filter == null || filter.isEmpty()) {
                        throw new IllegalArgumentException("The filter condition cannot be null or empty.");
                }

                boolean opened = false;
                if (!getDataSource().isOpen()) {
                        getDataSource().open();
                        opened = true;
                }

                SourceManager sm = getDataSourceFactory().getSourceManager();
                MemoryDataSetDriver d = new MemoryDataSetDriver(getDataSource(), true);
                final String uID = sm.nameAndRegister(d, DriverManager.DEFAULT_SINGLE_TABLE_NAME);

                StringBuilder sb = new StringBuilder();
                sb.append("SELECT oid FROM ").append(uID).append(" WHERE ");
                sb.append(filter);
                if (order != null && !order.isEmpty()) {
                        sb.append(" ORDER BY ").append(order);
                }
                sb.append(';');

                SQLStatement s = null;
                try {
                        s = Engine.parse(sb.toString(), getDataSourceFactory().getProperties());
                } catch (ParseException ex) {
                        throw new DriverException(ex);
                }

                s.setDataSourceFactory(getDataSourceFactory());
                s.prepare();
                DataSet dset = s.execute();

                for (int i = 0; i < dset.getRowCount(); i++) {
                        ints.add(dset.getFieldValue(i, 0).getAsInt());
                }

                s.cleanUp();

                if (opened) {
                        getDataSource().close();
                }

                sm.remove(uID);

                return ints;
        }

        /**
         * @return the filter
         */
        public String getFilter() {
                return filter;
        }

        /**
         * Sets a new value for the filter.
         * <p>
         * This method always invalidate the current filter if there is one.
         * </p>
         *
         * @param filter the filter to set
         */
        public final void setFilter(String filter) {
                this.filter = filter == null ? "" : filter;
                map = null;
        }

        /**
         * @return the current ordering
         */
        public String getOrder() {
                return order;
        }

        /**
         * Sets the current ordering (in SQL syntax).
         *
         * @param order an ordering string or null/empty string for no ordering
         */
        public void setOrder(String order) {
                this.order = order == null ? "" : order;
                map = null;
        }

        /**
         * Returns the readonly list of rowIndexes from the original DataSource
         * that correspond to the filtered results
         *
         * @return
         * @throws DriverException
         */
        public List<Integer> getIndexMap() throws DriverException {
                if (map == null) {
                        map = getMapDriver();
                }
                return Collections.unmodifiableList(map);
        }

        private class FilterDataSourceDecoratorEditionListener implements EditionListener {

                @Override
                public void singleModification(EditionEvent e) {
                        if (e.getType() == EditionEvent.DELETE) {
                                map.remove((int) e.getRowIndex());
                        } else {
                                map = null;
                        }
                }

                @Override
                public void multipleModification(MultipleEditionEvent e) {
                        for (int i = 0; i < e.getEvents().length; i++) {
                                if (map == null) {
                                        break;
                                }
                                singleModification(e.getEvents()[i]);
                        }
                }
        }
}
