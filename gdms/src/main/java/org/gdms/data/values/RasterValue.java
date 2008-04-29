package org.gdms.data.values;

import org.gdms.data.types.Type;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;

public class RasterValue extends AbstractValue {

	private GeoRaster geoRaster;

	RasterValue(GeoRaster geoRaster) {
		this.geoRaster = geoRaster;
	}

	public String getStringValue(ValueWriter writer) {
		return "Raster";
	}

	public int getType() {
		return Type.RASTER;
	}

	public int doHashCode() {
		return geoRaster.hashCode();
	}

	@Override
	public boolean doEquals(Object obj) {
		if (obj instanceof RasterValue) {
			return geoRaster.equals(((RasterValue) obj).geoRaster);
		} else {
			return false;
		}
	}

	public byte[] getBytes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public GeoRaster getAsRaster() throws IncompatibleTypesException {
		return geoRaster;
	}
}
