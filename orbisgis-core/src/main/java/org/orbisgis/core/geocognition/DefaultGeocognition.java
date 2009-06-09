package org.orbisgis.core.geocognition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.core.geocognition.persistence.GeocognitionNode;
import org.orbisgis.core.geocognition.persistence.NodeContent;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

public class DefaultGeocognition implements Geocognition {

	private static Logger logger = Logger.getLogger(DefaultGeocognition.class);

	private static ArrayList<GeocognitionElementFactory> factories = new ArrayList<GeocognitionElementFactory>();
	private static JAXBContext jaxbContext;
	private FolderElement root;
	private ArrayList<GeocognitionListener> listeners = new ArrayList<GeocognitionListener>();

	public static void clearFactories() {
		factories.clear();
		jaxbContext = null;
	}

	public DefaultGeocognition() {
		this.root = new FolderElement(this);
		(this).root.setId("");
	}

	@Override
	public void clear() {
		for (int i = root.getElementCount() - 1; i >= 0; i--) {
			removeElement(root, root.getElement(i));
		}
	}

	@Override
	public GeocognitionElement createTree(InputStream is)
			throws PersistenceException {
		FolderElement fe = new FolderElement(this);
		readIn(fe, is);
		return fe;
	}

	@Override
	public void read(InputStream is) throws PersistenceException {
		clear();
		readIn(root, is);
		root.setId("");
	}

	private void readIn(GeocognitionElement rootElement, InputStream is)
			throws PersistenceException {
		try {
			byte[] buffer = new byte[8 * 1024];
			byte[] all = new byte[0];
			int count;
			while ((count = is.read(buffer)) != -1) {
				byte[] aux = new byte[all.length + count];
				System.arraycopy(all, 0, aux, 0, all.length);
				System.arraycopy(buffer, 0, aux, all.length, count);
				all = aux;
			}
			is.close();
			is = new ByteArrayInputStream(all);
			GeocognitionNode xmlRoot = unMarshall(is);
			if (xmlRoot.getVersion() == null) {
				try {
					is = new ByteArrayInputStream(all);
					xmlRoot = upgradeNonVersioned(is);
				} catch (TransformerException e) {
					Services.getService(ErrorManager.class).error(
							"Cannot upgrade geocognition to new version", e);
				}
			}
			GeocognitionElement readRoot = fromXML(xmlRoot);
			for (int i = 0; i < readRoot.getElementCount(); i++) {
				rootElement.addElement(readRoot.getElement(i));
			}
			rootElement.setId(readRoot.getId());
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot read geocognition", e);
		} catch (IOException e) {
			throw new PersistenceException("Cannot read geocognition file", e);
		}
	}

	private GeocognitionNode unMarshall(InputStream is) throws JAXBException {
		JAXBContext jc = getJAXBContext();
		GeocognitionNode xmlRoot = (GeocognitionNode) jc.createUnmarshaller()
				.unmarshal(is);
		return xmlRoot;
	}

	private GeocognitionNode upgradeNonVersioned(InputStream is)
			throws TransformerException, JAXBException {
		logger.debug("Non versioned geocognition. Upgrading...");
		TransformerFactory xformFactory = TransformerFactory.newInstance();
		Transformer transformer = xformFactory.newTransformer(new StreamSource(
				this.getClass().getResourceAsStream(
						"/org/orbisgis/core/geocognition/Unversioned.xsl")));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(bos);
		transformer.transform(new StreamSource(is), result);

		GeocognitionNode xmlRoot = unMarshall(new ByteArrayInputStream(bos
				.toByteArray()));
		return xmlRoot;
	}

	private static JAXBContext getJAXBContext() throws JAXBException {
		if (jaxbContext == null) {
			String classpath = getCognitionContextPath();
			for (GeocognitionElementFactory factory : factories) {
				String contextPath = factory.getJAXBContextPath();
				if (contextPath != null) {
					classpath += ":" + contextPath;
				}
			}
			jaxbContext = JAXBContext.newInstance(classpath,
					DefaultGeocognition.class.getClassLoader());
		}
		return jaxbContext;
	}

	private static GeocognitionElementFactory getFactoryByType(
			String contentType) {
		for (GeocognitionElementFactory factory : factories) {
			if (factory.acceptContentTypeId(contentType)) {
				return factory;
			}
		}

		return null;
	}

