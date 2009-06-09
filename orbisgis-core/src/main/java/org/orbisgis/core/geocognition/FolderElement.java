package org.orbisgis.core.geocognition;

import java.util.ArrayList;

import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.IProgressMonitor;

class FolderElement extends AbstractGeocognitionElement implements
		GeocognitionElement {

	private String id;
	private ArrayList<GeocognitionElement> elements = new ArrayList<GeocognitionElement>();
	private DefaultGeocognition geocognition;

	FolderElement(DefaultGeocognition geocognition) {
		this.geocognition = geocognition;
	}

	public int getElementCount() {
		return elements.size();
	}

	public GeocognitionElement getElement(int i) {
		return elements.get(i);
	}

	@Override
	public void addElement(GeocognitionElement element) {
		addElement(element, true);
	}

	public void addElement(GeocognitionElement element, boolean fireEvents) {
		if (getElement(element.getId()) != null) {
			throw new UnsupportedOperationException("There is already "
					+ "an element with that id: " + element.getId());
		}
		elements.add(element);
		((AbstractGeocognitionElement) element).setParent(this);
		if (fireEvents) {
			geocognition.fireElementAdded(this, element);
		}
	}

	@Override
	public boolean isFolder() {
		return true;
	}

	@Override
	public Object getObject() {
		throw new UnsupportedOperationException("Folders do not wrap objects");
	}

	@Override
	public Object getJAXBObject() {
		throw new RuntimeException("bug");
	}

	@Override
	public String getTypeId() {
		return "org.orbisgis.core.geocognition.Folder";
	}

	@Override
	public boolean removeElement(GeocognitionElement element) {
		return removeElement(element, true);
	}

	public boolean removeElement(GeocognitionElement element, boolean fireEvents) {
		boolean allowRemove;
		if (fireEvents) {
			allowRemove = geocognition.fireElementRemoving(element);
		} else {
			allowRemove = true;
		}
		if (allowRemove) {
			((AbstractGeocognitionElement) element).setParent(null);
			elements.remove(element);
			if (fireEvents) {
				geocognition.fireElementRemoved(element);
			}
			((AbstractGeocognitionElement) element).elementRemoved(this);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void elementRemoved(GeocognitionElement oldParent) {
		for (GeocognitionElement child : elements) {
			((AbstractGeocognitionElement) child).elementRemoved(this);
		}
	}

	@Override
	public void close(IProgressMonitor progressMonitor) {
		unsupported();
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

	private void unsupported() {
		throw new UnsupportedOperationException("Folders cannot be edited");
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public GeocognitionElement getElement(String id) {
		for (GeocognitionElement element : elements) {
			if (element.getId().equals(id)) {
				return element;
			}
		}
		return null;
	}

	@Override
	public GeocognitionElementFactory getFactory() {
		return null;
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public String getXMLContent() {
		throw new UnsupportedOperationException(
				"Cannot extract XML from a folder");
	}

	@Override
	public void setXMLContent(String xml) {
		throw new UnsupportedOperationException(
				"Cannot extract XML from a folder");
	}

	@Override
	public boolean removeElement(String elementId) {
		GeocognitionElement elem = getElement(elementId);
		if (elem != null) {
			return removeElement(elem);
		} else {
			return false;
		}
	}

	@Override
	public GeocognitionElement cloneElement() throws GeocognitionException {
		FolderElement ret = new FolderElement(geocognition);
		ret.setId(getId());
		for (int i = 0; i < getElementCount(); i++) {
			ret.addElement(getElement(i).cloneElement());
		}
		return ret;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		fireIdChanged();
	}
}
