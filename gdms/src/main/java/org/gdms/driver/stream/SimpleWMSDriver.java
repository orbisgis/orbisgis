package org.gdms.driver.stream;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.geom.Rectangle2D;
import java.io.File;
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
import org.gvsig.remoteClient.wms.ICancellable;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSStatus;
//import org.orbisgis.core.layerModel.WMSClientPool;
import org.orbisgis.progress.ProgressMonitor;

/**
 *
 * @author Vincent Dépériers
 */
/** The driver who gets the information about a flux WMS(host,srs,format,),
 *  it can provide an acces to the GDMS, and return a value about the WMSclient to the layer.
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
    /**
     * Open the WMS driver.
     * @param streamSource
     * @throws DriverException 
     */
    @Override
    public void open(StreamSource streamSource) throws DriverException {
        this.m_StreamSource = streamSource;

    }
    
    /**
     * Close the flux source.
     * @throws DriverException 
     */
    @Override
    public void close() throws DriverException {
        this.m_StreamSource = null;
    }

    /**
     * Gets the value(host,src,layer name,format) in each field.
     * @param rowIndex
     * @param fieldId
     * @return
     * @throws DriverException 
     */
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

    /**
     * With the host of the flux wms, creates a WMS client.
     * @return
     * @throws ConnectException
     * @throws IOException 
     */
    // c'est pas très claire cette méthode, il y a des choses dont on n'a pas besoin...
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
    
    /**
     * Gets the envelope of the flux.
     * @return 
     */
    public Envelope getEnvelope() {
        return this.m_Envelope;
    }
    
    /**
     * Gets the status of the flux.
     * @return 
     */
    public WMSStatus getWMSStatus(){
        return this.m_WMSStatus;
    }
    
    public File getMap(int width, int height,ICancellable cancel){
        m_WMSStatus.setHeight(height);
        m_WMSStatus.setExtent(new Rectangle2D.Double(m_Envelope.getMinX(), m_Envelope.getMinY(), m_Envelope.getWidth(), m_Envelope.getHeight()));
        throw new RuntimeException("Compiled Code");
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

    /**
     * Gets the boundary box of the layer with the srs and the layer name.
     * @param layerName
     * @param layer
     * @param srs
     * @return 
     */
    // cette méthode est plutôt setLayerBoundingBox?
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

    /**
     * Gets the metadata stored in the schema of the driver.
     * @return
     * @throws DriverException 
     */
    @Override
    public Metadata getMetadata() throws DriverException {
        return this.m_Schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
    }

    @Override
    public boolean write(DataSet dataSource, ProgressMonitor pm) throws DriverException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Sets the adresse URL of the flux.
     * @param url
     * @throws DriverException 
     */
    @Override
    public void setURL(String url) throws DriverException {
        this.m_url = url;
    }

    /**
     * Checks if the driver is currently open.
     * @return 
     */
    @Override
    public boolean isOpen() {
        return true;
    }

    /**
     * Gets the prefix of the driver, returns "wms" for a wms driver.
     * @return 
     */
    @Override
    public String[] getPrefixes() {
        return new String[]{"wms"};
    }

    /**
     * Gets the schema of the flux.
     * @return
     * @throws DriverException 
     */
    @Override
    public Schema getSchema() throws DriverException {
        return this.m_Schema;
    }

    /**
     * Gets the DataSet  of the flux.
     * @param name
     * @return 
     */
    @Override
    public DataSet getTable(String name) {
        if (!name.equals(DriverManager.DEFAULT_SINGLE_TABLE_NAME)) {
            return null;
        }
        return this;
    }

    /**
     * Gets the data source factory of this flux.
     * @param dsf 
     */
    @Override
    public void setDataSourceFactory(DataSourceFactory dsf) {
        this.m_DataSourceFactory = dsf;
    }

    /**
     * Gets the type of flux that can be supported by the driver. For now, WMS driver can support Raster and Memory.
     * @return 
     */
    @Override
    public int getSupportedType() {
        return SourceManager.MEMORY | SourceManager.RASTER;
    }

    /**
     * Gets the type of the sources that this driver can read, for a WMS driver, it can read the flux WMS.
     * @return 
     */
    @Override
    public int getType() {
        return SourceManager.WMS;
    }

    /**
    * Returns a short representation of the source types that this driver accesses.
    * @return 
    */
    @Override
    public String getTypeName() {
        return "Stream WMS";
    }

    /**
     * Gets the definitions of the actual types that this driver can handle.
     * @return 
     */
    @Override
    public String getTypeDescription() {
        return "Simple WMS Stream Driver";
    }

    /**
     * Gets a description of the driver.
     * @return 
     */
    @Override
    public String getDriverId() {
        return this.DRIVER_NAME;
    }

    /**
     * Returns true if the driver can write contents to the source.
     * @return 
     */
    @Override
    public boolean isCommitable() {
        return this.m_Commitable;
    }

    /**
     * Sets if the driver can or can not wirte contents to the source.
     * @param commitable 
     */
    public void setCommitable(boolean commitable) {
        this.m_Commitable = commitable;
    }

    @Override
    public TypeDefinition[] getTypesDefinitions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Checks if the metadata can be stored in this driver.
     * @param metadata
     * @return
     * @throws DriverException 
     */
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
