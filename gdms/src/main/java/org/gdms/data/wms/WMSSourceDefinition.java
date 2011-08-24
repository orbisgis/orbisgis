package org.gdms.data.wms;

import org.apache.log4j.Logger;
import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.memory.MemoryDataSourceAdapter;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.Driver;
import org.gdms.driver.DataSet;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.WmsDefinitionType;
import org.orbisgis.progress.ProgressMonitor;

public class WMSSourceDefinition extends AbstractDataSourceDefinition {

        private WMSSource wmsSource;
        private static final Logger LOG = Logger.getLogger(WMSSourceDefinition.class);

        public WMSSourceDefinition(WMSSource wmsSource) {
                LOG.trace("Constructor");
                this.wmsSource = wmsSource;
        }

        @Override
        protected Driver getDriverInstance() {
                try {
                        MemoryDataSetDriver ret = new MemoryDataSetDriver(getWMSMetadata());
                        ret.addValues(new Value[]{
                                        ValueFactory.createValue(wmsSource.getHost()),
                                        ValueFactory.createValue(wmsSource.getLayer()),
                                        ValueFactory.createValue(wmsSource.getSrs()),
                                        ValueFactory.createValue(wmsSource.getFormat())});
                        return ret;
                } catch (DriverException e) {
                        // Access to DefaultMetadata doesn't give any exception
                        throw new DriverLoadException(e);
                }
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
                getDriver().setDataSourceFactory(getDataSourceFactory());

                MemoryDataSourceAdapter ds = new MemoryDataSourceAdapter(
                        getSource(tableName), (MemoryDriver) getDriver());
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
                return wmsSource.getLayer();
        }
}
