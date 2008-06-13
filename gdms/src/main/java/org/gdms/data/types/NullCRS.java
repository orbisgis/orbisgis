/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.types;

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