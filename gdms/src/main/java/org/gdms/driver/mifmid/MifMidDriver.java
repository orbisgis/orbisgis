/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.driver.mifmid;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.schema.SchemaMetadata;
import org.gdms.data.types.TypeDefinition;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

public final class MifMidDriver implements FileDriver {

        private static final String NOT_SUPPORTED = "This driver is readonly.";
        private MifMidReader mm;
        private DataSet driver;
        private Schema schema;
        private SchemaMetadata metadata;
        private File file;
        private static final Logger LOG = Logger.getLogger(MifMidDriver.class);

        @Override
        public void close() throws DriverException {
                LOG.trace("Closing");
                mm.close();
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{"mif", "mid"};
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening");
                try {
                        mm = new MifMidReader(file, false, metadata);
                        driver = mm.read().getTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }
        
       @Override
        public int getSupportedType() {
                return SourceManager.FILE | SourceManager.VECTORIAL;
        }

        @Override
        public int getType() {
                return SourceManager.FILE | SourceManager.VECTORIAL;
        }

        @Override
        public String getTypeDescription() {
                return "MIF/MID format";
        }

        @Override
        public String getTypeName() {
                return "MIF";
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getDriverId() {
                return "MIF/MID driver";
        }

        @Override
        public boolean isCommitable() {
                return false;
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                return new TypeDefinition[0];
        }

        @Override
        public String validateMetadata(Metadata metadata) throws DriverException {
                throw new UnsupportedOperationException(NOT_SUPPORTED);
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                if (name.equals(DriverManager.DEFAULT_SINGLE_TABLE_NAME)) {
                        return driver;
                } else {
                        return null;
                }
        }

        @Override
        public void setFile(File file) {
                this.file = file;
                schema = new DefaultSchema("MifMid" + file.getAbsolutePath().hashCode());
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

        @Override
        public boolean isOpen() {
                return driver != null;
        }
}
