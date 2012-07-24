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
package org.gdms.data.stream;

import java.net.URI;

import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.StreamDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.StreamDefinitionType;

/**
 * Definition of stream source.
 *
 * This is in a way the interface between the management of the data itself and
 * their integration into gdms.<br>
 * Here we store the StreamSource to be able to create
 * the StreamDatasource, through the StreamDataSourceAdapter.
 * </p>
 *
 * @author Antoine Gourlay
 * @author Vincent Dépériers
 */
public class StreamSourceDefinition extends AbstractDataSourceDefinition {

        private StreamSource streamSource;

        /**
         * Creates a new definition for a stream.
         * @param streamSource the information on the stream
         */
        public StreamSourceDefinition(StreamSource streamSource) {
                this.streamSource = streamSource;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj instanceof StreamSourceDefinition) {
                        StreamSourceDefinition ssd = (StreamSourceDefinition) obj;
                        return streamSource.equals(ssd.streamSource);
                } else {
                        return false;
                }
        }

        @Override
        public int hashCode() {
                return 48 + streamSource.hashCode();
        }

        /**
         * @return the stream source for the definition
         */
        public StreamSource getStreamSource() {
                return this.streamSource;
        }

        @Override
        protected Driver getDriverInstance() {
                return DriverUtilities.getStreamDriver(getDataSourceFactory().getSourceManager().getDriverManager(), streamSource.getStreamType());
        }

        @Override
        public DataSource createDataSource(String tableName, ProgressMonitor pm) throws DataSourceCreationException {
                try {
                        getDriver().setDataSourceFactory(getDataSourceFactory());

                        StreamDataSourceAdapter sdsa = new StreamDataSourceAdapter(
                                getSource(tableName), streamSource, (StreamDriver) getDriver());
                        sdsa.setDataSourceFactory(getDataSourceFactory());
                        return sdsa;
                } catch (DriverException e) {
                        throw new DataSourceCreationException(e);
                }
        }

        @Override
        public void createDataSource(DataSet contents, ProgressMonitor pm) throws DriverException {
                throw new UnsupportedOperationException("Cannot create stream sources.");
        }

        @Override
        public DefinitionType getDefinition() {
                StreamDefinitionType ret = new StreamDefinitionType();
                ret.setLayerName(streamSource.getLayerName());
                ret.setHost(streamSource.getHost());
                ret.setPort(String.valueOf(streamSource.getPort()));
                ret.setImageFormat(streamSource.getImageFormat());
                ret.setType(streamSource.getStreamType());
                ret.setSRS(streamSource.getSRS());

                return ret;
        }

        @Override
        public String getDriverTableName() {
                return DriverManager.DEFAULT_SINGLE_TABLE_NAME;
        }

        public static DataSourceDefinition createFromXML(StreamDefinitionType definition) {
                StreamSource streamSource = new StreamSource(definition.getHost(), Integer.parseInt(definition.getPort()),
                        definition.getLayerName(), definition.getType(),
                        definition.getImageFormat(), definition.getSRS());

                return new StreamSourceDefinition(streamSource);
        }

        @Override
        public URI getURI() throws DriverException {
                return URI.create(streamSource.getDbms());
        }

        @Override
        public String calculateChecksum(DataSource openDS) throws DriverException {
                return Integer.toString(streamSource.hashCode());
        }
}
