/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.memory;

import org.apache.log4j.Logger;
import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.EditableMemoryDriver;
import org.gdms.driver.Driver;
import org.gdms.driver.DataSet;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.ObjectDefinitionType;
import org.orbisgis.progress.ProgressMonitor;

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
}
