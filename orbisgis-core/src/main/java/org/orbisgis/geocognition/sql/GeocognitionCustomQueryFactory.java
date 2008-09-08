package org.orbisgis.geocognition.sql;

import org.gdms.sql.customQuery.CustomQuery;
import org.orbisgis.PersistenceException;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.persistence.Property;
import org.orbisgis.geocognition.persistence.PropertySet;

public class GeocognitionCustomQueryFactory implements
		GeocognitionElementFactory {

	public static final String BUILT_IN_QUERY_ID = "org.orbisgis.geocognition.BuiltInCustomQuery";
	public static final String JAVA_QUERY_ID = "org.orbisgis.geocognition.JavaCustomQuery";

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
			} else if (prop.getName().equals(
					GeocognitionJavaCustomQuery.PERSISTENCE_PROPERTY_NAME)) {
				return new GeocognitionJavaCustomQuery(propertySet, this);
			} else {
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
		} else if (object instanceof CustomQueryJavaCode) {
			return new GeocognitionJavaCustomQuery((Code) object, this);
		} else {
			throw new RuntimeException("bug!");
		}
	}

	@Override
	public boolean accepts(Object o) {
		if (o instanceof Class) {
			Class<?> c = (Class<?>) o;
			return CustomQuery.class.isAssignableFrom(c);
		} else {
			return o instanceof CustomQueryJavaCode;
		}
	}

	@Override
	public boolean acceptContentTypeId(String typeId) {
		return BUILT_IN_QUERY_ID.equals(typeId) || JAVA_QUERY_ID.equals(typeId);
	}

}