	private GeocognitionElement fromXML(GeocognitionNode xmlRoot) {
		GeocognitionElement ret = null;
		NodeContent nodeContent = xmlRoot.getNodeContent();
		if (nodeContent == null) {
			ret = new FolderElement(this);
		} else {
			Object xmlObject = nodeContent.getAny();
			String typeId = nodeContent.getContentTypeId();
			GeocognitionElementFactory factory = getFactoryByType(typeId);
			ret = new LeafElement(new UnsupportedExtensionElement(xmlObject,
					typeId));
			if (factory == null) {
				Services.getErrorManager().warning(
						"Non recognized geocognition element: "
								+ xmlRoot.getId() + ": " + typeId
								+ ". Element won't be available.");
			} else {
				try {
					ret = new LeafElement(factory.createElementFromXML(
							xmlObject, typeId));
				} catch (PersistenceException e) {
					Services.getErrorManager().warning(
							"Cannot restore element: " + xmlRoot.getId() + ": "
									+ typeId + ". Element won't be available.", e);
				} catch (RuntimeException e) {
					Services.getErrorManager().warning(
							"Non recognized geocognition content: "
									+ xmlRoot.getId() + ": " + typeId
									+ ". Element won't be available.", e);
				}
			}
		}
		ret.setId(xmlRoot.getId());

		if (ret.isFolder()) {
			FolderElement retFE = (FolderElement) ret;
			List<GeocognitionNode> nodes = xmlRoot.getGeocognitionNode();
			for (GeocognitionNode geoNode : nodes) {
				GeocognitionElement child = fromXML(geoNode);
				retFE.addElement(child);
			}
		}

		return ret;
	}

	private GeocognitionNode toXML(GeocognitionElement element) {
		GeocognitionNode ret = new GeocognitionNode();
		ret.setId(element.getId());
		if (element instanceof FolderElement) {
			FolderElement fe = (FolderElement) element;
			for (int i = 0; i < fe.getElementCount(); i++) {
				ret.getGeocognitionNode().add(toXML(fe.getElement(i)));
			}
		} else {
			NodeContent nodeContent = new NodeContent();
			nodeContent.setAny(element.getJAXBObject());
			nodeContent.setContentTypeId(element.getTypeId());
			ret.setNodeContent(nodeContent);
		}
		return ret;
	}

	@Override
	public void write(OutputStream bos) throws PersistenceException {
		GeocognitionElement element = (this).root;
		write(element, bos);
	}

	/**
	 * Sets the specified xml content to the specified element. There is no need
	 * for the element to be in the geocognition
	 *
	 * @param element
	 * @param xml
	 * @throws JAXBException
	 *             If the xml cannot be read into an object
	 * @throws GeocognitionException
	 *             If the object read is not valid
	 */
	static Object getValidXMLObject(AbstractGeocognitionElement element,
			String xml) throws JAXBException, GeocognitionException {
		ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
		JAXBContext jc = getJAXBContext();
		NodeContent nodeContent = (NodeContent) jc.createUnmarshaller()
				.unmarshal(is);
		// Check the content is valid
		String contentTypeId = nodeContent.getContentTypeId();
		GeocognitionElementFactory factory = getFactoryByType(contentTypeId);
		Object jaxbObject = nodeContent.getAny();
		try {
			GeocognitionExtensionElement elem = factory.createElementFromXML(
					jaxbObject, contentTypeId);
			IProgressMonitor pm = new NullProgressMonitor();
			elem.open(pm);
			elem.close(pm);
		} catch (PersistenceException e) {
			throw new GeocognitionException(e);
		} catch (UnsupportedOperationException e) {
			throw new GeocognitionException(e);
		} catch (EditableElementException e) {
			throw new GeocognitionException(e);
		}

		// Return the content
		return jaxbObject;
	}

	static String getXML(Object jaxbObject, String contentTypeId)
			throws JAXBException {
		JAXBContext jc = getJAXBContext();
		NodeContent nc = new NodeContent();
		nc.setContentTypeId(contentTypeId);
		nc.setAny(jaxbObject);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		jc.createMarshaller().marshal(nc, os);
		return new String(os.toByteArray());
	}

