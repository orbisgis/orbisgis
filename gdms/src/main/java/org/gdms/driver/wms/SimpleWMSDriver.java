/**
 * The GDMS library (Generic Datasource Management System) is a middleware
 * dedicated to the management of various kinds of data-sources such as spatial
 * vectorial data or alphanumeric. Based on the JTS library and conform to the
 * OGC simple feature access specifications, it provides a complete and robust
 * API to manipulate in a SQL way remote DBMS (PostgreSQL, H2...) or flat files
 * (.shp, .csv...).
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
 * or contact directly: info@orbisgis.org
 */
package org.gdms.driver.wms;

import java.awt.Image;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;


import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.wms.BoundingBox;
import com.vividsolutions.wms.Capabilities;
import com.vividsolutions.wms.MapImageFormatChooser;
import com.vividsolutions.wms.MapLayer;
import com.vividsolutions.wms.MapRequest;
import com.vividsolutions.wms.WMService;
import java.util.ArrayList;
import java.util.Map;
import org.apache.log4j.Logger;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.gdms.data.stream.WMSStreamSource;
import org.gdms.data.values.*;
import org.orbisgis.progress.ProgressMonitor;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.stream.DefaultGeoStream;
import org.gdms.data.stream.GeoStream;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.StreamDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.crs.ST_Transform;

/**
 * A driver that accesses a WMS stream.
 *
 * This can be used to open and access a source described by a
 * {@link org.gdms.data.stream.WMSStreamSource} whose StreamType is "wms".
 *
 * @author Antoine Gourlay
 * @author Vincent Dépériers
 */
public final class SimpleWMSDriver extends AbstractDataSet implements StreamDriver {

    public static final String DRIVER_NAME = "Simple WMS driver";
    private static final Logger LOG = Logger.getLogger(SimpleWMSDriver.class);
    private Schema schema;
    private GeoStream geoStream;
    private WMService wmsClient;
    private Capabilities cap;
    private MapLayer mapLayer;

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
    public void open(WMSStreamSource streamSource) throws DriverException {
        LOG.trace("Opening WMS Stream");
        try {
            //Initialise the WMSClient and get the capabilities
            StringBuilder sb = new StringBuilder();
            sb.append(streamSource.getScheme());
            sb.append("://");
            sb.append(streamSource.getHost());
            sb.append(streamSource.getPath());
            sb.append("?");
            Map<String, String> others = streamSource.getOthersQueryMap();
            for (Map.Entry<String, String> entry : others.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            String streamURL = sb.toString();
            wmsClient = new WMService(streamURL);
            wmsClient.initialize();
            cap = wmsClient.getCapabilities();
            String name = streamSource.getLayerName();
            MapLayer ml = cap.getTopLayer();
            mapLayer = find(name, ml);
            BoundingBox bbox = getLayerBoundingBox(mapLayer, streamSource.getCRS());

            //Create the GeoStream object
            geoStream = new DefaultGeoStream(this, streamSource,
                    new Envelope(bbox.getWestBound(), bbox.getEastBound(),
                    bbox.getSouthBound(), bbox.getNorthBound()));
        } catch (ConnectException e) {
            throw new DriverException(e);
        } catch (IOException e) {
            throw new DriverException(e);
        }
    }

    @Override
    public void close() throws DriverException {
        wmsClient = null;
    }

    @Override
    public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
        if (fieldId == 0) {
            return ValueFactory.createValue(geoStream);
        } else {
            throw new DriverException("Internal error: asked for field " + fieldId
                    + " on a Stream.");
        }
    }

    @Override
    public Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws DriverException {
        if (!isOpen()) {
            throw new DriverException("Driver is closed!");
        }

        try {
            WMSStreamSource streamSource = geoStream.getStreamSource();
            MapRequest mr = new MapRequest(wmsClient);
            mr.setVersion(wmsClient.getVersion());
            List<String> layers = new ArrayList<String>(1);
            layers.add(mapLayer.getName());
            mr.setLayerNames(layers);
            MapImageFormatChooser mifc = new MapImageFormatChooser(wmsClient.getVersion());
            mr.setFormat(mifc.chooseFormat(cap.getMapFormats()));
            BoundingBox bb = new BoundingBox(streamSource.getSRS(), extent.getMinX(),
                    extent.getMinY(), extent.getMaxX(), extent.getMaxY());
            mr.setBoundingBox(bb);
            mr.setFormat(streamSource.getImageFormat());
            mr.setImageWidth(width);
            mr.setImageHeight(height);
            mr.setTransparent(true);

            return mr.getImage();
        } catch (IOException e) {
            throw new DriverException(e);
        }
    }

    private MapLayer find(String name, MapLayer root) {
        if ((root.getName() != null && root.getName().equals(name))
                || (root.getName() == null && name == null)) {
            return root;
        } else {
            for (MapLayer l : root.getSubLayerList()) {
                MapLayer ml = find(name, l);
                if (ml != null) {
                    return ml;
                }
            }
        }
        return null;
    }

    /**
     * Gets the bounding box of the layer with the srs and the layer.
     *
     * @param layer The input MapLayer
     * @param srs A string representation of the expected SRS
     * @return The bounding box of layer in srs if it is explicitly advertised by the server. If it is not, we
     * retrieve an explicitly advertised bounding box defined on the server with its associated SRS and we
     * project it to the SRS we desire.
     */
    private BoundingBox getLayerBoundingBox(MapLayer layer, String srs) throws DriverException {
        // Obtain the bbox at current level
        BoundingBox bbox = layer.getBoundingBox(srs);
        // Some wrong bbox to not have null pointer exceptions
        if (bbox == null) {
            List<BoundingBox> allBoundingBoxList = layer.getAllBoundingBoxList();
            BoundingBox original;
            if (!allBoundingBoxList.isEmpty()) {
                original = allBoundingBoxList.get(0);
            } else {
                original = layer.getLatLonBoundingBox();
            }
            Envelope env = new Envelope(bbox.getWestBound(), bbox.getEastBound(),
                    bbox.getSouthBound(), bbox.getNorthBound());
            String originalSrs = original.getSRS();
            GeometryFactory gf = new GeometryFactory();
            Polygon poly = (Polygon) gf.toGeometry(env);
            ST_Transform transformFunction = new ST_Transform();
            try {
                if (BoundingBox.LATLON.equals(originalSrs)) {
                    originalSrs = "EPSG:4326";
                }
                CoordinateReferenceSystem inputCRS = DataSourceFactory.getCRSFactory().getCRS(originalSrs);
                Value val = transformFunction.evaluate(null,
                        ValueFactory.createValue(poly, inputCRS),
                        ValueFactory.createValue(srs));
                Envelope retEnv = val.getAsGeometry().getEnvelopeInternal();
                BoundingBox retBB = new BoundingBox(srs, retEnv.getMinX(), retEnv.getMinY(), retEnv.getMaxX(), retEnv.getMaxY());
                return retBB;
            } catch (FunctionException fe) {
                throw new DriverException("Could not find a valid bounding box for the layer " + layer.getName()
                        + " : " + fe.getCause());
            } catch (CRSException ex) {
                throw new DriverException("Could not find a valid coordinate reference system for the layer " + layer.getName()
                        + " : " + ex.getCause());
            }
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
                return new Number[]{geoStream.getEnvelope().getMaxX(), geoStream.getEnvelope().getMinX()};
            }
            case DataSet.Y: {
                return new Number[]{geoStream.getEnvelope().getMaxY(), geoStream.getEnvelope().getMinY()};
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
