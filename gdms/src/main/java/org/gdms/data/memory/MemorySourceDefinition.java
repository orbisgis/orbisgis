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
package org.gdms.data.memory;

import java.net.URI;
import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.EditableMemoryDriver;
import org.gdms.driver.MemoryDriver;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.ObjectDefinitionType;

/**
 * Definition for sources built on {@link MemoryDriver}.
 *
 * @author fergonco, Antoine Gourlay
 */
public class MemorySourceDefinition extends AbstractDataSourceDefinition {

        private MemoryDriver driver;
        private String tableName;
        private static final Logger LOG = Logger.getLogger(MemorySourceDefinition.class);

        public MemorySourceDefinition(MemoryDriver driver, String tableName) {
                LOG.trace("Constructor");
                this.driver = driver;
                this.tableName = tableName;
                setDriver(driver);
        }

        @Override
        public DataSource createDataSource(String sourceName, ProgressMonitor pm)
                throws DataSourceCreationException {
                LOG.trace("Creating datasource");
                MemoryDataSourceAdapter ds;
                ds = new MemoryDataSourceAdapter(getSource(sourceName), driver);
                LOG.trace("Datasource created");
                return ds;
        }

        @Override
        public void createDataSource(DataSet contents, ProgressMonitor pm) throws DriverException {
                LOG.trace("Writing datasource to object");
                ((EditableMemoryDriver) driver).write(contents, pm);
        }

        @Override
        public DefinitionType getDefinition() {
                ObjectDefinitionType ret = new ObjectDefinitionType();
                ret.setClazz(driver.getClass().getCanonicalName());

                return ret;
        }

        public static DataSourceDefinition createFromXML(ObjectDefinitionType d)
                throws InstantiationException, IllegalAccessException,
                ClassNotFoundException {
                String className = d.getClazz();
                MemoryDriver od = (MemoryDriver) Class.forName(className).newInstance();

                return new MemorySourceDefinition(od, d.getTableName());
        }

        @Override
        protected Driver getDriverInstance() {
                return driver;
        }

        public MemoryDriver getObject() {
                return driver;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof MemorySourceDefinition) {
                        MemorySourceDefinition dsd = (MemorySourceDefinition) obj;
                        return (driver.equals(dsd.driver));
                } else {
                        return false;
                }
        }

        @Override
        public String getDriverTableName() {
                return tableName;
        }

        @Override
        public int hashCode() {
                return 158 * driver.hashCode() + tableName.hashCode();
        }

        /**
         * {@inheritDoc }
         * <p>
         * There is no URI associated with this definition: this method always returns null.
         * </p>
         * @return null
         */
        @Override
        public URI getURI() {
                return null;
        }
}
