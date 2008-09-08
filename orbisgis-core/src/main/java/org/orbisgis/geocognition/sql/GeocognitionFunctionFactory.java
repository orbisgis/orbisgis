package org.orbisgis.geocognition.sql;

import org.gdms.sql.function.Function;
import org.orbisgis.PersistenceException;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.persistence.Property;
import org.orbisgis.geocognition.persistence.PropertySet;

public class GeocognitionFunctionFactory implements GeocognitionElementFactory {

	public static final String BUILT_IN_FUNCTION_ID = "org.orbisgis.geocognition.BuiltInFunction";
	public static final String JAVA_FUNCTION_ID = "org.orbisgis.geocognition.JavaFunction";

	@Override
	public GeocognitionExtensionElement createElementFromXML(Object jaxbObject,
			String contentTypeId) throws PersistenceException {
		try {
			PropertySet propertySet = (PropertySet) jaxbObject;
			Property prop = propertySet.getProperty().get(0);
			if (prop.getName().equals(GeocognitionBuiltInFunction.PERSISTENCE_PROPERTY_NAME)) {
				return new GeocognitionBuiltInFunction(propertySet, this);
			} else if (prop.getName().equals(
					GeocognitionJavaFunction.PERSISTENCE_PROPERTY_NAME)) {
				return new GeocognitionJavaFunction(propertySet, this);
			} else {
				throw new RuntimeException("bug!");
			}
		} catch (ClassNotFoundException e) {
			throw new PersistenceException("Cannot instantiate function", e);
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
			return new GeocognitionBuiltInFunction((Class<? extends Function>) object,
					this);
		} else if (object instanceof FunctionJavaCode) {
			return new GeocognitionJavaFunction((Code) object, this);
		} else {
			throw new RuntimeException("bug!");
		}
	}

	@Override
	public boolean accepts(Object o) {
		if (o instanceof Class) {
			Class<?> c = (Class<?>) o;
			return Function.class.isAssignableFrom(c);
		} else {
			return o instanceof FunctionJavaCode;
		}
	}

	@Override
	public boolean acceptContentTypeId(String typeId) {
		return BUILT_IN_FUNCTION_ID.equals(typeId)
				|| JAVA_FUNCTION_ID.equals(typeId);
	}

}
