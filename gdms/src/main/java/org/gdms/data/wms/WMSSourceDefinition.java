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
package org.gdms.data.wms;

import java.net.URI;

import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.memory.MemoryDataSourceAdapter;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.WmsDefinitionType;

public class WMSSourceDefinition extends AbstractDataSourceDefinition<MemoryDriver> {

        private WMSSource wmsSource;
        private static final Logger LOG = Logger.getLogger(WMSSourceDefinition.class);

        public WMSSourceDefinition(WMSSource wmsSource) {
                LOG.trace("Constructor");
                this.wmsSource = wmsSource;
        }

        @Override
        protected MemoryDriver getDriverInstance() throws DriverException {
                MemoryDataSetDriver ret = new MemoryDataSetDriver(getWMSMetadata());
                ret.addValues(new Value[]{
                                ValueFactory.createValue(wmsSource.getHost()),
                                ValueFactory.createValue(wmsSource.getLayer()),
                                ValueFactory.createValue(wmsSource.getSrs()),
                                ValueFactory.createValue(wmsSource.getFormat())});
                return ret;
        }

        private Metadata getWMSMetadata() throws DriverException {
                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("host", Type.STRING);
                metadata.addField("layer", Type.STRING);
                metadata.addField("srs", Type.STRING);
                metadata.addField("format", Type.STRING);
                return metadata;
        }

        @Override
        public DataSource createDataSource(String tableName, ProgressMonitor pm)
                throws DataSourceCreationException {
                LOG.trace("Creating datasource");

                final MemoryDriver driver;
                try {
                        driver = getDriver();
                } catch (DriverException ex) {
                        throw new DataSourceCreationException(ex);
                }

                driver.setDataSourceFactory(getDataSourceFactory());

                MemoryDataSourceAdapter ds = new MemoryDataSourceAdapter(
                        getSource(tableName), driver);
                LOG.trace("Datasource created");
                return ds;
        }

        @Override
        public void createDataSource(DataSet contents, ProgressMonitor pm)
                throws DriverException {
                throw new UnsupportedOperationException("Cannot create WMS sources");
        }

        @Override
        public int getType() {
                return SourceManager.WMS;
        }

        @Override
        public String getTypeName() {
                return "WMS";
        }

        public WMSSource getWMSSource() {
                return wmsSource;
        }

        @Override
        public DefinitionType getDefinition() {
                WmsDefinitionType def = new WmsDefinitionType();
                def.setHost(wmsSource.getHost());
                def.setLayerName(wmsSource.getLayer());
                def.setSrs(wmsSource.getSrs());
                def.setFormat(wmsSource.getFormat());
                return def;
        }

        public static DataSourceDefinition createFromXML(
                WmsDefinitionType definitionType) {
                WMSSource wmsSource = new WMSSource(definitionType.getHost(),
                        definitionType.getLayerName(), definitionType.getSrs(),
                        definitionType.getFormat());
                return new WMSSourceDefinition(wmsSource);
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof WMSSourceDefinition) {
                        WMSSourceDefinition dsd = (WMSSourceDefinition) obj;
                        return (wmsSource.getHost().equals(dsd.getWMSSource().getHost())
                                && wmsSource.getLayer().equals(
                                dsd.getWMSSource().getLayer())
                                && wmsSource.getSrs().equals(dsd.getWMSSource().getSrs())
                                && wmsSource.getFormat().equals(
                                dsd.getWMSSource().getFormat()));
                } else {
                        return false;
                }
        }

        @Override
        public int hashCode() {
                return 48 + wmsSource.hashCode();
        }

        @Override
        public String getDriverTableName() {
                return DriverManager.DEFAULT_SINGLE_TABLE_NAME;
        }

        @Override
        public URI getURI() throws DriverException {
                return URI.create(wmsSource.getHost());
        }
}
