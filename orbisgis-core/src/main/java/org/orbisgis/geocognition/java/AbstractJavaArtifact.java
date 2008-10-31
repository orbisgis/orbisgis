package org.orbisgis.geocognition.java;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.orbisgis.edition.EditableElementException;
import org.orbisgis.geocognition.AbstractExtensionElement;
import org.orbisgis.geocognition.GeocognitionElementContentListener;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.PersistenceUtils;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.geocognition.persistence.Property;
import org.orbisgis.geocognition.persistence.PropertySet;
import org.orbisgis.geocognition.sql.Code;
import org.orbisgis.geocognition.sql.CodeListener;
import org.orbisgis.geocognition.sql.NameRefactoringVisitor;
import org.orbisgis.javaManager.autocompletion.AbstractVisitor;
import org.orbisgis.javaManager.parser.JavaParser;
import org.orbisgis.javaManager.parser.ParseException;
import org.orbisgis.javaManager.parser.TokenMgrError;
import org.orbisgis.progress.IProgressMonitor;

public abstract class AbstractJavaArtifact extends AbstractExtensionElement
		implements GeocognitionExtensionElement {
	// compiling properties
	public static final String COMPILE_RESULT = "COMPILE_RESULT";
	public static final String COMPILE_OK = "COMPILE_RESULT_OK";
	public static final String COMPILE_ERROR = "COMPILE_RESULT_ERROR";

	private PropertySet revertStatus;
	private GeocognitionElementContentListener elementListener;
	private CodeChangeListener codeListener;
	protected Code code;
	protected HashMap<String, String> properties = new HashMap<String, String>();

	public AbstractJavaArtifact(Code code, GeocognitionElementFactory factory) {
		super(factory);
		this.code = code;
	}

	public AbstractJavaArtifact(PropertySet properties,
			GeocognitionElementFactory factory) {
		super(factory);
		setJAXBObject(properties);
	}

	@Override
	public Object getJAXBObject() {
		Property codeProperty = PersistenceUtils.newProperty(
				getCodePropertyName(), code.getCode());
		Map<String, String> properties = getPersistentProperties();
		PropertySet ps = new PropertySet();
		if (properties != null) {
			Iterator<String> it = properties.keySet().iterator();
			while (it.hasNext()) {
				String propertyName = it.next();
				ps.getProperty().add(
						PersistenceUtils.newProperty(propertyName, properties
								.get(propertyName)));
			}
		}
		ps.getProperty().add(codeProperty);
		return ps;
	}

	/**
	 * Gets all the properties that have to be persistent apart of the code
	 * 
	 * @return
	 */
	protected abstract Map<String, String> getPersistentProperties();

	/**
	 * Set all the properties that were returned in
	 * {@link #getPersistentProperties()}
	 * 
	 * @param properties
	 */
	protected abstract void setPersistentProperties(
			Map<String, String> properties);

	/**
	 * Gets the name of the persistence property containing the java code
	 * 
	 * @return
	 */
	protected abstract String getCodePropertyName();

	/**
	 * Instantiates the subclass of code wrapped by this element
	 * 
	 * @param codeContent
	 * @return
	 */
	protected abstract Code instantiateJavaCode(String codeContent);

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return code;
	}

	@Override
	public void close(IProgressMonitor progressMonitor) {
		setJAXBObject(revertStatus);
		this.code.removeCodeListener(codeListener);
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
		this.revertStatus = (PropertySet) getJAXBObject();
		codeListener = new CodeChangeListener();
		this.code.addCodeListener(codeListener);
	}

	@Override
	public void save() throws EditableElementException {
		this.revertStatus = (PropertySet) getJAXBObject();
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public Object getRevertJAXBObject() {
		return revertStatus;
	}

	@Override
	public void setElementListener(GeocognitionElementContentListener listener) {
		this.elementListener = listener;
	}

	@Override
	public void setJAXBObject(Object jaxbObject) {
		PropertySet props = (PropertySet) jaxbObject;
		HashMap<String, String> properties = new HashMap<String, String>();
		List<Property> propList = props.getProperty();
		for (Property property : propList) {
			properties.put(property.getName(), property.getValue());
		}
		String codeContent = properties.get(getCodePropertyName());
		if (code == null) {
			code = instantiateJavaCode(codeContent);
		} else {
			code.setCode(codeContent);
		}
		setPersistentProperties(properties);
	}

	@Override
	public void idChanged(String newId) throws GeocognitionException {
		try {
			String newCode = changeName(newId);
			this.code.setCode(newCode);
		} catch (ParseException e) {
			e.printStackTrace();
			properties.put(COMPILE_RESULT, COMPILE_ERROR);
		} catch (TokenMgrError e) {
			properties.put(COMPILE_RESULT, COMPILE_ERROR);
		}
	}

	protected String changeName(String newId) throws ParseException {
		NameRefactoringVisitor nrv = new NameRefactoringVisitor(this.code
				.getCode(), newId);
		applyVisitor(nrv, this.code.getCode());
		return nrv.getModifiedText();
	}

	protected void applyVisitor(AbstractVisitor nrv, String javaCode)
			throws ParseException {
		ByteArrayInputStream bis = new ByteArrayInputStream(javaCode.getBytes());
		JavaParser jp = new JavaParser(bis);
		jp.CompilationUnit();
		jp.getRootNode().jjtAccept(nrv, null);
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}

	private class CodeChangeListener implements CodeListener {

		@Override
		public void codeChanged(Code code) {
			if (elementListener != null) {
				elementListener.contentChanged();
			}
		}

	}

}
