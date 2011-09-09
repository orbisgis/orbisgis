package org.orbisgis.core.geocognition;

import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.IProgressMonitor;

public class UnsupportedExtensionElement extends AbstractExtensionElement
		implements GeocognitionExtensionElement {

	private Object jaxbObject;
	private String typeId;

	public UnsupportedExtensionElement(Object jaxbObject, String typeId) {
		super(null);
		this.typeId = typeId;
		this.jaxbObject = jaxbObject;
	}

	@Override
	public Object getRevertJAXBObject() {
		return jaxbObject;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void setElementListener(GeocognitionElementContentListener listener) {
	}

	@Override
	public void setJAXBObject(Object jaxbObject)
			throws IllegalArgumentException, GeocognitionException {
		this.jaxbObject = jaxbObject;
	}

	@Override
	public void close(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
	}

	@Override
	public Object getJAXBObject() {
		return jaxbObject;
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return null;
	}

	@Override
	public String getTypeId() {
		return typeId;
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
	}

	@Override
	public void save() throws UnsupportedOperationException,
			EditableElementException {
	}

}
