package org.gdms.spatial;

import java.util.Collection;
import java.util.Set;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.util.InternationalString;

@SuppressWarnings("unchecked")
public class NullCRS {
	public static class OGCRS implements CoordinateReferenceSystem {

		public CoordinateSystem getCoordinateSystem() {
			throw new UnsupportedOperationException("CRS not implemented yet");
		}

		public InternationalString getScope() {
			throw new UnsupportedOperationException("CRS not implemented yet");
		}

		public Extent getValidArea() {
			throw new UnsupportedOperationException("CRS not implemented yet");
		}

		public Collection getAlias() {
			throw new UnsupportedOperationException("CRS not implemented yet");
		}

		public Set getIdentifiers() {
			throw new UnsupportedOperationException("CRS not implemented yet");
		}

		public Identifier getName() {
			throw new UnsupportedOperationException("CRS not implemented yet");
		}

		public InternationalString getRemarks() {
			throw new UnsupportedOperationException("CRS not implemented yet");
		}

		public String toWKT() throws UnsupportedOperationException {
			throw new UnsupportedOperationException("CRS not implemented yet");
		}
	}

	public static CoordinateReferenceSystem singleton = new OGCRS();
}