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

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.orbisgis.progress.NullProgressMonitor;

import org.gdms.data.schema.Schema;
import org.gdms.driver.ChecksumCalculator;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;

public abstract class AbstractDataSourceDefinition<D extends Driver> implements
        DataSourceDefinition<D> {

        private static final Logger LOG = Logger.getLogger(AbstractDataSourceDefinition.class);
        private DataSourceFactory dsf;
        private D driver;

        @Override
        public void freeResources(String name) throws DataSourceFinalizationException {
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
                this.dsf = dsf;
        }

        @Override
        public D getDriver() throws DriverException {
                if (driver == null) {
                        driver = getDriverInstance();
                }

                return driver;
        }

        /**
         * Return true if this definition represents the same source as the
         * specified one
         *
         * @param dsd
         * @return
         */
        @Override
        public abstract boolean equals(Object dsd);

        @Override
        public abstract int hashCode();

        protected abstract D getDriverInstance() throws DriverException;

        public void setDriver(D driver) {
                this.driver = driver;
        }

        public DataSourceFactory getDataSourceFactory() {
                return dsf;
        }

        protected Source getSource(String name) {
                return getDataSourceFactory().getSourceManager().getSource(name);
        }

        @Override
        public String calculateChecksum(DataSource openDS) throws DriverException {
                if (driver instanceof ChecksumCalculator) {
                        return ((ChecksumCalculator) driver).getChecksum();
                } else {
                        try {
                                DataSource ds = openDS;
                                if (ds == null) {
                                        ds = createDataSource("any", new NullProgressMonitor());
                                }
                                ds.setDataSourceFactory(dsf);
                                ds.open();
                                String ret = DigestUtilities.getBase64Digest(ds);
                                ds.close();
                                return ret;
                        } catch (NoSuchAlgorithmException e) {
                                throw new DriverException(e);
                        } catch (DataSourceCreationException e) {
                                throw new DriverException(e);
                        }
                }
        }

        @Override
        public List<String> getSourceDependencies() throws DriverException {
                return Collections.emptyList();
        }

        @Override
        public int getType() throws DriverException {
                return getDriver().getType();
        }

        @Override
        public String getTypeName() throws DriverException {
                try {
                        return getDriver().getTypeName();
                } catch (DriverLoadException e) {
                        LOG.warn("Error getting type name. Returning Unknown.", e);
                        return "Unknown";
                }
        }

        @Override
        public void initialize() throws DriverException {
        }

        @Override
        public String getDriverId() throws DriverException {
                try {
                        return getDriver().getDriverId();
                } catch (DriverLoadException e) {
                        return null;
                }
        }

        @Override
        public void delete() throws DriverException {
        }

        @Override
        public Schema getSchema() throws DriverException {
                return getDriver().getSchema();
        }
        
        @Override
        public void refresh() {
        }
}
