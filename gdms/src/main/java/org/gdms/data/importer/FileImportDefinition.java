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
package org.gdms.data.importer;

import java.io.File;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.schema.Schema;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.io.FileImporter;
import org.gdms.driver.io.Importer;

/**
 * A definition for importing the content of a table from a file.
 *
 * @author Antoine Gourlay
 */
public class FileImportDefinition implements ImportSourceDefinition {

        private File file;
        private FileImporter importer;
        private DataSourceFactory dsf;

        /**
         * Creates a new definition for the specified file.
         *
         * @param file an existing file
         */
        public FileImportDefinition(File file) {
                this.file = file;
        }

        private void loadImporter() {
                if (importer == null) {
                        importer = dsf.getSourceManager().getDriverManager().getFileImporter(file);
                }
        }

        @Override
        public int getType() {
                loadImporter();
                return importer.getType();
        }

        @Override
        public String getTypeName() {
                loadImporter();
                return importer.getTypeName();
        }

        @Override
        public String getImporterId() {
                loadImporter();
                return importer.getImporterId();
        }

        @Override
        public Importer getImporter() {
                loadImporter();
                return importer;
        }

        @Override
        public Schema getSchema() throws DriverException {
                loadImporter();
                return importer.getSchema();
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
                this.dsf = dsf;
        }

        @Override
        public DataSourceDefinition importSource(String tableName) throws DriverException {
                // a new file for the converted table
                File newFile = dsf.getResultFile();

                loadImporter();
                // we let the importer write
                importer.open();
                DiskBufferDriver dbuf = new DiskBufferDriver(newFile, importer.getSchema().getTableByName(tableName));
                importer.setDataSourceFactory(dsf);
                importer.convertTable(tableName, dbuf);
                importer.close();
                dbuf.writingFinished();

                // we register the new file
                return new FileSourceDefinition(newFile, tableName);
        }

        @Override
        public DataSourceDefinition[] importAllSources() throws DriverException {

                importer.open();
                importer.setDataSourceFactory(dsf);
                
                final Schema schema = importer.getSchema();
                
                DataSourceDefinition[] sources = new DataSourceDefinition[schema.getTableCount()];
                
                final String[] tableNames = schema.getTableNames();
                for (int i = 0; i < tableNames.length; i++) {
                        String tableName = tableNames[i];
                        File newFile = dsf.getResultFile();
                        DiskBufferDriver dbuf = new DiskBufferDriver(newFile, schema.getTableByName(tableName));
                        importer.convertTable(tableName, dbuf);
                        dbuf.writingFinished();
                        sources[i] = new FileSourceDefinition(file, tableName);
                }
                
                importer.close();

                return sources;
        }
}
