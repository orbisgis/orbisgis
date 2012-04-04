package org.gdms.driver.wms;

import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.vividsolutions.jts.geom.Envelope;
import org.apache.log4j.Logger;
import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.exceptions.WMSException;
import org.gvsig.remoteClient.utils.BoundaryBox;
import org.gvsig.remoteClient.wms.ICancellable;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.stream.DefaultGeoStream;
import org.gdms.data.stream.GeoStream;
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
import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.exceptions.WMSException;
import org.gvsig.remoteClient.utils.BoundaryBox;
import org.gvsig.remoteClient.wms.ICancellable;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSLayer;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.orbisgis.progress.ProgressMonitor;

/**
 * The driver who gets the information about a flux WMS(host,srs,format,), it
 * can provide an acces to the GDMS, and return a value about the WMSclient to
 * the layer.
 * 
 * @author Vincent Dépériers
 */
public final class SimpleWMSDriver extends AbstractDataSet implements StreamReadWriteDriver {

    public static final String DRIVER_NAME = "Simple WMS driver";
    private static final Logger LOG = Logger.getLogger(SimpleWMSDriver.class);
    private Schema m_Schema;
    private boolean m_Commitable = false;
    private GeoStream m_GeoStream;

    public SimpleWMSDriver() throws DriverException {
        DefaultMetadata metadata = new DefaultMetadata();
        metadata.addField("stream", Type.STREAM);

        this.m_Schema = new DefaultSchema(DRIVER_NAME + this.hashCode());
        this.m_Schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
    }

    /**
     * Open the WMS driver.
     *
     * @param streamSource
     * @throws DriverException
     */
    @Override
    public void open(StreamSource streamSource) throws DriverException {
            LOG.trace("Opening WMS Stream");
        try {
            //Initialise the WMSClient and get the capabilities
            WMSClient WMSClient = WMSClientPool.getWMSClient(streamSource.getHost());
            WMSClient.getCapabilities(null, true, null);
            
            //Get the layer largest boundingbox
            BoundaryBox bbox = getLayerBoundingBox(streamSource.getLayerName(), WMSClient.getRootLayer(), streamSource.getSRS());  
            Envelope envelope = new Envelope(bbox.getXmin(), bbox.getXmax(), bbox.getYmin(), bbox.getYmax());

            //Create the GeoStream object
            m_GeoStream = new DefaultGeoStream(this, streamSource);
            m_GeoStream.setEnvelope(envelope);
        } catch (ConnectException e) {
            throw new DriverException(e);
        } catch (IOException e) {
            throw new DriverException(e);
        }
    }

    /**
     * Close the flux source.
     *
     * @throws DriverException
     */
    @Override
    public void close() throws DriverException {
            LOG.trace("Closing WMS Stream");
        //this.m_StreamSource = null;
    }

