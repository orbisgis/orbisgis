package org.orbisgis.geocognition.sql;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.edition.EditableElementException;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.java.AbstractJavaArtifact;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.geocognition.persistence.PropertySet;
import org.orbisgis.javaManager.CompilationException;
import org.orbisgis.javaManager.parser.ParseException;
import org.orbisgis.javaManager.parser.TokenMgrError;

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
public abstract class AbstractJavaSQLArtifact extends AbstractJavaArtifact
		implements GeocognitionExtensionElement {

	// persistence property name
	static final String PERSISTENCE_PROPERTY_NAME = "function-code";
	private String id;

	public AbstractJavaSQLArtifact(Code code, GeocognitionElementFactory factory) {
		super(code, factory);
	}

	public AbstractJavaSQLArtifact(PropertySet properties,
			GeocognitionElementFactory factory) throws ClassNotFoundException {
		super(properties, factory);
	}

	protected String getCodePropertyName() {
		return PERSISTENCE_PROPERTY_NAME;
	}

	@Override
	public void save() throws EditableElementException {
		super.save();
		publish();
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
		String nameChanged = changeName(newId);
		GetNameRefactoringVisitor gnrv = new GetNameRefactoringVisitor(
				nameChanged, "return \"" + newId + "\";");
		applyVisitor(gnrv, nameChanged);
		nameChanged = gnrv.getModifiedText();
		this.code.setCode(nameChanged);
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

}
