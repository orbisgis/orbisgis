package org.gdms.driver.stream;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
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
import org.gvsig.remoteClient.utils.BoundaryBox;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSStatus;
//import org.orbisgis.core.layerModel.WMSClientPool;
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
    private WMSClient m_WMSClient;
    private WMSStatus m_WMSStatus;
    private Envelope m_Envelope;
    
    public SimpleWMSDriver() throws DriverException {
        DefaultMetadata metadata = new DefaultMetadata();
        metadata.addField("host", Type.STRING);
        metadata.addField("layer", Type.STRING);
        metadata.addField("srs", Type.STRING);
        metadata.addField("format", Type.STRING);

        this.m_Schema = new DefaultSchema(DRIVER_NAME + this.hashCode());
        this.m_Schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
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
                    return ValueFactory.createValue(m_StreamSource.getHost());
                case 1:
                    return ValueFactory.createValue(m_StreamSource.getLayerName());
                case 2:
                    return ValueFactory.createValue(m_StreamSource.getSRS());
                case 3:
                    return ValueFactory.createValue(m_StreamSource.getImageFormat());
            }
        }
        return null;
    }

    public WMSClient getWMSClient() throws ConnectException, IOException {

        String host = m_StreamSource.getHost(); //$NON-NLS-1$
        
        //Ne marche pas car on doit mettre orbisgis-core ... alors on feinte ...
        //m_WMSClient = WMSClientPool.getWMSClient(host);
        m_WMSClient = new WMSClient(host);
	m_WMSClient.getCapabilities(null, true, null);
        //m_WMSClient.getCapabilities(null, false, null);
        m_WMSStatus = new WMSStatus();
        String wmslayerName = m_StreamSource.getLayerName(); //$NON-NLS-1$
        m_WMSStatus.addLayerName(wmslayerName);
        m_WMSStatus.setSrs(m_StreamSource.getSRS()); //$NON-NLS-1$

        BoundaryBox bbox = getLayerBoundingBox(wmslayerName, m_WMSClient.getRootLayer(), m_WMSStatus.getSrs());
        m_WMSStatus.setExtent(new Rectangle2D.Double(bbox.getXmin(), bbox.getYmin(), bbox.getXmax() - bbox.getXmin(), bbox.getYmax()
                - bbox.getYmin()));
        m_Envelope = new Envelope(bbox.getXmin(), bbox.getXmax(), bbox.getYmin(), bbox.getYmax());
        m_WMSStatus.setFormat(m_StreamSource.getImageFormat()); //$NON-NLS-1$
        
        return m_WMSClient;
    }
    
    public Envelope getEnvelope() {
        return this.m_Envelope;
    }
    
    public WMSStatus getWMSStatus(){
        return this.m_WMSStatus;
    }

    private org.gvsig.remoteClient.wms.WMSLayer find(String layerName, org.gvsig.remoteClient.wms.WMSLayer layer) {
        if (layerName.equals(layer.getName())) {
            return layer;
        } else {
            ArrayList<?> children = layer.getChildren();
            for (Object object : children) {
                org.gvsig.remoteClient.wms.WMSLayer child = (org.gvsig.remoteClient.wms.WMSLayer) object;
                org.gvsig.remoteClient.wms.WMSLayer ret = find(layerName, child);
                if (ret != null) {
                    return ret;
                }
            }
        }

        return null;
    }

    private BoundaryBox getLayerBoundingBox(String layerName, org.gvsig.remoteClient.wms.WMSLayer layer, String srs) {
        org.gvsig.remoteClient.wms.WMSLayer wmsLayer = find(layerName, layer);
        // Obtain the bbox at current level
        BoundaryBox bbox = wmsLayer.getBbox(srs);
        while ((bbox == null) && (wmsLayer.getParent() != null)) {
            wmsLayer = wmsLayer.getParent();
            bbox = wmsLayer.getBbox(srs);
        }

        // Some wrong bbox to not have null pointer exceptions
        if (bbox == null) {
            bbox = new BoundaryBox();
            bbox.setXmin(0);
            bbox.setYmin(0);
            bbox.setXmax(100);
            bbox.setYmax(100);
            bbox.setSrs(srs);
        }
        return bbox;
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
        return this.m_Schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
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
        return "Stream WMS";
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
