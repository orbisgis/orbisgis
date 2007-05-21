package org.gdms.spatial;

import org.geotools.referencing.crs.DefaultGeographicCRS;

public class NullCRS extends DefaultGeographicCRS {
	static {
		singleton = new NullCRS();
	}

	public static NullCRS singleton;

	public NullCRS() {
		super(WGS84);
	}
}