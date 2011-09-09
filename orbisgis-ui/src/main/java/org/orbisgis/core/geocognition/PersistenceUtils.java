package org.orbisgis.core.geocognition;

import org.orbisgis.core.geocognition.persistence.Property;
import org.orbisgis.core.geocognition.persistence.PropertySet;

public class PersistenceUtils {

	public static Property newProperty(String name, String value) {
		Property property = new Property();
		property.setName(name);
		property.setValue(value);
		return property;
	}

	public static PropertySet newPropertySet(Property... properties) {
		PropertySet props = new PropertySet();
		for (Property property : properties) {
			props.getProperty().add(property);
		}
		return props;
	}

}
