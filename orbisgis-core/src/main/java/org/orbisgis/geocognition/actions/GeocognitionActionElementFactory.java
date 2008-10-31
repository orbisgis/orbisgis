package org.orbisgis.geocognition.actions;

import org.orbisgis.PersistenceException;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.persistence.PropertySet;

public class GeocognitionActionElementFactory implements
		GeocognitionElementFactory {

	public static final String ACTION_ID = "org.orbisgis.geocognition.Action";

	@Override
	public boolean acceptContentTypeId(String typeId) {
		return ACTION_ID.equals(typeId);
	}

	@Override
	public boolean accepts(Object object) {
		return object instanceof ActionCode;
	}

	@Override
	public GeocognitionExtensionElement createElementFromXML(Object jaxbObject,
			String contentTypeId) throws PersistenceException {
		return new GeocognitionActionElement((PropertySet) jaxbObject, this);
	}

	@Override
	public GeocognitionExtensionElement createGeocognitionElement(Object object) {
		return new GeocognitionActionElement((ActionCode) object, this);
	}

	@Override
	public String getJAXBContextPath() {
		return null;
	}

}