    /**
     * Gets the value(host,src,layer name,format) in each field.
     *
     * @param rowIndex
     * @param fieldId
     * @return
     * @throws DriverException
     */
    @Override
    public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
        if(fieldId == 0) {
               return  ValueFactory.createValue(this.m_GeoStream);
        } else {
        return null;
    }
    }

    /**
     * Get image from the WMS stream. We creat a WMSStatus 
     * We get the StreamSource from the GeoStream then we can initialize the WMSStatus
     * We get the WMSClient from the WMSClientPool. 
     * We always get the client because the driver is open first
     *      * 
     * @param width
     * @param height
     * @param extent
     * @param pm
     * @return
     * @throws DriverException 
     */
    @Override
    public Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws DriverException {
        try {
                m_GeoStream.setEnvelope(extent);
                StreamSource streamSource = m_GeoStream.getStreamSource();
            
                //Create the WMSStatus object
                WMSStatus WMSStatus = new WMSStatus();
                WMSStatus.addLayerName(streamSource.getLayerName());
                WMSStatus.setSrs(streamSource.getSRS());
                WMSStatus.setFormat(streamSource.getImageFormat());
                WMSStatus.setWidth(width);
                WMSStatus.setHeight(height);
                WMSStatus.setExtent(new Rectangle2D.Double(extent.getMinX(), extent.getMinY(), extent.getWidth(), extent.getHeight()));

                //Driver is always open first so a WMSClient is available in the WMSClientPool
                return ImageIO.read(WMSClientPool.getWMSClient(streamSource.getHost()).getMap(WMSStatus, null));
        } catch (WMSException e) {
            throw new DriverException(e);
        } catch (ServerErrorException e) {
            throw new DriverException(e);
        } catch (IOException e) {
            throw new DriverException(e);
        }
    }

    private org.gvsig.remoteClient.wms.WMSLayer find(String layerName, WMSLayer layer) {
        if (layerName.equals(layer.getName())) {
            return layer;
        } else {
            ArrayList<?> children = layer.getChildren();
            for (Object object : children) {
                WMSLayer child = (WMSLayer) object;
                WMSLayer ret = find(layerName, child);
                if (ret != null) {
                    return ret;
                }
            }
        }

        return null;
    }

    /**
     * Gets the boundary box of the layer with the srs and the layer name.
     *
     * @param layerName
     * @param layer
     * @param srs
     * @return
     */
    // cette méthode est plutôt setLayerBoundingBox?
    private BoundaryBox getLayerBoundingBox(String layerName, WMSLayer layer, String srs) {
        WMSLayer wmsLayer = find(layerName, layer);
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
        switch(dimension) {
                case DataSet.X: {
                        Number[] number = {m_GeoStream.getEnvelope().getMaxX(), m_GeoStream.getEnvelope().getMinX()};
                        return number;
                }
                case DataSet.Y:{
                        Number[] number = {m_GeoStream.getEnvelope().getMaxY(), m_GeoStream.getEnvelope().getMinY()};
                        return number;
                }
                default:
                        throw new DriverException("Unimplemented dimension");
        }
    }

    /**
     * Gets the metadata stored in the schema of the driver.
     *
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
     * Checks if the driver is currently open.
     *
     * @return
     */
    @Override
    public boolean isOpen() {
        return true;
    }

    /**
     * Gets the prefix of the driver, returns "wms" for a wms driver.
     *
     * @return
     */
    @Override
    public String[] getPrefixes() {
        return new String[]{"wms"};
    }

    /**
     * Gets the schema of the flux.
     *
     * @return
     * @throws DriverException
     */
    @Override
    public Schema getSchema() throws DriverException {
        return this.m_Schema;
    }

    /**
     * Gets the DataSet of the flux.
     *
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
     *
     * @param dsf
     */
    @Override
    public void setDataSourceFactory(DataSourceFactory dsf) {
    }

    /**
     * Gets the type of flux that can be supported by the driver. For now, WMS
     * driver can support Raster and Memory.
     *
     * @return
     */
    @Override
    public int getSupportedType() {
        return SourceManager.MEMORY | SourceManager.RASTER;
    }

    /**
     * Gets the type of the sources that this driver can read, for a WMS driver,
     * it can read the flux WMS.
     *
     * @return
     */
    @Override
    public int getType() {
        return SourceManager.STREAM;
    }

    /**
     * Returns a short representation of the source types that this driver
     * accesses.
     *
     * @return
     */
    @Override
    public String getTypeName() {
        return "Stream WMS";
    }

    /**
     * Gets the definitions of the actual types that this driver can handle.
     *
     * @return
     */
    @Override
    public String getTypeDescription() {
        return "Simple WMS Stream Driver";
    }

    /**
     * Gets a description of the driver.
     *
     * @return
     */
    @Override
    public String getDriverId() {
        return DRIVER_NAME;
    }

    /**
     * Returns true if the driver can write contents to the source.
     *
     * @return
     */
    @Override
    public boolean isCommitable() {
        return this.m_Commitable;
    }

    /**
     * Sets if the driver can or can not wirte contents to the source.
     *
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
     *
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
