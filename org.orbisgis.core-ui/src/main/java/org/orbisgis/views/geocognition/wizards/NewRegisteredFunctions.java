package org.orbisgis.views.geocognition.wizards;

import java.util.Map;

import javax.swing.ImageIcon;

import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.sql.GeocognitionBuiltInFunction;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.wizard.INewGeocognitionElement;

public class NewRegisteredFunctions extends AbstractRegisteredSQLArtifact
		implements INewGeocognitionElement {

	@Override
	public GeocognitionElementFactory getFactory() {
		return new GeocognitionFunctionFactory();
	}

	@Override
	public String getName() {
		return "Built-in function";
	}

	@Override
	protected String[] getArtifactNames() {
		return FunctionManager.getFunctionNames();
	}

	@Override
	protected String getName(Object sqlArtifact) {
		return ((Function) sqlArtifact).getName();
	}

	@Override
	protected ImageIcon getIcon(String contentTypeId,
			Map<String, String> properties) {
		if (GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID
				.equals(contentTypeId)) {
			String registered = properties
					.get(GeocognitionBuiltInFunction.REGISTERED);
			if ((registered != null)
					&& registered
							.equals(GeocognitionBuiltInFunction.IS_NOT_REGISTERED)) {
				return IconLoader.getIcon("builtInFunctionMapError.png");
			} else {
				return IconLoader.getIcon("builtInFunctionMap.png");
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean isUniqueIdRequired(int index) {
		return false;
	}

	@Override
	public String getBaseName() {
		return null;
	}

	@Override
	protected Class<?> getArtifact(String name) {
		return FunctionManager.getFunction(name).getClass();
	}
}
