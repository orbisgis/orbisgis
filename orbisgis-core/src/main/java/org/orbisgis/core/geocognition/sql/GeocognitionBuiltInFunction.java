package org.orbisgis.core.geocognition.sql;

import java.util.HashMap;
import java.util.Map;

import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.core.geocognition.persistence.PropertySet;

public class GeocognitionBuiltInFunction extends AbstractBuiltInSQLArtifact
		implements GeocognitionExtensionElement {

	public GeocognitionBuiltInFunction(Class<? extends Function> functionClass,
			GeocognitionElementFactory factory) {
		super(functionClass, factory);
	}

	public GeocognitionBuiltInFunction(PropertySet properties,
			GeocognitionElementFactory factory) throws ClassNotFoundException {
		super(properties, factory);
	}

	@Override
	public String getTypeId() {
		return GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getFixedId() {
		try {
			return ((Class<? extends Function>) class_).newInstance().getName();
		} catch (InstantiationException e) {
			throw new RuntimeException("Bug!", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Bug!", e);
		}
	}
	
	@Override
	public Map<String, String> getProperties() {
		boolean registered = false;
		String functionName = getFixedId();
		Function function = FunctionManager.getFunction(functionName);
		registered = (function != null) && function.getClass().equals(class_);
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.put(REGISTERED, (registered) ? IS_REGISTERED : IS_NOT_REGISTERED);
		return ret;
	}


}
