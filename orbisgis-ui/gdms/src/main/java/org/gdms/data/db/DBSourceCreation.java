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
package org.gdms.data.db;

import org.gdms.data.AbstractDataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.schema.Metadata;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;

public class DBSourceCreation extends AbstractDataSourceCreation {

        private DBSource source;
        private Metadata metadata;

        /**
         * Builds a new DBSourceCreation
         *
         * @param source
         *            information about the table to be created
         * @param dmd
         *            Information about the schema of the new source. If null the source is supposed to
         *            exist already.
         */
        public DBSourceCreation(DBSource source, Metadata dmd) {
                this.source = source;
                this.metadata = dmd;
        }

        @Override
        public DataSourceDefinition create(String tableName) throws DriverException {
                // DB Table Definition only carry one table, so the tableName parameter
                // is useless here. Maybe we need a DB Definition...

                ((DBReadWriteDriver) getDriver()).setDataSourceFactory(getDataSourceFactory());
                if (metadata != null) {
                        ((DBReadWriteDriver) getDriver()).createSource(source, metadata);
                }

                return new DBTableSourceDefinition(source);
        }

        @Override
        public String[] getAvailableTables() throws DriverException {
                return new String[]{source.getTableName()};
        }

        @Override
        protected DBReadWriteDriver getDriverInstance() {
                return (DBReadWriteDriver) DriverUtilities.getDriver(
                        getDataSourceFactory().getSourceManager().getDriverManager(),
                        source.getPrefix());
        }

        public String getPrefix() {
                return source.getPrefix();
        }
}