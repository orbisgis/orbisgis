package org.orbisgis.geocognition.symbology;

import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.collection.persistence.SymbolList;
import org.orbisgis.renderer.symbol.collection.persistence.SymbolType;

public class GeocognitionSymbolFactory implements GeocognitionElementFactory {

	static final String ID = "org.orbisgis.geocognition.Symbol";

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
		return ID.equals(typeId);
	}
}
