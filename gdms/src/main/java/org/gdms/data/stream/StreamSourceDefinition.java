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

import org.apache.log4j.Logger;
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
 * Here we will store the StreamSource to give it as a parameter when we create
 * the StreamDatasource, through the StreamDataSourceAdapter.
 * </p>
 *
 * @author Vincent Dépériers
 */
public class StreamSourceDefinition extends AbstractDataSourceDefinition {

        private StreamSource m_streamSource;
        private static final Logger LOG = Logger.getLogger(StreamSourceDefinition.class);

        public StreamSourceDefinition(StreamSource streamSource) {
                LOG.trace("Constructor");
                this.m_streamSource = streamSource;
        }

        /**
         *
         * @param obj
         * @return true if the object is an instance of StreamSourceDifinition
         * having the same attributes values as the m_StreamSource
         */
        @Override
        public boolean equals(Object obj) {
                if (obj instanceof StreamSourceDefinition) {
                        StreamSourceDefinition ssd = (StreamSourceDefinition) obj;
                        return (equals(ssd.m_streamSource.getDbms(), m_streamSource.getDbms())
                                && equals(ssd.m_streamSource.getLayerName(), m_streamSource.getLayerName())
                                && equals(ssd.m_streamSource.getHost(), m_streamSource.getHost())
                                && equals(ssd.m_streamSource.getPassword(), m_streamSource.getPassword())
                                && (ssd.m_streamSource.getPort() == m_streamSource.getPort())
                                && equals(ssd.m_streamSource.getUser(), m_streamSource.getUser())
                                && equals(ssd.m_streamSource.getPrefix(), m_streamSource.getPrefix()));
                } else {
                        return false;
                }
        }

        private boolean equals(String str, String str2) {
                if (str == null) {
                        return str2 == null;
                } else {
                        return str.equals(str2);
                }
        }

        @Override
        public int hashCode() {
                return 48 + m_streamSource.hashCode();
        }

        public StreamSource getStreamSource() {
                return this.m_streamSource;
        }

        @Override
        protected Driver getDriverInstance() {
                return DriverUtilities.getStreamDriver(getDataSourceFactory().getSourceManager().getDriverManager(), m_streamSource.getPrefix());
        }

        @Override
        public DataSource createDataSource(String tableName, ProgressMonitor pm) throws DataSourceCreationException {
                try {
                        getDriver().setDataSourceFactory(getDataSourceFactory());

                        StreamDataSourceAdapter sdsa = new StreamDataSourceAdapter(
                                getSource(tableName), m_streamSource, (StreamDriver) getDriver());
                        sdsa.setDataSourceFactory(getDataSourceFactory());
                        return sdsa;
                } catch (DriverException e) {
                        throw new DataSourceCreationException(e);
                }
        }

        @Override
        public void createDataSource(DataSet contents, ProgressMonitor pm) throws DriverException {
                throw new UnsupportedOperationException("Cannot create stream sources");
        }

        @Override
        public DefinitionType getDefinition() {
                StreamDefinitionType ret = new StreamDefinitionType();
                ret.setLayerName(m_streamSource.getLayerName());
                ret.setHost(m_streamSource.getHost());
                ret.setImageFormat(m_streamSource.getImageFormat());
                ret.setSRS(m_streamSource.getSRS());

                return ret;
        }

        @Override
        public String getDriverTableName() {
                return DriverManager.DEFAULT_SINGLE_TABLE_NAME;
        }

        public StreamSource getSourceDefinition() {
                return this.m_streamSource;
        }
        
        public static DataSourceDefinition createFromXML(StreamDefinitionType definition) {
                StreamSource streamSource = new StreamSource(definition.getHost(), Integer.parseInt(definition.getPort()),
                        definition.getLayerName(), definition.getPrefix(),
                        definition.getImageFormat(), definition.getSRS(),
                        definition.getUser(), definition.getPassword());

                return new StreamSourceDefinition(streamSource);
        }

        @Override
        public URI getURI() throws DriverException {
                return URI.create(m_streamSource.getDbms());
        }
}
