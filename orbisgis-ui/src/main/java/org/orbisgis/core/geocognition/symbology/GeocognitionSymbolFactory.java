/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.geocognition.symbology;

import org.orbisgis.core.OrbisGISPersitenceConfig;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolList;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolType;

public class GeocognitionSymbolFactory implements GeocognitionElementFactory {

	@Override
	public String getJAXBContextPath() {
		String path = SymbolType.class.getName();
		path = path.substring(0, path.lastIndexOf('.'));

		return path;
	}

	@Override
	public GeocognitionExtensionElement createElementFromXML(Object xmlObject,
			String contentTypeId) {
		return new GeocognitionSymbol((SymbolList) xmlObject, this);
	}

	@Override
	public GeocognitionExtensionElement createGeocognitionElement(Object object) {
		return new GeocognitionSymbol((Symbol) object, this);
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof Symbol;
	}

	@Override
	public boolean acceptContentTypeId(String typeId) {
		return OrbisGISPersitenceConfig.GEOCOGNITION_SYMBOL_FACTORY_ID
				.equals(typeId);
	}
}
