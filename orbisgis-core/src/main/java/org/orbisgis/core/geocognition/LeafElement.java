package org.orbisgis.core.geocognition;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.IProgressMonitor;

public final class LeafElement extends AbstractGeocognitionElement implements
		GeocognitionElement {

	private String fixedId;
	private String id;
	private Boolean modified = null;
	private GeocognitionExtensionElement element;
	private ContentListener contentListener;

	public LeafElement(GeocognitionExtensionElement element) {
		this.element = element;
		fixedId = element.getFixedId();
		if (fixedId != null) {
			this.id = fixedId;
		}
		contentListener = new ContentListener();
	}

	@Override
	public void setId(String id) throws IllegalArgumentException {
		if (fixedId != null) {
			if (!id.toLowerCase().equals(fixedId.toLowerCase())) {
				throw new IllegalArgumentException("This element cannot "
						+ "have an id different than " + fixedId);
			} else {
				try {
					element.idChanged(id);
					this.id = id;
					fireIdChanged();
				} catch (GeocognitionException e) {
					throw new IllegalArgumentException("Cannot change id", e);
				}
			}
		} else {
			try {
				GeocognitionElement parent = getParent();
				if (parent != null) {
					for (int i = 0; i < parent.getElementCount(); i++) {
						GeocognitionElement child = parent.getElement(i);
						if (child != this) {
							if (child.getId().toLowerCase().equals(
									id.toLowerCase())) {
								throw new GeocognitionException(
										"There is already "
												+ "an element with the id:"
												+ id);
							}
						}
					}
				}
				element.idChanged(id);
				this.id = id;
				fireIdChanged();
			} catch (GeocognitionException e) {
				throw new IllegalArgumentException("Cannot change id", e);
			}
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void addElement(GeocognitionElement element) {
		throw new UnsupportedOperationException(
				"This element cannot have children");
	}

	@Override
	public boolean removeElement(GeocognitionElement element) {
		throw new UnsupportedOperationException(
				"This element cannot have children");
	}

	@Override
	public void elementRemoved(GeocognitionElement oldPatent) {
		element.elementRemoved();
	}

	@Override
	public GeocognitionElement getElement(int i) {
		throw new UnsupportedOperationException(
				"This element cannot have children");
	}

	@Override
	public GeocognitionElement getElement(String id) {
		throw new UnsupportedOperationException(
				"This element cannot have children");
	}

	@Override
	public int getElementCount() {
		throw new UnsupportedOperationException(
				"This element cannot have children");
	}

	@Override
	public boolean removeElement(String elementId) {
		throw new UnsupportedOperationException(
				"This element cannot have children");
	}

	@Override
	public boolean isFolder() {
		return false;
	}

	@Override
	public boolean isModified() {
		if (modified == null) {
			if (element.isModified()) {
				modified = true;
			} else {
				try {
					Object obj = getJAXBObject();
					String contextPath = getFactory().getJAXBContextPath();
					if (contextPath == null) {
						contextPath = DefaultGeocognition
								.getCognitionContextPath();
					}
					JAXBContext context = JAXBContext.newInstance(contextPath,
							this.getClass().getClassLoader());
					Marshaller marshaller = context.createMarshaller();
					ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
					marshaller.marshal(obj, bos1);
					ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
					marshaller.marshal(element.getRevertJAXBObject(), bos2);
					modified = !new String(bos1.toByteArray())
							.equals(new String(bos2.toByteArray()));
				} catch (JAXBException e) {
					modified = true;
				}
			}
		}

		return modified;
	}

	@Override
	public void save() throws UnsupportedOperationException,
			EditableElementException {
		modified = null;
		EditableElementException problem = null;
		try {
			element.save();
		} catch (EditableElementException e) {
			problem = e;
		}
		fireSave();
		if (problem != null) {
			throw problem;
		}
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		modified = null;
		element.setElementListener(contentListener);
		element.open(progressMonitor);
	}

	@Override
	public void close(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		element.close(progressMonitor);
		element.setElementListener(null);
	}

	private final class ContentListener implements
			GeocognitionElementContentListener {
		@Override
		public void contentChanged() {
			modified = null;
			fireContentChanged();
		}
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
	public String toString() {
		return getId();
	}

	@Override
	public String getXMLContent() throws GeocognitionException {
		try {
			return DefaultGeocognition.getXML(getJAXBObject(), getTypeId());
		} catch (JAXBException e) {
			throw new GeocognitionException(
					"Cannot get the xml of the element", e);
		}
	}

	@Override
	public void setXMLContent(String xml) throws GeocognitionException {
		try {
			Object xmlObject = DefaultGeocognition.getValidXMLObject(this, xml);
			element.setJAXBObject(xmlObject);
		} catch (IllegalArgumentException e) {
			throw new GeocognitionException(
					"Cannot set the xml of the element", e);
		} catch (JAXBException e) {
			throw new GeocognitionException(
					"Cannot set the xml of the element", e);
		} catch (GeocognitionException e) {
			throw new GeocognitionException(
					"Cannot set the xml of the element", e);
		}
	}

	@Override
	public GeocognitionElement cloneElement() throws GeocognitionException {
		LeafElement cloned;
		try {
			cloned = new LeafElement(getFactory().createElementFromXML(
					element.getJAXBObject(), element.getTypeId()));
			cloned.setId(getId());
		} catch (PersistenceException e) {
			throw new GeocognitionException(e.getMessage(), e);
		}
		return cloned;
	}

	@Override
	public Map<String, String> getProperties() {
		Map<String, String> ret = element.getProperties();
		if (ret == null) {
			return super.getProperties();
		} else {
			return ret;
		}
	}
}
