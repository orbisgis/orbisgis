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
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.orbisgis.core.layerModel.persistence.LayerType;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.renderer.legend.WMSLegend;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;
import org.gdms.driver.stream.SimpleWMSDriver;

public class WMSLayer extends GdmsLayer {

	private static final String NOT_SUPPORTED = I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.methodNotSupportedInWMSLayer"); //$NON-NLS-1$
	private DataSource ds;
	private Envelope envelope;
	private WMSConnection connection;
	private String wmslayerName;

	public WMSLayer(String name, DataSource ds) {
		super(name);
		this.ds = ds;
	}

	@Override
	public DataSource getDataSource() {
		return null;
	}

	@Override
	public Envelope getEnvelope() {
		return envelope;
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

	public Legend[] getRenderingLegend() throws DriverException {
		return new Legend[] { getWMSLegend() };
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

	public WMSLegend getWMSLegend() {
		return new WMSLegend(getWMSConnection(), wmslayerName);

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
                        SimpleWMSDriver driver = (SimpleWMSDriver)ds.getDriver();
                        /*
                        String host = driver.getHost(); 
                        
                        
                        
			//String host = ds.getString(0, "host"); //$NON-NLS-1$
                        System.out.println("Host : " + host);
			
                        
                        WMSClient client = WMSClientPool.getWMSClient(host);
			client.getCapabilities(null, false, null);
			WMSStatus status = new WMSStatus();
			wmslayerName = ds.getString(0, "layer"); //$NON-NLS-1$
			status.addLayerName(wmslayerName);
			status.setSrs(ds.getString(0, "srs")); //$NON-NLS-1$
                        
			BoundaryBox bbox = getLayerBoundingBox(wmslayerName, client
					.getRootLayer(), status.getSrs());
			status.setExtent(new Rectangle2D.Double(bbox.getXmin(), bbox
					.getYmin(), bbox.getXmax() - bbox.getXmin(), bbox.getYmax()
					- bbox.getYmin()));
			envelope = new Envelope(bbox.getXmin(), bbox.getXmax(), bbox
					.getYmin(), bbox.getYmax());
			status.setFormat(ds.getString(0, "format")); //$NON-NLS-1$
			connection = new WMSConnection(client, status);
                        * 
                        */
                        
                        //TODO A revoir une fois que le gdms gerera tout les clients
                        WMSClient client = driver.getWMSClient();
                        WMSClientPool.registerClient(client);
                        connection = new WMSConnection(client, driver.getWMSStatus());
                        envelope = driver.getEnvelope();
			ds.close();
		} catch (AlreadyClosedException e) {
			throw new LayerException(I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.bug"), e); //$NON-NLS-1$
		} catch (DriverException e) {
			throw new LayerException(I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.cannotOpenWMSDescription"), e); //$NON-NLS-1$
		} catch (ConnectException e) {
			throw new LayerException(I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.cannotConnectToWmsServer"), e); //$NON-NLS-1$
		} catch (IOException e) {
			throw new LayerException(I18N.getString("orbisgis-core.org.orbisgis.wMSLayer.cannotRetrieveWMSServerContent"), e); //$NON-NLS-1$
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

	public WMSConnection getWMSConnection() {
		return connection;
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

}
