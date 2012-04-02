/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.layerModel;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.gvsig.remoteClient.utils.BoundaryBox;
import org.orbisgis.core.layerModel.persistence.LayerType;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.renderer.legend.WMSLegend;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;
import org.gdms.data.stream.StreamDataSourceAdapter;
import org.gdms.driver.wms.SimpleWMSDriver;
import org.orbisgis.core.Services;

public class WMSLayer extends GdmsLayer {

    private static final String NOT_SUPPORTED = I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.methodNotSupportedInWMSLayer"); //$NON-NLS-1$
    private DataSource ds;
    private String wmslayerName;
    private SimpleWMSDriver driver;

    public WMSLayer(String name, DataSource ds) {
        super(name);
        this.ds = ds;
    }

    @Override
    public DataSource getDataSource() {
        return ds;
    }

    @Override
    public Envelope getEnvelope() {
        //Move Envelope in DataSource
        //return ((SimpleWMSDriver)ds.getDriver()).getEnvelope();
        
        
        Envelope result = new Envelope();

        if (null != ds) {
            try {
                //TODO Look RightValueDecorator for other layer, driver etc for getScop.
                result = ds.getFullExtent();
            } catch (DriverException e) {
                Services.getErrorManager().error(
                        I18N.getString("org.orbisgis.layerModel.layer.cannotGetTheExtentOfLayer") //$NON-NLS-1$
                        + ds.getName(), e);
            }
        }
        return result;  
    }

    @Override
    public GeoRaster getRaster() throws DriverException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public RasterLegend[] getRasterLegend() throws DriverException,
            UnsupportedOperationException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public RasterLegend[] getRasterLegend(String fieldName)
            throws IllegalArgumentException, DriverException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public Legend[] getRenderingLegend() throws DriverException {
        try {
        return new Legend[]{
            new WMSLegend(
                ((SimpleWMSDriver)ds.getDriver()).getWMSClient(),
                ((SimpleWMSDriver)ds.getDriver()).getWMSStatus(),
                wmslayerName)};
        } catch(ConnectException e) {
            throw new DriverException(e);
        } catch(IOException e) {
            throw new DriverException(e);
        }
    }

    @Override
    public int[] getSelection() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public Legend[] getVectorLegend() throws DriverException,
            UnsupportedOperationException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public Legend[] getVectorLegend(String fieldName)
            throws IllegalArgumentException, DriverException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public boolean isRaster() throws DriverException {
        return false;
    }

    @Override
    public boolean isVectorial() throws DriverException {
        return false;
    }

    @Override
    public void open() throws LayerException {
        super.open();
        try {
            ds.open();
            driver = (SimpleWMSDriver) ds.getDriver();
            
            //If we close here, the datasource is never open again. We get an exception.
            //ds.close();
        } catch (AlreadyClosedException e) {
            throw new LayerException(I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.bug"), e); //$NON-NLS-1$
        } catch (DriverException e) {
            throw new LayerException(I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.cannotOpenWMSDescription"), e); //$NON-NLS-1$
//        } catch (ConnectException e) {
//            throw new LayerException(I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.cannotConnectToWmsServer"), e); //$NON-NLS-1$
//        } catch (IOException e) {
//            throw new LayerException(I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.cannotRetrieveWMSServerContent"), e); //$NON-NLS-1$
        }
    }

    private org.gvsig.remoteClient.wms.WMSLayer find(String layerName,
            org.gvsig.remoteClient.wms.WMSLayer layer) {
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

    private BoundaryBox getLayerBoundingBox(String layerName,
            org.gvsig.remoteClient.wms.WMSLayer layer, String srs) {
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

    public boolean isWMS() {
        return true;
    }

    @Override
    public void restoreLayer(LayerType layer) throws LayerException {
        this.setName(layer.getName());
        this.setVisible(layer.isVisible());
    }

    @Override
    public LayerType saveLayer() {
        LayerType ret = new LayerType();
        ret.setName(getName());
        ret.setSourceName(getMainName());
        ret.setVisible(isVisible());
        return ret;
    }

    @Override
    public void setLegend(Legend... legends) throws DriverException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void setLegend(String fieldName, Legend... legends)
            throws IllegalArgumentException, DriverException {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void setSelection(int[] newSelection) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public boolean isStream() {
        return true;
    }
}
