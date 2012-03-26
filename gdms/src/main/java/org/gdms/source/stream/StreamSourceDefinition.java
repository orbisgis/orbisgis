package org.gdms.source.stream;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.memory.MemoryDataSourceAdapter;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.source.directory.DefinitionType;
import org.orbisgis.progress.ProgressMonitor;
import org.apache.log4j.Logger;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.wms.WMSSourceDefinition;
import org.gdms.driver.*;
import org.gdms.driver.driverManager.DriverManager;

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
        LOG.trace("Creating datasource");
        getDriver().setDataSourceFactory(getDataSourceFactory());

        //TO DO Maybe create a StreamDataSourceAdaptater ?
        MemoryDataSourceAdapter ds = new MemoryDataSourceAdapter(
                getSource(tableName), (MemoryDriver) getDriver());
        LOG.trace("Datasource created");
        return ds;
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
}
