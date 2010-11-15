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

import java.util.List;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.RowMappedDriver;
import org.gdms.sql.strategies.SQLProcessor;

/**
 *
 * @author Antoine Gourlay
 */
public class FilterDataSourceDecorator extends AbstractDataSourceDecorator {

    private RowMappedDriver mapDriver;
    private String filter;

    public FilterDataSourceDecorator(DataSource internalDataSource) throws DriverException {
        this(internalDataSource, null);
    }

    public FilterDataSourceDecorator(DataSource internalDataSource, String filter) throws DriverException {
        super(internalDataSource);
        this.filter = filter;
    }

    @Override
    public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
        if (mapDriver == null) {
            mapDriver = getMapDriver();
        }
        return mapDriver.getFieldValue(rowIndex, fieldId);
    }

    /**
     * Returns the index in the original datasource (unfiltered). This index
     * is kept in-memory only, it is NOT written to disk.
     * {@inheritDoc}
     */
    public long getOriginalIndex(long rowIndex) throws DriverException {
        if (mapDriver == null) {
            mapDriver = getMapDriver();
        }
        return mapDriver.getOriginalIndex(rowIndex);
    }

    @Override
    public long getRowCount() throws DriverException {
        if (mapDriver == null) {
            mapDriver = getMapDriver();
        }
        return mapDriver.getRowCount();
    }

    @Override
    public void open() throws DriverException {
        if (mapDriver == null) {
            mapDriver = getMapDriver();
        }
    }

    @Override
    public void close() throws DriverException, AlreadyClosedException {
        
    }



    /**
     * Returns the Drive used by the SelectionOp when processing the query
     * @return the driver
     * @throws DriverException
     */
    private RowMappedDriver getMapDriver() throws DriverException {
        if (getFilter() == null || getFilter().isEmpty()) {
            throw new NullPointerException("The filter condition cannot be null or empty.");
        }
        String rq = "SELECT * FROM " + getDataSource().getName() + " WHERE " + getFilter() + ";";
        SQLProcessor p = new SQLProcessor(getDataSourceFactory());
        try {
            return (RowMappedDriver) p.execute(rq, null);
        } catch (Exception ex) {
            throw new DriverException(ex);
        }
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
    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<Integer> getIndexMap() {
        return mapDriver.getIndexMap();
    }
}
