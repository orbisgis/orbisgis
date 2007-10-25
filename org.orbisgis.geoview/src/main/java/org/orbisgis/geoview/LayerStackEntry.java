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

	public LayerStackEntry(final DataSource dataSource, final Style style) {
		this.dataSource = dataSource;
		this.style = style;
	}

	public LayerStackEntry(final GeoRaster geoRaster,
			final Style style, final Envelope mapEnvelope) {
		this.geoRaster = geoRaster;
		this.style = style;
		this.mapEnvelope = mapEnvelope;
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
}