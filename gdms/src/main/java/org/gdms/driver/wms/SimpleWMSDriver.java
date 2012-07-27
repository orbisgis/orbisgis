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
 * A driver that accesses a WMS stream.
 * 
 * This can be used to open and access a source described by a {@link StreamSource } whose
 * StreamType is "wms".
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

                schema = new DefaultSchema(DRIVER_NAME + this.hashCode());
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

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

        @Override
        public void close() throws DriverException {
                wmsClient.close();
                wmsClient = null;
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                if (fieldId == 0) {
                        return ValueFactory.createValue(geoStream);
                } else {
                        throw new DriverException("Internal error: asked for field " + fieldId +
                                 " on a Stream.");
                }
        }

        @Override
        public Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws DriverException {
                if (!isOpen()) {
                        throw new DriverException("Driver is closed!");
                }
                
                try {
                        StreamSource streamSource = geoStream.getStreamSource();

                        //Create the WMSStatus object
                        WMSStatus wmsStatus = new WMSStatus();
                        wmsStatus.addLayerName(streamSource.getLayerName());
                        wmsStatus.setSrs(streamSource.getSRS());
                        wmsStatus.setFormat(streamSource.getImageFormat());
                        wmsStatus.setWidth(width);
                        wmsStatus.setHeight(height);
                        wmsStatus.setExtent(new Rectangle2D.Double(extent.getMinX(), extent.getMinY(), extent.getWidth(), extent.getHeight()));

                        return ImageIO.read(wmsClient.getMap(wmsStatus, null));
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
        private BoundaryBox getLayerBoundingBox(String layerName, WMSLayer layer, String srs) throws DriverException {
                WMSLayer wmsLayer = find(layerName, layer);
                // Obtain the bbox at current level
                BoundaryBox bbox = wmsLayer.getBbox(srs);
                while ((bbox == null) && (wmsLayer.getParent() != null)) {
                        wmsLayer = wmsLayer.getParent();
                        bbox = wmsLayer.getBbox(srs);
                }

                // Some wrong bbox to not have null pointer exceptions
                if (bbox == null) {
                        throw new DriverException("Could not find a valid bounding box for the layer " + layerName);
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
                                return new Number[] {geoStream.getEnvelope().getMaxX(), geoStream.getEnvelope().getMinX()};
                        }
                        case DataSet.Y: {
                                return new Number[] {geoStream.getEnvelope().getMaxY(), geoStream.getEnvelope().getMinY()};
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
