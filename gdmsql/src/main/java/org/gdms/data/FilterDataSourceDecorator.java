/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 *
 * or contact directly:
 * info@orbisgis.org
 **/
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
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SQLEngine;
import org.gdms.sql.engine.SqlStatement;

/**
 * This Decorator can filter a underlying DataSource with a SQL Expression.
 *
 * It does not produce a new DataSource, but rather allows access through getFieldValue()
 * to the filtered results.
 * This class does not replicate the data, it simply builds an list of rowIndexes from
 * the original DataSource that match the Expression, and use it to return the data
 * from the real DataSource. The list of indexes is stored in memory only.
 *
 * It is not necessary to call the open and close methods to access methods that do not
 * read any data in the original DataSource : getOriginalIndex, getRowCount and getIndexMap
 * can be called without the DataSourceDecorator or the actual DataSource being opened.
 * The first call to any of these will trigger the filtering.
 * 
 * @author Antoine Gourlay
 */
public class FilterDataSourceDecorator extends AbstractDataSourceDecorator {

        private List<Integer> map;
        private String filter;

        public FilterDataSourceDecorator(DataSource internalDataSource) throws DriverException {
                this(internalDataSource, null);
        }

        public FilterDataSourceDecorator(DataSource internalDataSource, String filter) throws DriverException {
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
         * Returns the index in the original datasource (unfiltered). This index
         * is kept in memory only, it is NOT written to disk.
         * {@inheritDoc}
         * @param rowIndex
         * @return
         * @throws DriverException
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
         * @return the driver
         * @throws DriverException
         */
        private List<Integer> getMapDriver() throws DriverException {
                List<Integer> ints = new ArrayList<Integer>();
                if (getFilter() == null || getFilter().isEmpty()) {
                        throw new IllegalArgumentException("The filter condition cannot be null or empty.");
                }
                
                SourceManager sm = getDataSourceFactory().getSourceManager();
                MemoryDataSetDriver d = new MemoryDataSetDriver(getDataSource(), true);
                final String uID = sm.nameAndRegister(d, DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                
                String rq = "SELECT oid FROM " + uID + " WHERE " + filter + ";";
                SQLEngine p = getDataSourceFactory().getSqlEngine();
                SqlStatement s = null;
                try {
                        s = p.parse(rq)[0];
                } catch (ParseException ex) {
                        throw new DriverException(ex);
                }
                
                s.prepare(getDataSourceFactory());
                DataSet dset = s.execute();
                
                for (int i = 0; i < dset.getRowCount(); i++) {
                        ints.add(dset.getFieldValue(i, 0).getAsInt());
                }
                
                s.cleanUp();
                
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
         * @param filter the filter to set
         */
        public final void setFilter(String filter) {
                this.filter = filter == null ? "" : filter;
                map = null;
        }

        /**
         * Returns the readonly list of rowIndexes from the original DataSource
         * that correspond to the filtered results
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

        @Override
        public SQLDataSourceFactory getDataSourceFactory() {
                return (SQLDataSourceFactory) super.getDataSourceFactory();
        }
}
