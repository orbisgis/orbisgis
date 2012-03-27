package org.gdms.driver.stream;

import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.stream.StreamSource;
import org.gdms.data.types.Type;

import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.StreamReadWriteDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.ProgressMonitor;

/**
 *
 * @author Vincent Dépériers
 */
public final class SimpleWMSDriver extends AbstractDataSet implements StreamReadWriteDriver {

    public static final String DRIVER_NAME = "Simple WMS driver";
    private static final Logger LOG = Logger.getLogger(SimpleWMSDriver.class);
    private String m_url;
    private StreamSource m_StreamSource;
    private Schema m_Schema;
    private boolean m_Commitable = true;
    private DataSourceFactory m_DataSourceFactory;

    public SimpleWMSDriver() throws DriverException {
        DefaultMetadata metadata = new DefaultMetadata();
        metadata.addField("host", Type.STRING);
        metadata.addField("layer", Type.STRING);
        metadata.addField("srs", Type.STRING);
        metadata.addField("format", Type.STRING);

        this.m_Schema = new DefaultSchema(DRIVER_NAME + this.hashCode());
        this.m_Schema.addTable("main", metadata);
    }

    @Override
    public void open(StreamSource streamSource) throws DriverException {
        this.m_StreamSource = streamSource;

    }

    @Override
    public void close() throws DriverException {
        this.m_StreamSource = null;
    }

    @Override
    public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
        //Pour le moment mais c'est a changer xD
        
        if (rowIndex == 0) {
            switch (fieldId) {
                case 0:
                    System.out.println("getHost " + m_StreamSource.getHost());
                    return ValueFactory.createValue(m_StreamSource.getHost());
                case 1:
                    System.out.println("getLayerName " + m_StreamSource.getLayerName());
                    return ValueFactory.createValue(m_StreamSource.getLayerName());
                case 2:
                    System.out.println("getSRS " + m_StreamSource.getSRS());
                    return ValueFactory.createValue(m_StreamSource.getSRS());
                case 3:
                    System.out.println("getImageFormat " + m_StreamSource.getImageFormat());
                    return ValueFactory.createValue(m_StreamSource.getImageFormat());
            }
        }
        return null;
    }

    @Override
    public long getRowCount() throws DriverException {
        return 1;
    }

    @Override
    public Number[] getScope(int dimension) throws DriverException {
        return null;
    }

    @Override
    public Metadata getMetadata() throws DriverException {
        return this.m_Schema.getTableByName("main");
    }

    @Override
    public boolean write(DataSet dataSource, ProgressMonitor pm) throws DriverException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setURL(String url) throws DriverException {
        this.m_url = url;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public String[] getPrefixes() {
        return new String[]{"wms"};
    }

    @Override
    public Schema getSchema() throws DriverException {
        return this.m_Schema;
    }

    @Override
    public DataSet getTable(String name) {
        if (!name.equals(DriverManager.DEFAULT_SINGLE_TABLE_NAME)) {
            return null;
        }
        return this;
    }

    @Override
    public void setDataSourceFactory(DataSourceFactory dsf) {
        this.m_DataSourceFactory = dsf;
    }

    @Override
    public int getSupportedType() {
        return SourceManager.MEMORY | SourceManager.RASTER;
    }

    @Override
    public int getType() {
        return SourceManager.WMS;
    }

    @Override
    public String getTypeName() {
        return "WMSStreamDriver";
    }

    @Override
    public String getTypeDescription() {
        return "Simple WMS Stream Driver";
    }

    @Override
    public String getDriverId() {
        return this.DRIVER_NAME;
    }

    @Override
    public boolean isCommitable() {
        return this.m_Commitable;
    }

    public void setCommitable(boolean commitable) {
        this.m_Commitable = commitable;
    }

    @Override
    public TypeDefinition[] getTypesDefinitions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String validateMetadata(Metadata metadata) throws DriverException {
        if (metadata.getFieldCount() != 1) {
            return "Cannot store more than one raster field";
        } else {
            int typeCode = metadata.getFieldType(0).getTypeCode();
            if (typeCode != Type.RASTER) {
                return "Cannot store " + TypeFactory.getTypeName(typeCode);
            }
        }
        return null;
    }
}
