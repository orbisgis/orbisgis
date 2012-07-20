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
import org.gdms.data.memory.MemoryDataSourceAdapter;
import org.gdms.data.wms.WMSSourceDefinition;
import org.gdms.driver.*;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.StreamDefinitionType;

/**
 *
 * @author Vincent Dépériers
 */
public class StreamSourceDefinition extends AbstractDataSourceDefinition {

    private StreamSource m_streamSource;
    private static final Logger LOG = Logger.getLogger(WMSSourceDefinition.class);

    public StreamSourceDefinition(StreamSource streamSource) {
        LOG.trace("Constructor");
        this.m_streamSource = streamSource;
    }

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
                    && equals(ssd.m_streamSource.getTarget(), m_streamSource.getTarget())
                    && equals(ssd.m_streamSource.getSchemaName(), m_streamSource.getSchemaName())
                    && equals(ssd.m_streamSource.getPrefix(), m_streamSource.getPrefix()));
        } else {
            return false;
        }
    }

    private boolean equals(String str, String str2) {
        if (str == null) {
            return true;
        } else {
            return str.equals(str2);
        }
    }

    /**
     * Not supported yet...
     *
     * @return
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Driver getDriverInstance() {
        return DriverUtilities.getStreamDriver(getDataSourceFactory().getSourceManager().getDriverManager(), m_streamSource.getPrefix());
    }

    @Override
    public DataSource createDataSource(String tableName, ProgressMonitor pm) throws DataSourceCreationException {
            try {
                    getDriver().setDataSourceFactory(getDataSourceFactory());

                    //TO DO Maybe create a StreamDataSourceAdaptater ?
                    MemoryDataSourceAdapter ds = new MemoryDataSourceAdapter(
                            getSource(tableName), (MemoryDriver) getDriver());
                    return ds;
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
        ret.setPort(Integer.toString(m_streamSource.getPort()));
        ret.setTarget(m_streamSource.getTarget());
        ret.setPassword(m_streamSource.getPassword());
        ret.setUser(m_streamSource.getUser());
        ret.setPrefix(m_streamSource.getPrefix());
        ret.setSchemaName(m_streamSource.getSchemaName());

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
        StreamSource streamSource = new StreamSource(definition.getHost(), Integer.parseInt(definition.getPort()), definition.getTarget(),
                definition.getLayerName(), definition.getUser(), definition.getPassword(), definition.getSchemaName(), definition.getPrefix());

        return new StreamSourceDefinition(streamSource);
    }

        @Override
        public URI getURI() throws DriverException {
                return URI.create(m_streamSource.getDbms());
        }
}
