package org.orbisgis.geoview;

import org.gdms.data.DataSource;
import org.grap.model.GeoRaster;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Envelope;

public class LayerStackEntry {
	private DataSource dataSource;

	private GeoRaster geoRaster;

	private Style style;

	private Envelope mapEnvelope;

	private String layerName;

	public LayerStackEntry(final DataSource dataSource, final Style style,
			String layerName) {
		this.dataSource = dataSource;
		this.style = style;
		this.layerName = layerName;
	}

	public LayerStackEntry(final GeoRaster geoRaster, final Style style,
			final Envelope mapEnvelope, String layerName) {
		this.geoRaster = geoRaster;
		this.style = style;
		this.mapEnvelope = mapEnvelope;
		this.layerName = layerName;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public GeoRaster getGeoRaster() {
		return geoRaster;
	}

	public Style getStyle() {
		return style;
	}

	public Envelope getMapEnvelope() {
		return mapEnvelope;
	}

	public String getLayerName() {
		return layerName;
	}
}