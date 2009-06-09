package org.orbisgis.core.geocognition.sql;

import org.orbisgis.core.geocognition.AbstractExtensionElement;
import org.orbisgis.core.geocognition.GeocognitionElementContentListener;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.core.geocognition.persistence.Property;
import org.orbisgis.core.geocognition.persistence.PropertySet;
import org.orbisgis.progress.IProgressMonitor;

public abstract class AbstractBuiltInSQLArtifact extends
		AbstractExtensionElement implements GeocognitionExtensionElement {
	
	static final String PERSISTENCE_PROPERTY_NAME = "class-name";
	public static final String REGISTERED = "REGISTERED";
	public static final String IS_REGISTERED = "IS_REGISTERED";
	public static final String IS_NOT_REGISTERED = "IS_NOT_REGISTERED";
	protected Class<?> class_;

	public AbstractBuiltInSQLArtifact(Class<?> class_,
			GeocognitionElementFactory factory) {
		super(factory);
		this.class_ = class_;
	}

	public AbstractBuiltInSQLArtifact(PropertySet properties,
			GeocognitionElementFactory factory) throws ClassNotFoundException {
		super(factory);
		String className = properties.getProperty().get(0).getValue();
		class_ = Class.forName(className);
	}

	@Override
	public Object getJAXBObject() {
		PropertySet props = new PropertySet();
		Property property = new Property();
		property.setName(PERSISTENCE_PROPERTY_NAME);
		property.setValue(class_.getName());
		props.getProperty().add(property);
		return props;
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return class_;
	}

	@Override
	public void close(IProgressMonitor progressMonitor) {
		unsupported();
	}

	private void unsupported() {
		throw new UnsupportedOperationException(
				"Built-in sql artifacts cannot be edited");
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
		unsupported();
	}

	@Override
	public void save() {
		unsupported();
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public Object getRevertJAXBObject() {
		return null;
	}

	@Override
	public void setElementListener(GeocognitionElementContentListener listener) {
	}

	@Override
	public void setJAXBObject(Object jaxbObject)
			throws IllegalArgumentException, GeocognitionException {
		unsupported();
	}
}
