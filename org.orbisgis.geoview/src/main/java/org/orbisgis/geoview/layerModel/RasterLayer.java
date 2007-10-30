package org.orbisgis.geoview.layerModel;

import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Envelope;

public class RasterLayer extends BasicLayer {
	private GeoRaster geoRaster;

	RasterLayer(String name,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
	}

	public void set(GeoRaster GeoRaster, Style style) throws Exception {
		setGeoRaster(GeoRaster);
		setStyle(style);
	}

	public GeoRaster getGeoRaster() {
		return geoRaster;
	}

	public void setGeoRaster(GeoRaster geoRaster) {
		this.geoRaster = geoRaster;
	}

	public Envelope getEnvelope() {
		if (null == geoRaster) {
			return new Envelope();
		} else {
			return geoRaster.getMetadata().getEnvelope();
		}
	}
}