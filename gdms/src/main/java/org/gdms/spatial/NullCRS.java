package org.gdms.spatial;

import java.util.HashMap;

import org.geotools.metadata.iso.extent.ExtentImpl;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.cs.DefaultEllipsoidalCS;
import org.geotools.referencing.datum.DefaultGeodeticDatum;

@SuppressWarnings("unchecked")
public class NullCRS {
	private static HashMap properties;

	static {
		properties = new HashMap(4);
		properties.put(DefaultGeographicCRS.NAME_KEY, "WGS84");
		properties.put(DefaultGeographicCRS.VALID_AREA_KEY, ExtentImpl.WORLD);
		singleton = new DefaultGeographicCRS(properties,
				DefaultGeodeticDatum.WGS84, DefaultEllipsoidalCS.GEODETIC_2D);
	}

	public static DefaultGeographicCRS singleton;

}