	@Override
	public void write(GeocognitionElement element, OutputStream bos)
			throws PersistenceException {
		GeocognitionNode root = toXML(element);
		root.setVersion(1);
		try {
			JAXBContext jc = getJAXBContext();
			jc.createMarshaller().marshal(root, bos);
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot save geocognition", e);
		}
	}

	@Override
	public void write(OutputStream bos, String... ids)
			throws PersistenceException, IllegalArgumentException {
		GeocognitionElement container = new FolderElement(this);
		container.setId("");
		for (String elementId : ids) {
			Id rid = new Id(elementId);
			GeocognitionElement el = getElement(root, rid);
			if (el != null) {
				container.addElement(el);
				el = container;
			} else {
				throw new IllegalArgumentException("The path does not exist: "
						+ elementId);
			}
		}
		if ((container.getElementCount() == 1)
				&& (container.getElement(0).isFolder())) {
			container = container.getElement(0);
		}
		write(container, bos);
	}

	@Override
	public void addElement(String id, Object element) {
		GeocognitionElement newElement = null;
		if (element instanceof Folder) {
			newElement = new FolderElement(this);
		} else {
			GeocognitionElementFactory factory = getFactoryByObject(element);
			if (factory == null) {
				throw new IllegalArgumentException(
						"The type is not supported: " + element.getClass());
			}
			newElement = new LeafElement(factory
					.createGeocognitionElement(element));
		}
		addInElement(new Id(id), newElement);
	}

	@Override
	public void addGeocognitionElement(String id, GeocognitionElement element) {
		addInElement(new Id(id), element);
	}

	private static GeocognitionElementFactory getFactoryByObject(Object element) {
		for (GeocognitionElementFactory factory : factories) {
			if (factory.accepts(element)) {
				return factory;
			}
		}

		return null;
	}

	private void addInElement(Id id, GeocognitionElement newElement) {
		if (getElement(root, id) != null) {
			throw new IllegalArgumentException(
					"There is already an element with the id: " + id);
		}
		newElement.setId(id.getLast());
		GeocognitionElement parent = createNecessaryNodes(id);
		parent.addElement(newElement);
	}

	private GeocognitionElement createNecessaryNodes(Id id) {
		GeocognitionElement current = root;
		for (int i = 0; i < id.getLength() - 1; i++) {
			GeocognitionElement elem = current.getElement(id.getPart(i));
			if ((elem == null) && (!current.isFolder())) {
				throw new IllegalArgumentException(current.getId()
						+ " is not a folder");
			} else if ((elem == null) && (current.isFolder())) {
				FolderElement newFolder = new FolderElement(this);
				newFolder.setId(id.getPart(i));
				current.addElement(newFolder);
				current = newFolder;
			} else {
				current = elem;
			}
		}

		return current;
	}

	void fireElementAdded(GeocognitionElement parent,
			GeocognitionElement newElement) {
		for (GeocognitionListener listener : listeners) {
			listener.elementAdded((this), parent, newElement);
		}
	}

	private void fireElementMoved(GeocognitionElement element,
			GeocognitionElement oldParent) {
		for (GeocognitionListener listener : listeners) {
			listener.elementMoved((this), element, oldParent);
		}
	}

