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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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

import org.gdms.data.AbstractDataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;

/**
 * Used to create Object sources from an existing MemoryDriver.
 * @author Antoine Gourlay
 */
public class MemorySourceCreation extends AbstractDataSourceCreation {
        
        private MemoryDriver obj;

        /**
         * Builds a new MemorySourceCreation from an existing MemoryDriver.
         * @param obj 
         */
        public MemorySourceCreation(MemoryDriver obj) {
                this.obj = obj;
        }

        @Override
        protected Driver getDriverInstance() {
                return obj;
        }

        @Override
        public DataSourceDefinition create(String tableName) throws DriverException {
                return new MemorySourceDefinition(obj, tableName);
        }

        @Override
        public String[] getAvailableTables() throws DriverException {
                return obj.getSchema().getTableNames();
        }
        
        
        
}
