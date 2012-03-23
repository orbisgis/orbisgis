/*
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
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.data;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.gdms.data.schema.Schema;

import org.gdms.driver.ChecksumCalculator;
import org.gdms.driver.DriverException;
import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.orbisgis.progress.NullProgressMonitor;

public abstract class AbstractDataSourceDefinition implements
        DataSourceDefinition {

        private static final Logger LOG = Logger.getLogger(AbstractDataSourceDefinition.class);
        private DataSourceFactory dsf;
        private Driver driver;

        @Override
        public void freeResources(String name) throws DataSourceFinalizationException {
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
                this.dsf = dsf;
        }

        @Override
        public Driver getDriver() {
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

        protected abstract Driver getDriverInstance();

        public void setDriver(Driver driver) {
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
                return new ArrayList<String>(0);
        }

        @Override
        public int getType() {
                return getDriver().getType();
        }

        @Override
        public String getTypeName() {
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
        public String getDriverId() {
                try {
                        return getDriver().getDriverId();
                } catch (DriverLoadException e) {
                        return null;
                }
        }

        @Override
        public void delete() {
        }

        @Override
        public Schema getSchema() throws DriverException {
                return getDriver().getSchema();
        }
        
        @Override
        public void refresh() {
        }
}
