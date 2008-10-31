package org.orbisgis.geocognition.sql;

import java.util.Map;

import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.persistence.PropertySet;

/**
 * Function java implementation
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public class GeocognitionJavaFunction extends AbstractJavaSQLArtifact implements
		GeocognitionExtensionElement {

	public GeocognitionJavaFunction(Code code,
			GeocognitionElementFactory factory) {
		super(code, factory);
	}

	public GeocognitionJavaFunction(PropertySet properties,
			GeocognitionElementFactory factory) throws ClassNotFoundException {
		super(properties, factory);
	}

	@Override
	public String getTypeId() {
		return GeocognitionFunctionFactory.JAVA_FUNCTION_ID;
	}

	@Override
	protected void removeArtifact(String id) {
		FunctionManager.remove(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addArtifact(Class<?> cl) {
		FunctionManager.addFunction((Class<? extends Function>) cl);
	}

	@Override
	protected Code instantiateJavaCode(String codeContent) {
		return new FunctionJavaCode(codeContent);
	}

	@Override
	protected String getInterfaceName() {
		return Function.class.getName();
	}

	@Override
	protected Map<String, String> getPersistentProperties() {
		return null;
	}

	@Override
	protected void setPersistentProperties(Map<String, String> properties) {
	}
}
