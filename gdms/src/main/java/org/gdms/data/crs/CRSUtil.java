package org.gdms.data.crs;

import org.gdms.data.types.CRSConstraint;
import org.gdms.data.types.Constraint;

import fr.cts.crs.CRSFactory;
import fr.cts.crs.CoordinateReferenceSystem;
import fr.cts.crs.NullCRS;
import fr.cts.crs.Proj4CRSFactory;

public class CRSUtil {
	private static CRSFactory crsFactory;

	public static CRSFactory getCRSFactory() {		
		return crsFactory;
	}
	
	public static CoordinateReferenceSystem getCRSFromEPSG(String code) {
		init(code);
		return CRSFactory.getCRS(code);
	}
	
	static void init(String code) {
		if (crsFactory == null) 
			crsFactory = new Proj4CRSFactory();
		try {
			crsFactory.createCRSCodes(code);

		} catch (java.net.MalformedURLException mue) {
		} catch (java.io.IOException ioe) {
		}		
	}

	public static Constraint getCRSConstraint(CoordinateReferenceSystem crs) {

		return new CRSConstraint(-1, crs);
	}

	public static Constraint getCRSConstraint(int srid) {

		if (srid == -1) {
			return new CRSConstraint(-1, NullCRS.singleton);
		} else {
			return new CRSConstraint(srid, CRSUtil.getCRSFromEPSG(Integer.toString(srid)));
		}
	}
}
