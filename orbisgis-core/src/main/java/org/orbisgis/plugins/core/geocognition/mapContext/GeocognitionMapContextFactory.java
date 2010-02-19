package org.orbisgis.plugins.core.geocognition.mapContext;

import org.orbisgis.plugins.core.PersistenceException;
import org.orbisgis.plugins.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.plugins.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.plugins.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.plugins.core.layerModel.MapContext;

public class GeocognitionMapContextFactory implements
		GeocognitionElementFactory {

	static final String ID = "org.orbisgis.plugins.core.geocognition.MapContext";

	@Override
	public String getJAXBContextPath() {
		String legendsPath = new GeocognitionLegendFactory()
				.getJAXBContextPath();
		String mapPath = org.orbisgis.plugins.core.layerModel.persistence.MapContext.class
				.getName();
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
