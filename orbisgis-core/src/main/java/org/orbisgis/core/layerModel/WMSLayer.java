package org.orbisgis.core.layerModel;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.gvsig.remoteClient.utils.BoundaryBox;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.layerModel.persistence.LayerType;

import com.vividsolutions.jts.geom.Envelope;

public class WMSLayer extends GdmsLayer {

	private static final String NOT_SUPPORTED = "Method not supported in WMS layers";
	private DataSource ds;
	private Envelope envelope;
	private WMSConnection connection;

	public WMSLayer(String name, DataSource ds,
			CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.ds = ds;
	}

	@Override
	public SpatialDataSourceDecorator getDataSource() {
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

	@Override
	public Legend[] getRenderingLegend() throws DriverException {
		return new Legend[0];
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
			String host = ds.getString(0, "host");
			WMSClient client = WMSClientPool.getWMSClient(host);
			client.getCapabilities(null, false, null);
			WMSStatus status = new WMSStatus();
			String layerName = ds.getString(0, "layer");
			status.addLayerName(layerName);
			status.setSrs(ds.getString(0, "srs"));

			BoundaryBox bbox = getLayerBoundingBox(layerName, client
					.getRootLayer(), status.getSrs());
			status.setExtent(new Rectangle2D.Double(bbox.getXmin(), bbox
					.getYmin(), bbox.getXmax() - bbox.getXmin(), bbox.getYmax()
					- bbox.getYmin()));
			envelope = new Envelope(bbox.getXmin(), bbox.getXmax(), bbox
					.getYmin(), bbox.getYmax());
			status.setFormat(ds.getString(0, "format"));
			connection = new WMSConnection(client, status);
			ds.close();
		} catch (AlreadyClosedException e) {
			throw new LayerException("Bug", e);
		} catch (DriverException e) {
			throw new LayerException("Cannot open wms description", e);
		} catch (ConnectException e) {
			throw new LayerException("Cannot connect to WMS server", e);
		} catch (IOException e) {
			throw new LayerException("Cannot retrieve WMS server content", e);
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
