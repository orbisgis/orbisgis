package org.orbisgis.core.geocognition.sql;

import org.gdms.sql.customQuery.CustomQuery;
import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.core.geocognition.persistence.Property;
import org.orbisgis.core.geocognition.persistence.PropertySet;

public class GeocognitionCustomQueryFactory implements
		GeocognitionElementFactory {

	public static final String BUILT_IN_QUERY_ID = "org.orbisgis.core.geocognition.BuiltInCustomQuery";

	@Override
	public GeocognitionExtensionElement createElementFromXML(Object jaxbObject,
			String contentTypeId) throws PersistenceException {
		try {
			PropertySet propertySet = (PropertySet) jaxbObject;
			Property prop = propertySet.getProperty().get(0);
			if (prop.getName().equals(
					GeocognitionBuiltInCustomQuery.PERSISTENCE_PROPERTY_NAME)) {
				return new GeocognitionBuiltInCustomQuery(
						(PropertySet) jaxbObject, this);
			}  else {
				throw new RuntimeException("bug!");
			}
		} catch (ClassNotFoundException e) {
			throw new PersistenceException("Cannot instantiate custom query", e);
		}
	}

	@Override
	public String getJAXBContextPath() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GeocognitionExtensionElement createGeocognitionElement(Object object) {
		if (object instanceof Class<?>) {
			return new GeocognitionBuiltInCustomQuery(
					(Class<? extends CustomQuery>) object, this);
		}  else {
			throw new RuntimeException("bug!");
		}
	}

	@Override
	public boolean accepts(Object o) {
		if (o instanceof Class) {
			Class<?> c = (Class<?>) o;
			return CustomQuery.class.isAssignableFrom(c);
		}
		return false;
	}

	@Override
	public boolean acceptContentTypeId(String typeId) {
		return BUILT_IN_QUERY_ID.equals(typeId);
	}

}
