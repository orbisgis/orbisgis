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

import com.vividsolutions.jts.geom.Envelope;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.gvsig.remoteClient.utils.BoundaryBox;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.orbisgis.core.renderer.legend.WMSLegend;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;

public class WMSLayer extends BeanLayer {

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
		throw new UnsupportedOperationException(I18N.tr("Method not supported in WMS layers"));
	}

	public WMSLegend getRenderingLegend() throws DriverException {
		return getWMSLegend();
	}

	@Override
	public int[] getSelection() {
		throw new UnsupportedOperationException(I18N.tr("Method not supported in WMS layers"));
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
		try {
			ds.open();
			String host = ds.getString(0, "host"); //$NON-NLS-1$
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
			ds.close();
		} catch (AlreadyClosedException e) {
			throw new LayerException(e); //$NON-NLS-1$
		} catch (DriverException e) {
			throw new LayerException(I18N.tr("Cannot open wms description"), e); //$NON-NLS-1$
		} catch (ConnectException e) {
			throw new LayerException(I18N.tr("Cannot connect to WMS server"), e); //$NON-NLS-1$
		} catch (IOException e) {
			throw new LayerException(I18N.tr("Cannot retrieve WMS server content"), e); //$NON-NLS-1$
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
	public void setSelection(int[] newSelection) {
		throw new UnsupportedOperationException(I18N.tr("Method not supported in WMS layers"));
	}

        @Override
        public List<Style> getStyles(){
                return null;
        }

        @Override
        public Style getStyle(int i){
                return null;
        }

	@Override
	public ArrayList<Rule> getRenderingRule() throws DriverException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

        public void addLayerListenerRecursively(LayerListener listener) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeLayerListenerRecursively(LayerListener listener) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public ILayer remove(ILayer layer, boolean isMoving) throws LayerException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public ILayer remove(ILayer layer) throws LayerException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public ILayer remove(String layerName) throws LayerException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addLayer(ILayer layer) throws LayerException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addLayer(ILayer layer, boolean isMoving) throws LayerException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public ILayer getLayerByName(String layerName) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean acceptsChilds() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public ILayer[] getChildren() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public void insertLayer(ILayer layer, int index) throws LayerException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getIndex(ILayer targetLayer) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public void close() throws LayerException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public void insertLayer(ILayer layer, int index, boolean isMoving) throws LayerException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public int getLayerCount() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public ILayer getLayer(int index) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public ILayer[] getRasterLayers() throws DriverException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public ILayer[] getVectorLayers() throws DriverException {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
