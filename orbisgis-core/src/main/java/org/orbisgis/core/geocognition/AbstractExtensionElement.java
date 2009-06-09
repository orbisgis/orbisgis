package org.orbisgis.core.geocognition;

import java.util.Map;

import org.orbisgis.core.geocognition.mapContext.GeocognitionException;

public abstract class AbstractExtensionElement implements
		GeocognitionExtensionElement {

	private GeocognitionElementFactory factory;

	public AbstractExtensionElement(GeocognitionElementFactory factory) {
		this.factory = factory;
	}

	@Override
	public GeocognitionElementFactory getFactory() {
		return factory;
	}

	@Override
	public void idChanged(String newId) throws GeocognitionException {
	}
	
	@Override
	public void elementRemoved() {
	}
	
	@Override
	public String getFixedId() {
		return null;
	}
	
	@Override
	public Map<String, String> getProperties() {
		return null;
	}
}
