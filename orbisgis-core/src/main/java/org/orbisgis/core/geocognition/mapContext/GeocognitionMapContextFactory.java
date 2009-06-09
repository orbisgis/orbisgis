package org.orbisgis.core.geocognition.mapContext;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.core.layerModel.MapContext;

public class GeocognitionMapContextFactory implements
		GeocognitionElementFactory {

	static final String ID = "org.orbisgis.core.geocognition.MapContext";

	@Override
	public String getJAXBContextPath() {
		String legendsPath = new GeocognitionLegendFactory()
				.getJAXBContextPath();
		String mapPath = org.orbisgis.core.layerModel.persistence.MapContext.class.getName();
		mapPath = mapPath.substring(0, mapPath.lastIndexOf('.'));
		if (legendsPath != null) {
			mapPath += ":" + legendsPath;
		}
		return mapPath;
	}

	@Override
	public GeocognitionExtensionElement createElementFromXML(Object xmlObject,
			String contentTypeId) throws PersistenceException {
		return new GeocognitionMapContext(xmlObject, this);
	}

	@Override
	public GeocognitionExtensionElement createGeocognitionElement(Object object) {
		GeocognitionMapContext ret = new GeocognitionMapContext(
				(MapContext) object, this);
		return ret;
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof MapContext;
	}

	@Override
	public boolean acceptContentTypeId(String typeId) {
		return ID.equals(typeId);
	}

}
