package org.orbisgis.geocognition.sql;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.edition.EditableElementException;
import org.orbisgis.geocognition.AbstractExtensionElement;
import org.orbisgis.geocognition.GeocognitionElementContentListener;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.geocognition.persistence.Property;
import org.orbisgis.geocognition.persistence.PropertySet;
import org.orbisgis.javaManager.CompilationException;
import org.orbisgis.javaManager.autocompletion.AbstractVisitor;
import org.orbisgis.javaManager.parser.JavaParser;
import org.orbisgis.javaManager.parser.ParseException;
import org.orbisgis.javaManager.parser.TokenMgrError;
import org.orbisgis.progress.IProgressMonitor;

/**
 * <p>
 * This element contains a base Java implementation for sql artifacts: functions
 * and custom queries. The new element will be registered in the respective
 * manager during the time it is on the geocognition and the code compiles, this
 * is, it will be registered when it is added to it and unregistered at removal.
 * If at any time an edition is done and the code no longer compiles, the latest
 * compiling version will remain registered. Consequently, if the initial code
 * does not compile the function won't be available.
 * </p>
 * <p>
 * The registration process removes any previous artifact registered with the
 * name this element has. Note that this can lead to the unregistration of a
 * built-it artifact and it's up to the user to take care about name collisions.
 * Typically, an hypothetical user interface will make checks before renaming
 * and creating this kind of elements
 * </p>
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public abstract class AbstractJavaSQLArtifact extends AbstractExtensionElement
		implements GeocognitionExtensionElement {

	// persistence property name
	static final String PERSISTENCE_PROPERTY_NAME = "function-code";

	// compiling properties
	public static final String COMPILE_RESULT = "COMPILE_RESULT";
	public static final String COMPILE_OK = "COMPILE_RESULT_OK";
	public static final String COMPILE_ERROR = "COMPILE_RESULT_ERROR";

	private Code code;
	private Property revertStatus;
	private GeocognitionElementContentListener elementListener;
	private CodeChangeListener codeListener;
	private String id;
	private HashMap<String, String> properties = new HashMap<String, String>();

	public AbstractJavaSQLArtifact(Code code, GeocognitionElementFactory factory) {
		super(factory);
		this.code = code;
	}

	public AbstractJavaSQLArtifact(PropertySet properties,
			GeocognitionElementFactory factory) throws ClassNotFoundException {
		super(factory);
		String codeContent = properties.getProperty().get(0).getValue();
		code = getJavaCode(codeContent);
	}

	protected abstract Code getJavaCode(String codeContent);

	@Override
	public Object getJAXBObject() {
		Property property = getProperty(code);
		return getPropertySet(property);
	}

	private Object getPropertySet(Property property) {
		PropertySet props = new PropertySet();
		props.getProperty().add(property);
		return props;
	}

	private Property getProperty(Code code) {
		Property property = new Property();
		property.setName(PERSISTENCE_PROPERTY_NAME);
		property.setValue(code.getCode());
		return property;
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return code;
	}

	@Override
	public void close(IProgressMonitor progressMonitor) {
		code.setCode(revertStatus.getValue());
		this.code.removeCodeListener(codeListener);
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
		this.revertStatus = getProperty(this.code);
		codeListener = new CodeChangeListener();
		this.code.addCodeListener(codeListener);
	}

	@Override
	public void save() throws EditableElementException {
		this.revertStatus = getProperty(this.code);
		publish();
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public Object getRevertJAXBObject() {
		return getPropertySet(revertStatus);
	}

	@Override
	public void setElementListener(GeocognitionElementContentListener listener) {
		this.elementListener = listener;
	}

	@Override
	public void setJAXBObject(Object jaxbObject)
			throws IllegalArgumentException, GeocognitionException {
		PropertySet props = (PropertySet) jaxbObject;
		Property property = props.getProperty().get(0);
		code.setCode(property.getValue());
	}

	private class CodeChangeListener implements CodeListener {

		@Override
		public void codeChanged(Code code) {
			if (elementListener != null) {
				elementListener.contentChanged();
			}
		}

	}

	@Override
	public void elementRemoved() {
		removeArtifact(id);
	}

	protected abstract void removeArtifact(String id);

	/**
	 * Check that there is no SQL registered artifact with that name, change the
	 * id, modify the code, register the new code in FunctionManager
	 * 
	 * @see org.orbisgis.geocognition.AbstractExtensionElement#idChanged(java.lang.String)
	 */
	@Override
	public void idChanged(String newId) throws GeocognitionException {
		if ((id != null)
				&& !newId.equals(id)
				&& ((FunctionManager.getFunction(newId) != null) || (QueryManager
						.getQuery(newId) != null))) {
			throw new GeocognitionException("Invalid id. There is already "
					+ "a function or custom query with that name: " + newId);
		}
		if (id != null) {
			removeArtifact(id);
		}
		this.id = newId;
		try {
			changeCode(newId);
			publish();
		} catch (ParseException e) {
			properties.put(COMPILE_RESULT, COMPILE_ERROR);
		} catch (TokenMgrError e) {
			properties.put(COMPILE_RESULT, COMPILE_ERROR);
		} catch (EditableElementException e) {
			properties.put(COMPILE_RESULT, COMPILE_ERROR);
		}
	}

	private void changeCode(String newId) throws ParseException {
		NameRefactoringVisitor nrv = new NameRefactoringVisitor(this.code
				.getCode(), newId);
		applyVisitor(nrv, this.code.getCode());
		String nameChanged = nrv.getModifiedText();
		GetNameRefactoringVisitor gnrv = new GetNameRefactoringVisitor(
				nameChanged, "return \"" + newId + "\";");
		applyVisitor(gnrv, nameChanged);
		nameChanged = gnrv.getModifiedText();
		this.code.setCode(nameChanged);
	}

	private void applyVisitor(AbstractVisitor nrv, String javaCode)
			throws ParseException {
		ByteArrayInputStream bis = new ByteArrayInputStream(javaCode.getBytes());
		JavaParser jp = new JavaParser(bis);
		jp.CompilationUnit();
		jp.getRootNode().jjtAccept(nrv, null);
	}

	private void publish() throws EditableElementException {
		try {
			changeCode(id);
			Class<?> cl = this.code.compile();

			if (cl != null) {
				removeArtifact(id);
				try {
					addArtifact(cl);
					properties.put(COMPILE_RESULT, COMPILE_OK);
				} catch (IllegalArgumentException e) {
					properties.put(COMPILE_RESULT, COMPILE_ERROR);
					throw new EditableElementException(
							"Invalid function implementation", e);
				}
			} else {
				properties.put(COMPILE_RESULT, COMPILE_ERROR);
				throw new EditableElementException("Error compiling the class");
			}
		} catch (CompilationException e) {
			properties.put(COMPILE_RESULT, COMPILE_ERROR);
			throw new EditableElementException("Compile error", e);
		} catch (ClassCastException e) {
			properties.put(COMPILE_RESULT, COMPILE_ERROR);
			throw new EditableElementException("The class must implement "
					+ getInterfaceName(), e);
		} catch (ParseException e) {
			properties.put(COMPILE_RESULT, COMPILE_ERROR);
			throw new EditableElementException("Cannot parse content", e);
		} catch (TokenMgrError e) {
			properties.put(COMPILE_RESULT, COMPILE_ERROR);
			throw new EditableElementException("Cannot parse content", e);
		}
	}

	protected abstract String getInterfaceName();

	/**
	 * Adds the specified function in the respective manager
	 * 
	 * @param cl
	 * @throws IllegalArgumentException
	 *             If the class does is not valid
	 */
	protected abstract void addArtifact(Class<?> cl)
			throws IllegalArgumentException;

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}

}
