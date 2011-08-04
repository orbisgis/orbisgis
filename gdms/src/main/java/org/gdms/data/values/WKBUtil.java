/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.values;

import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

final class WKBUtil {

	private static volatile WKBWriter writer3D = null;
	private static volatile WKBWriter writer2D = null;

	private static volatile WKTWriter textWriter3D = null;
	private static volatile WKTWriter textWriter2D = null;

	private static volatile WKBReader wkbReader = null;
	private static volatile WKTReader wktReader = null;

	public static WKBReader getWKBReaderInstance() {
		if (wkbReader == null) {
			wkbReader = new WKBReader();
		}
		return wkbReader;
	}

	public static WKBWriter getWKBWriter2DInstance() {
		if (writer2D == null) {
			writer2D = new WKBWriter();
		}
		return writer2D;
	}

	public static WKBWriter getWKBWriter3DInstance() {
		if (writer3D == null) {
			writer3D = new WKBWriter(3);
		}
		return writer3D;
	}

	public static WKTReader getWKTReaderInstance() {
		if (wktReader == null) {
			wktReader = new WKTReader();
		}
		return wktReader;
	}

	public static WKTWriter getTextWKTWriter2DInstance() {
		if (textWriter2D == null) {
			textWriter2D = new WKTWriter();
		}
		return textWriter2D;
	}

	public static WKTWriter getTextWKTWriter3DInstance() {
		if (textWriter3D == null) {
			textWriter3D = new WKTWriter(3);
		}
		return textWriter3D;
	}

        private WKBUtil() {
        }

}