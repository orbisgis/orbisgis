package org.orbisgis.geocognition.mapContext;

import org.orbisgis.PersistenceException;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.layerModel.MapContext;

public class GeocognitionMapContextFactory implements
		GeocognitionElementFactory {

	static final String ID = "org.orbisgis.geocognition.MapContext";

	@Override
	public String getJAXBContextPath() {
		String legendsPath = new GeocognitionLegendFactory()
				.getJAXBContextPath();
		String mapPath = org.orbisgis.layerModel.persistence.MapContext.class.getName();
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
