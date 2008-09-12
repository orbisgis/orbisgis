package org.orbisgis.views.geocognition.sync;

import java.util.ArrayList;
import java.util.Map;

import org.orbisgis.Services;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionElementListener;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.IProgressMonitor;

public class GeocognitionElementDecorator implements GeocognitionElement {
	private GeocognitionElement element;
	private ArrayList<GeocognitionElementDecorator> children;
	private ArrayList<IdPath> filterPaths;

	/**
	 * Creates a new GeocognitionElementDecorator
	 * 
	 * @param e
	 *            the element to decorate
	 */
	public GeocognitionElementDecorator(GeocognitionElement e,
			ArrayList<IdPath> filt) {
		if (e == null) {
			Services.getErrorManager().error(
					"bug!",
					new IllegalArgumentException(
							"Cannot decorate a null element"));
		}
		filterPaths = filt;
		element = e;
		children = new ArrayList<GeocognitionElementDecorator>();
		if (element.isFolder()) {
			for (int i = 0; i < element.getElementCount(); i++) {
				GeocognitionElementDecorator child = filter(new GeocognitionElementDecorator(
						element.getElement(i), filt));
				if (child != null) {
					children.add(child);
				}
			}
		}
	}

	@Override
	public void addElement(GeocognitionElement element)
			throws UnsupportedOperationException {
		GeocognitionElementDecorator e = (GeocognitionElementDecorator) element;
		if (filter(e) != null) {
			this.element.addElement(e.element);
			children.add(e);
		}
	}

	@Override
	public void addElementListener(GeocognitionElementListener listener) {
		element.addElementListener(listener);
	}

	@Override
	public boolean removeElementListener(GeocognitionElementListener listener) {
		return element.removeElementListener(listener);
	}

	@Override
	public GeocognitionElementDecorator getElement(int i)
			throws UnsupportedOperationException {
		return filter(getElement(element.getElement(i).getId()));
	}

	private GeocognitionElementDecorator filter(
			GeocognitionElementDecorator elem) {
		if (filterPaths == null || elem == null) {
			return elem;
		} else {
			IdPath childPath = new IdPath(elem.getIdPath());
			boolean show = false;
			for (IdPath idPath : filterPaths) {
				if (childPath.startsWith(idPath)) {
					show = true;
					break;
				}
			}

			return show ? elem : null;
		}
	}

	@Override
	public GeocognitionElementDecorator getElement(String id) {
		GeocognitionElementDecorator elem = null;
		for (GeocognitionElementDecorator dec : children) {
			if (dec.getId().equals(id)) {
				return dec;
			}
		}

		return filter(elem);
	}

	@Override
	public int getElementCount() throws UnsupportedOperationException {
		return children.size();
	}

	@Override
	public String getId() {
		return element.getId();
	}

	@Override
	public String getIdPath() {
		return element.getIdPath();
	}

	@Override
	public GeocognitionElement getParent() {
		return element.getParent();
	}

	@Override
	public String getXMLContent() throws GeocognitionException {
		return element.getXMLContent().replaceAll(">", ">\n").trim();
	}

	@Override
	public boolean isFolder() {
		return element.isFolder();
	}

	@Override
	public boolean isModified() {
		return element.isModified();
	}

	@Override
	public boolean removeElement(GeocognitionElement element) {
		return removeElement(element.getId());
	}

	@Override
	public boolean removeElement(String elementId) {
		GeocognitionElementDecorator remove = null;
		for (GeocognitionElementDecorator dec : children) {
			if (dec.getId().equals(elementId)) {
				remove = dec;
				children.remove(dec);
				break;
			}
		}

		if (remove != null) {
			boolean isRemoved = element.removeElement(remove.element);
			return isRemoved;
		} else {
			return false;
		}
	}

	@Override
	public void setId(String id) throws IllegalArgumentException {
		element.setId(id);
	}

	@Override
	public void setXMLContent(String xml) throws GeocognitionException {
		element.setXMLContent(xml.replaceAll(">\n", ">"));
	}

	@Override
	public void close(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
		element.close(progressMonitor);
	}

	@Override
	public GeocognitionElementFactory getFactory() {
		return element.getFactory();
	}

	@Override
	public Object getJAXBObject() {
		return element.getJAXBObject();
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return element.getObject();
	}

	@Override
	public String getTypeId() {
		return element.getTypeId();
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, GeocognitionException {
		element.open(progressMonitor);
	}

	@Override
	public void save() throws UnsupportedOperationException,
			GeocognitionException {
		element.save();
	}

	@Override
	public String toString() {
		return element.getId();
	}

	@Override
	public GeocognitionElementDecorator cloneElement()
			throws GeocognitionException {
		return new GeocognitionElementDecorator(element.cloneElement(),
				filterPaths);
	}

	/**
	 * Gets the element decorated by this decorator
	 * 
	 * @return the wrapped element
	 */
	public GeocognitionElement getDecoratedElement() {
		return element;
	}

	@Override
	public Map<String, String> getProperties() {
		return element.getProperties();
	}
}
