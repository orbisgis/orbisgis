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
package org.orbisgis.coremap.stream;

import java.awt.Image;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.cts.CRSFactory;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.h2gis.utilities.SpatialResultSet;
import org.orbisgis.progress.ProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;

/**
 * A driver that accesses a WMS stream.
 *
 * This can be used to open and access a source described by a
 * {@link org.orbisgis.core.stream.WMSStreamSource} whose StreamType is "wms".
 *
 * @author Antoine Gourlay
 * @author Vincent Dépériers
 */
public final class SimpleWMSDriver implements GeoStream {

    private static final I18n I18N = I18nFactory.getI18n(SimpleWMSDriver.class);
    private static final Logger LOG = Logger.getLogger(SimpleWMSDriver.class);
    private WMService wmsClient;
    private Capabilities cap;
    private MapLayer mapLayer;
    private Envelope envelope;
    private WMSStreamSource streamSource;

    public void open(WMSStreamSource streamSource) throws IOException {
        this.streamSource = streamSource;
        LOG.trace("Opening WMS Stream");
        try {
            //Initialise the WMSClient and get the capabilities
            StringBuilder sb = new StringBuilder();
            sb.append(streamSource.getScheme());
            sb.append("://");
            sb.append(streamSource.getHost());
            if(streamSource.getPort() != WMSStreamSource.DEFAULT_PORT ){
                sb.append(":").append(streamSource.getPort());
            }
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
            envelope = new Envelope(bbox.getWestBound(), bbox.getEastBound(),
                    bbox.getSouthBound(), bbox.getNorthBound());
        } catch (ConnectException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws IOException {
        if (streamSource == null) {
            throw new IOException(I18N.tr("WMS stream is not initialised"));
        }
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
    private BoundingBox getLayerBoundingBox(MapLayer layer, String srs) throws IOException {
        // Obtain the bbox at current level
        BoundingBox bbox = layer.getBoundingBox(srs);
        // Some wrong bbox to not have null pointer exceptions
        if (bbox == null) {
            List<BoundingBox> allBoundingBoxList = layer.getAllBoundingBoxList();
            BoundingBox original;
            if (!allBoundingBoxList.isEmpty()) {
                return allBoundingBoxList.get(0);
            } else {
                return layer.getLatLonBoundingBox();
            }
        }
        return bbox;
    }

    @Override
    public Envelope getEnvelope() {
        return envelope;
    }

    @Override
    public WMSStreamSource getStreamSource() {
        return streamSource;
    }
}