	@Override
	public void addElementFactory(GeocognitionElementFactory factory) {
		factories.add(factory);
		jaxbContext = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getElement(String id, Class<T> c) {
		if (root.getId().equals(id)) {
			return (T) root;
		} else {
			GeocognitionElement ret = getElement(root, new Id(id));
			if (ret == null) {
				return null;
			} else {
				Object object = ret.getObject();
				try {
					return (T) object;
				} catch (ClassCastException e) {
					throw new IllegalArgumentException(
							"Wrong type. The element " + "type is: "
									+ object.getClass());
				}
			}
		}
	}

	private GeocognitionElement getElement(GeocognitionElement root, Id id) {
		GeocognitionElement current = root;
		for (int i = 0; i < id.getLength(); i++) {
			boolean found = false;
			for (int j = 0; j < current.getElementCount(); j++) {
				GeocognitionElement element = current.getElement(j);
				if (element.getId().equals(id.getPart(i))) {
					current = element;
					found = true;
					break;
				}
			}
			if (!found) {
				return null;
			}
		}
		return current;
	}

	@Override
	public GeocognitionElement getRoot() {
		return root;
	}

	@Override
	public GeocognitionElement removeElement(String id) {
		Id ident = new Id(id);
		GeocognitionElement parent = getElement(root, ident.getParent());
		if (parent == null) {
			throw new IllegalArgumentException("The parent does not exist: "
					+ id);
		} else {
			GeocognitionElement element = parent.getElement(ident.getLast());

			if (element != null) {
				return removeElement(parent, element);
			} else {
				throw new IllegalArgumentException(
						"The element does not exist: " + id);
			}
		}
	}

	private GeocognitionElement removeElement(GeocognitionElement parent,
			GeocognitionElement element) {
		if (parent.removeElement(element)) {
			return element;
		} else {
			return null;
		}
	}

	boolean fireElementRemoving(GeocognitionElement element) {
		for (GeocognitionListener listener : listeners) {
			if (!listener.elementRemoving((this), element)) {
				return false;
			}
		}

		return true;
	}

	void fireElementRemoved(GeocognitionElement element) {
		for (GeocognitionListener listener : listeners) {
			listener.elementRemoved((this), element);
		}
	}

	@Override
	public GeocognitionElement getGeocognitionElement(String id) {
		if (root.getId().equals(id)) {
			return root;
		} else {
			return getElement(root, new Id(id));
		}
	}

	@Override
	public void addGeocognitionListener(GeocognitionListener listener) {
		(this).listeners.add(listener);
	}

	@Override
	public boolean removeGeocognitionListener(GeocognitionListener listener) {
		return listeners.remove(listener);
	}

	@Override
	public void addFolder(String id) {
		Id ident = new Id(id);
		FolderElement fe = new FolderElement(this);
		addInElement(ident, fe);
	}

	@Override
	public String getUniqueIdPath(String prefix) {
		int n = -1;
		String id;
		do {
			n++;
			id = prefix + n;
		} while (getElement(root, new Id(id)) != null);
		return id;
	}

	@Override
	public String getUniqueId(String prefix) {
		int n = -1;
		String id;
		do {
			n++;
			id = prefix + n;
		} while (exists(id));
		return id;
	}

	private boolean exists(final String id) {
		GeocognitionElement[] elems = getElements(new GeocognitionFilter() {

			@Override
			public boolean accept(GeocognitionElement element) {
				return element.getId().equals(id);
			}

		});

		return elems.length > 0;
	}

	@Override
	public void move(String id, String newParent) {
		GeocognitionElement toMove = getElement(root, new Id(id));
		if (toMove == null) {
			throw new IllegalArgumentException(
					"The specified element does not exist: " + id);
		}
		GeocognitionElement toMoveTo = getElement(root, new Id(newParent));
		if (toMoveTo == null) {
			throw new IllegalArgumentException(
					"The specified element does not exist: " + newParent);
		}
		if (toMoveTo.isFolder()) {
			GeocognitionElement oldParent = toMove.getParent();
			((FolderElement) oldParent).removeElement(toMove, false);
			try {
				((FolderElement) toMoveTo).addElement(toMove, false);
				fireElementMoved(toMove, oldParent);
			} catch (UnsupportedOperationException e) {
				oldParent.addElement(toMove);
				throw e;
			}
		} else {
			throw new UnsupportedOperationException("The specified "
					+ "destination does not accept children: " + newParent);
		}
	}

	@Override
	public GeocognitionElement[] getElements(
			GeocognitionFilter geocognitionFilter) {
		return getElements(root, geocognitionFilter).toArray(
				new GeocognitionElement[0]);
	}

	private ArrayList<GeocognitionElement> getElements(
			GeocognitionElement element, GeocognitionFilter filter) {
		ArrayList<GeocognitionElement> ret = new ArrayList<GeocognitionElement>();
		for (int i = 0; i < element.getElementCount(); i++) {
			GeocognitionElement child = element.getElement(i);
			if (child instanceof FolderElement) {
				ret.addAll(getElements(child, filter));
			} else {
				if (filter.accept(child)) {
					ret.add(child);
				}
			}

		}

		return ret;
	}

	@Override
	public GeocognitionElement createFolder(String id) {
		FolderElement ret = new FolderElement((this));
		ret.setId(id);
		return ret;
	}

	public static String getCognitionContextPath() {
		return "org.orbisgis.core.geocognition.persistence";
	}
}
