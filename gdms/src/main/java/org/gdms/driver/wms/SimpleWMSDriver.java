package org.gdms.driver.wms;

import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import javax.imageio.ImageIO;

import com.vividsolutions.jts.geom.Envelope;
import org.apache.log4j.Logger;
import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.exceptions.WMSException;
import org.gvsig.remoteClient.utils.BoundaryBox;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSLayer;
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
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.StreamDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

/**
 * The driver who gets the information about a flux WMS(host,srs,format,), it
 * can provide an acces to the GDMS, and return a value about the WMSclient to
 * the layer.
 *
 * @author Antoine Gourlay
 * @author Vincent Dépériers
 */
public final class SimpleWMSDriver extends AbstractDataSet implements StreamDriver {

        public static final String DRIVER_NAME = "Simple WMS driver";
        private static final Logger LOG = Logger.getLogger(SimpleWMSDriver.class);
        private Schema schema;
        private GeoStream geoStream;
        private WMSClient wmsClient;

        /**
         * Creates a new WMS driver.
         *
         * @throws DriverException
         */
        public SimpleWMSDriver() throws DriverException {
                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("stream", Type.STREAM);

                this.schema = new DefaultSchema(DRIVER_NAME + this.hashCode());
                this.schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

        /**
         * Opens the WMS driver.
         *
         * @param streamSource a stream source to open
         * @throws DriverException if there is an error accessing the WMS server
         */
        @Override
        public void open(StreamSource streamSource) throws DriverException {
                LOG.trace("Opening WMS Stream");
                try {
                        //Initialise the WMSClient and get the capabilities
                        wmsClient = new WMSClient(streamSource.getHost());
                        wmsClient.getCapabilities(null, true, null);

                        //Get the layer largest boundingbox
                        BoundaryBox bbox = getLayerBoundingBox(streamSource.getLayerName(), wmsClient.getRootLayer(), streamSource.getSRS());

                        //Create the GeoStream object
                        geoStream = new DefaultGeoStream(this, streamSource, 
                                new Envelope(bbox.getXmin(), bbox.getXmax(), bbox.getYmin(), bbox.getYmax()));
                } catch (ConnectException e) {
                        throw new DriverException(e);
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * Close the stream source.
         *
         * @throws DriverException
         */
        @Override
        public void close() throws DriverException {
                wmsClient.close();
                wmsClient = null;
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                if (fieldId == 0) {
                        return ValueFactory.createValue(this.geoStream);
                } else {
                        throw new DriverException("Internal error: asked for field " + fieldId +
                                 " on a Stream.");
                }
        }

        /**
         * Get image from the WMS stream. 
         * 
         * We create a WMSStatus
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
                if (!isOpen()) {
                        throw new DriverException("Driver is closed!");
                }
                
                try {
                        StreamSource streamSource = geoStream.getStreamSource();

                        //Create the WMSStatus object
                        WMSStatus WMSStatus = new WMSStatus();
                        WMSStatus.addLayerName(streamSource.getLayerName());
                        WMSStatus.setSrs(streamSource.getSRS());
                        WMSStatus.setFormat(streamSource.getImageFormat());
                        WMSStatus.setWidth(width);
                        WMSStatus.setHeight(height);
                        WMSStatus.setExtent(new Rectangle2D.Double(extent.getMinX(), extent.getMinY(), extent.getWidth(), extent.getHeight()));

                        return ImageIO.read(wmsClient.getMap(WMSStatus, null));
                } catch (WMSException e) {
                        throw new DriverException(e);
                } catch (ServerErrorException e) {
                        throw new DriverException(e);
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        private WMSLayer find(String layerName, WMSLayer layer) {
                if (layerName.equals(layer.getName())) {
                        return layer;
                } else {
                        List<WMSLayer> children = layer.getChildren();
                        for (WMSLayer child : children) {
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
                switch (dimension) {
                        case DataSet.X: {
                                Number[] number = {geoStream.getEnvelope().getMaxX(), geoStream.getEnvelope().getMinX()};
                                return number;
                        }
                        case DataSet.Y: {
                                Number[] number = {geoStream.getEnvelope().getMaxY(), geoStream.getEnvelope().getMinY()};
                                return number;
                        }
                        default:
                                throw new DriverException("Unimplemented dimension");
                }
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
        }

        @Override
        public boolean isOpen() {
                return wmsClient != null;
        }

        @Override
        public String[] getStreamTypes() {
                return new String[]{"wms"};
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
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
        }

        @Override
        public int getSupportedType() {
                return SourceManager.STREAM;
        }

        @Override
        public int getType() {
                return SourceManager.STREAM;
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
                return DRIVER_NAME;
        }

        @Override
        public boolean isCommitable() {
                return false;
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                throw new UnsupportedOperationException();
        }

        @Override
        public String validateMetadata(Metadata metadata) throws DriverException {
                return null;
        }
}
